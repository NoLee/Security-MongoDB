/*Copyright (C) 2017  PENTARAKIS EMMANOUIL
test

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/

import java.security.*;
import java.security.spec.*;
import java.util.*;
import org.bson.Document;
import org.bson.types.Binary;
import com.mongodb.*;
import com.mongodb.client.*;

public class Database {
	
	private String uname = null;
	private String databaseName = null;
	
	public String getUname() {
		return uname;
	}	

	public Database(String uname, String databaseName) {
		super();
		this.uname = uname;
		this.databaseName = databaseName;
	}
	
	/**
	 * After obtaining credentials with "kinit" from command line, use Connect() to open a connection to a MongoDB instance
	 * @param name Username that has the Kerberosv5 credentials
	 * @return MongoClient connection to database
	 */
	public MongoClient Connect() throws Exception {
		//use kinit first to obtain credentials for kerberos
		MongoClient con = new MongoClient(new MongoClientURI("mongodb://"+uname+"%40TELECOM.ECE.NTUA.GR@plrf20.telecom.ece.ntua.gr/"));
        return con;
	}

	/**
	 * After obtaining credentials with "kinit" from command line, use Connect() to open a connection to a MongoDB instance
	 * @param name Username that has the Kerberosv5 credentials
	 * @return MongoClient connection to database
	 */
	public MongoClient Connect(String uname) throws Exception {
		//use kinit first to obtain credentials for kerberos
		MongoClient con = new MongoClient(new MongoClientURI("mongodb://"+uname+"%40TELECOM.ECE.NTUA.GR@plrf20.telecom.ece.ntua.gr/?authMechanism=GSSAPI&authSource=$external"));
        return con;
	}
	
	/**
	 * Stores the given public key to database from the connected user (used for DIGITAL SIGNATURES)
	 * @param m Mongo client obtained by the "Connect" method
	 * @param pk Public key to store in the database, used for Digital Signature
	 */
	public void storeSignaturePK(MongoClient m, PublicKey pk) {		
		MongoDatabase db = m.getDatabase(databaseName);
        MongoCollection<Document> c = db.getCollection("SignatureKeys");
        
		Document doc = new Document("Name", uname)
        		.append("PublicKey", pk.getEncoded()); 
        c.insertOne(doc);		
	}	
	
	/**
	 * Stores the given public key to database from the connected user (used for RSA CRYPTOGRAPHY)
	 * @param m Mongo client obtained by the "Connect" method
	 * @param pk Public key to store in the database, used for RSA cryptography
	 * @param AFM Unique number of person in the database
	 * @param priv Private key used to digitally sign the data
	 */
	public void storeRsaPK(MongoClient m, PublicKey pk, String AFM, PrivateKey priv) throws Exception {		
		MongoDatabase db = m.getDatabase(databaseName);
        MongoCollection<Document> c = db.getCollection( "BasicInfo");
        
        String key1 = "Public Key";
        Document doc = new Document(key1, pk.getEncoded()); 

    	byte[] signature = signDataPK(pk.getEncoded(), priv, AFM);  
    	//Create the signature field and values
    	//example "_Name" : ["diGiTaLS1gn4TuR3","NoLee"] 
    	doc.append("_"+key1, Arrays.asList(signature, uname));
    	Document doc2 = new Document("$set",doc);
        //doc to query, we expect one result		
  		Document queryDoc = new Document("AFM", AFM);
  		
    	c.updateOne(queryDoc, doc2);		
	}	
	
	
	/** 
	 * Gets the public key for the desired username.
	 * @param m Mongo client obtained by the "Connect" method
	 * @param username Username of the author of the public key
	 * @return Public key for the selected user from the database (Digital signatures)
	 */
	public PublicKey getSignaturePK (MongoClient m, String username) throws Exception {		
		MongoDatabase db = m.getDatabase(databaseName);
        MongoCollection<Document> c = db.getCollection( "SignatureKeys");
        
        //get the binary public key from Database, we expect one result		
		Document doc = new Document("Name", username);		
		MongoCursor<Document> cursor = c.find(doc).iterator();
		
        Binary binKey=null;
        try {
        	binKey = (Binary) cursor.next().get("PublicKey");
        } finally {
        	cursor.close();
        }	        
        
        //make a byte array from binary public key
        byte[] encKey = new byte[binKey.length()];
        encKey = binKey.getData();
        
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
    	
	    KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
	    PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
	    
		return pubKey;		
	}
	
	/** 
	 * Gets the public key for the desired username.
	 * @param m Mongo client obtained by the "Connect" method
	 * @param username Username of the author of the public key
	 * @return Public key for the selected user from the database (RSA cryptography)
	 */
	public PublicKey getRsaPK (MongoClient m, String AFM) throws Exception {		
		MongoDatabase db = m.getDatabase(databaseName);
        MongoCollection<Document> c = db.getCollection("BasicInfo");
        
        //get the binary public key from Database, we expect one result		
        Document doc = new Document("AFM", AFM);		
		MongoCursor<Document> cursor = c.find(doc).iterator();
		
        Binary binKey=null;
        try {
        	binKey = (Binary) cursor.next().get("Public Key");
        } finally {
        	cursor.close();
        }	        
        
        //make a byte array from binary public key
        byte[] encKey = new byte[binKey.length()];
        encKey = binKey.getData();
        
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
    	
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
	    
		return pubKey;		
	}
	
	/**
	 * This method is used to insert a new user to the database, must have AFM
	 * @param m Mongo client obtained by the "Connect" method
	 * @param AFM is the unique tax number for each person
	 * @param priv Private key used for Digital Signature
	 */
	public void myInsert(MongoClient m, String AFM, PrivateKey priv) throws Exception {		
		MongoDatabase db = m.getDatabase(databaseName);
		//TODO change the collection name
        MongoCollection<Document> c = db.getCollection( "BasicInfo");
        
        Document doc = new Document("AFM", AFM);
        
    	byte[] signature = signData(AFM,priv,AFM);  
    	//Create the signature field and values
    	//example "_Name" : ["diGiTaLS1gn4TuR3","NoLee"] 
    	doc.append("_AFM", Arrays.asList(signature, uname));    	
        c.insertOne(doc);		
	}	
	
	/**
	 * This method is used to update or insert into a document data to the selected database.
	 * @param AFM is the unique tax number for each person
	 * @param m Mongo client obtained by the "Connect" method
	 * @param doc Must contain one field and one value (Strings)
	 * @param priv Private key used for Digital Signature
	 */
	public void myUpdate(String AFM,MongoClient m, Document doc, PrivateKey priv) throws Exception {		
		MongoDatabase db = m.getDatabase(databaseName);
		//TODO change the collection name
        MongoCollection<Document> c = db.getCollection( "BasicInfo");
          
        //Document has one key and one data	
        //Get the key and data
        Set<String> set = doc.keySet();    	
    	Iterator<String> iter = set.iterator();
	    String key1 = iter.next();
	    String data1 = doc.getString(key1);

    	byte[] signature = signData(data1,priv,AFM);  
    	//Create the signature field and values
    	//example "_Name" : ["diGiTaLS1gn4TuR3","NoLee"] 
    	doc.append("_"+key1, Arrays.asList(signature, uname));
    	Document doc2 = new Document("$set",doc);
        //doc to query, we expect one result		
  		Document queryDoc = new Document("AFM", AFM);
  		
    	c.updateOne(queryDoc, doc2);
        //c.insertOne(doc);		
	}
	
	public void myUpdatePK(String AFM,MongoClient m, Document doc, PrivateKey priv) throws Exception {		
		MongoDatabase db = m.getDatabase(databaseName);
		//TODO change the collection name
        MongoCollection<Document> c = db.getCollection( "BasicInfo");
          
        //Document has one key and one data	
        //Get the key and data
        Set<String> set = doc.keySet();    	
    	Iterator<String> iter = set.iterator();
	    String key1 = iter.next();
	    byte[] data1 =  (byte[]) doc.get(key1);

/*        //make a byte array from binary object
        byte[] data = new byte[data1.length()];
        data = data1.getData();*/
        
    	byte[] signature = signDataPK(data1,priv,AFM);  
    	//Create the signature field and values
    	//example "_Name" : ["diGiTaLS1gn4TuR3","NoLee"] 
    	doc.append("_"+key1, Arrays.asList(signature, uname));
    	Document doc2 = new Document("$set",doc);
        //doc to query, we expect one result		
  		Document queryDoc = new Document("AFM", AFM);
  		
    	c.updateOne(queryDoc, doc2);
        //c.insertOne(doc);		
	}
	
	/**
	 * Uses the provided private key to sign the provided data for the Digital Signature procedure
	 * @param data String with data to be signed
	 * @param priv Private key used for Digital Signature
	 * @param salt Salt used in Digital Signature so you cannot copy both signature and data, usually is the AFM of the person the data concerns
	 * @return a byte array containing the Digital Signature data
	 */
	private byte[] signData (String data, PrivateKey priv, String salt) throws Exception{
		
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
    	dsa.initSign(priv);
    	
    	String saltedData = data + ":" + salt;
    	
    	byte[] dataB = saltedData.getBytes();
    	
    	dsa.update(dataB);
    	
    	byte[] realSig = dsa.sign();
    	
    	return realSig;    
	}
	
	private byte[] signDataPK (byte[] data, PrivateKey priv, String salt) throws Exception{
		
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
    	dsa.initSign(priv);
    	salt = ":"+salt;
    	byte[] saltB = salt.getBytes();
    	byte[] dataB = new byte[data.length + saltB.length];

    	for (int i = 0; i < dataB.length; ++i)
    	{
    	    dataB[i] = i < data.length ? data[i] : saltB[i - data.length];
    	}
    	
    	dsa.update(dataB);
    	
    	byte[] realSig = dsa.sign();
    	
    	return realSig;    
	}
	
	/**
	 * Validates the Digital signature for provided data.
	 * @param m Mongo client obtained by the "Connect" method
	 * @param data String with data to be validated
	 * @param signature Signature to be validated
	 * @param uname Username of the person that signed the data
	 * @return the result of the validation, true or false
	 */
	public boolean validate (MongoClient m,String data,byte[] signature,String username) throws Exception {
		/* get public key from database */
		PublicKey pubKey = getSignaturePK(m,username);
		
		/* create a Signature object and initialize it with the public key */
		Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
		sig.initVerify(pubKey);
		
		/* Update and verify the data */
		sig.update(data.getBytes());
		
		boolean verifies = sig.verify(signature);
		
		System.out.println("signature verifies: " + verifies);
		return verifies;
	}
	
	public boolean validatePK (MongoClient m,byte[] data,byte[] signature,String username) throws Exception {
		/* get public key from database */
		PublicKey pubKey = getSignaturePK(m,username);
		
		/* create a Signature object and initialize it with the public key */
		Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
		sig.initVerify(pubKey);
		
		/* Update and verify the data */
		sig.update(data);
		
		boolean verifies = sig.verify(signature);
		
		System.out.println("signature verifies: " + verifies);
		return verifies;
	}

}



