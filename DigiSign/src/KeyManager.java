/*Copyright (C) 2017  PENTARAKIS EMMANOUIL


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

import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.Cipher;

import com.mongodb.MongoClient;


public class KeyManager {
	
	private final String ALGORITHM = "RSA";
		
	public KeyPair createKeyPairs () throws NoSuchAlgorithmException, NoSuchProviderException {		
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
	
		KeyPair pair = keyGen.generateKeyPair();
		return pair;
	}
	
	public KeyPair createRSAkeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        // 512 is keysize
        keyGen.initialize(1024, random);

        KeyPair generateKeyPair = keyGen.generateKeyPair();
        return generateKeyPair;
    }

	public void saveKeysToFile (KeyPair keys, String uname) throws IOException {
			
		PrivateKey priv = keys.getPrivate();
		PublicKey pub = keys.getPublic();
		
		/* save the public key in a file */
		byte[] key = pub.getEncoded();
		FileOutputStream keyfos = new FileOutputStream(uname+"Pub");
		keyfos.write(key);
		keyfos.close();        	
		
		/* save the private key in a file */
		byte[] key2 = priv.getEncoded();
		FileOutputStream key2fos = new FileOutputStream(uname+"Priv");
		key2fos.write(key2);
		key2fos.close();
	}
	
	public void saveKeysToFileRSA (KeyPair keys, String uname) throws IOException {
		
		PrivateKey priv = keys.getPrivate();
		PublicKey pub = keys.getPublic();
		
		/* save the public key in a file */
		byte[] key = pub.getEncoded();
		FileOutputStream keyfos = new FileOutputStream(uname+"RSAPub");
		keyfos.write(key);
		keyfos.close();        	
		
		/* save the private key in a file */
		byte[] key2 = priv.getEncoded();
		FileOutputStream key2fos = new FileOutputStream(uname+"RSAPriv");
		key2fos.write(key2);
		key2fos.close();
	}
	
	public PublicKey getPublicKey (String uname) throws Exception {
		
	    FileInputStream keyfis = new FileInputStream(uname+"Pub");
	    byte[] encKey = new byte[keyfis.available()];  
	    keyfis.read(encKey);
	    keyfis.close();
	
	    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
	
	    KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
	    PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
	    
	    return pubKey;
	}
	
	
	public PrivateKey getPrivateKey (String uname) throws Exception {
		
		FileInputStream keyfis = new FileInputStream(uname+"Priv");
		byte[] encKey = new byte[keyfis.available()];
		keyfis.read(encKey);
		keyfis.close();
	
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encKey);
	
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);
		
		return privKey;
	}
    
	
	public PublicKey getPublicKeyRSA (String uname) throws Exception {
		
	    FileInputStream keyfis = new FileInputStream(uname+"RSAPub");
	    byte[] encKey = new byte[keyfis.available()];  
	    keyfis.read(encKey);
	    keyfis.close();
	   
	    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
	
	    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
	    PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
	    
	    return pubKey;
	}
	
	
	public PrivateKey getPrivateKeyRSA (String uname) throws Exception {
		
		FileInputStream keyfis = new FileInputStream(uname+"RSAPriv");
		byte[] encKey = new byte[keyfis.available()];
		keyfis.read(encKey);
		keyfis.close();
	
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encKey);
	
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		PrivateKey privKey = keyFactory.generatePrivate(privKeySpec);
		
		return privKey;
	}
	
    
	public byte[] encryptRSA(PublicKey key, String inputData) throws Exception {
		
		byte[] inputDataB = inputData.getBytes();
	    //PublicKey key = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));
	
	    Cipher cipher = Cipher.getInstance(ALGORITHM);
	    cipher.init(Cipher.PUBLIC_KEY, key);
	
	    byte[] encryptedBytes = cipher.doFinal(inputDataB);
	
	    return encryptedBytes;
	}
	
	public byte[] decryptRSA(PrivateKey key, byte[] inputData) throws Exception {
	
	    //PrivateKey key = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));
	
	    Cipher cipher = Cipher.getInstance(ALGORITHM);
	    cipher.init(Cipher.PRIVATE_KEY, key);
	
	    byte[] decryptedBytes = cipher.doFinal(inputData);
	
	    return decryptedBytes;
	}
	


	
	//------------------------------------------------------------------------------------------
	//-----------------------------------TO DELETE ---------------------------------------------
	//------------------------------------------------------------------------------------------
	
	public byte[] signData (String data, PrivateKey priv) throws Exception, NoSuchProviderException{
		
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
    	dsa.initSign(priv);
    	
    	byte[] dataB = data.getBytes();
    	
    	dsa.update(dataB);
    	
    	byte[] realSig = dsa.sign();
    	
    	return realSig;
	}	
	
	public boolean validateString (PublicKey pubKey,byte[] sigToVerify, String data) throws Exception {
		
	     /* create a Signature object and initialize it with the public key */
	     Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
	     sig.initVerify(pubKey);
	
	     /* Update and verify the data */
	
	     byte[] dataB = data.getBytes();
	     sig.update(dataB);

	     boolean verifies = sig.verify(sigToVerify);
	
	     System.out.println("signature verifies: " + verifies);
		 return verifies;
	}
	
}
