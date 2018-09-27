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


import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;

import org.bson.Document;
import org.bson.types.Binary;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.awt.Component;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Font;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

public class MainGUI {

	private JFrame frame;	
	private Database connector;
	private MongoClient mClient ;
	private String databaseName = "HealthCare";
	private KeyManager keyManager = new KeyManager();	
	
	byte[] nameEn;
	byte[] surEn;
	byte[] ageEn;
	byte[] illEn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		

		frame = new JFrame("HealthCare Manager v0.3");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 434, 261);
		frame.getContentPane().add(panel);
		panel.setLayout(new CardLayout(0, 0));
		
		JPanel login_panel = new JPanel();
		panel.add(login_panel, "login_panel");
		login_panel.setLayout(null);
		
		JLabel inserunameLB = new JLabel("Insert your username to login. Make sure you kinit before connecting.");
		inserunameLB.setHorizontalAlignment(SwingConstants.CENTER);
		inserunameLB.setBounds(10, 37, 414, 38);
		login_panel.add(inserunameLB);
		
		JLabel unameLB = new JLabel("Username:");
		unameLB.setHorizontalAlignment(SwingConstants.RIGHT);
		unameLB.setBounds(93, 118, 75, 14);
		login_panel.add(unameLB);
		
		JTextField unameTXT = new JTextField();
		unameTXT.setBounds(185, 115, 135, 20);
		login_panel.add(unameTXT);
		unameTXT.setColumns(10);
		JLabel lblUser = new JLabel("hello ");
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    connector = new Database(unameTXT.getText(),databaseName);
			    try {
					mClient = connector.Connect();
				} catch (Exception e1) {
					//Auto-generated catch block
					e1.printStackTrace();
				}
			    //System.out.println(unameTXT.getText() + " " + databaseName);
				CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "home_panel");
			    //Set hello message for next panel
			    lblUser.setText("Hello "+unameTXT.getText());
			}
		});
		btnLogin.setBounds(82, 187, 89, 23);
		login_panel.add(btnLogin);
		
		JButton btnCreateNew = new JButton("Create new & Login");
		btnCreateNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//KeyManager km = new KeyManager();
				connector = new Database(unameTXT.getText(),databaseName);
				try {
					KeyPair keys = keyManager.createKeyPairs();
					keyManager.saveKeysToFile(keys, unameTXT.getText());
					mClient = connector.Connect();
					connector.storeSignaturePK(mClient, keys.getPublic());
				} catch (Exception e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println(unameTXT.getText() + " " + databaseName);
				CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "home_panel");
			    //Set hello message for next panel
			    lblUser.setText("Hello "+unameTXT.getText());
				
			}
		});
		btnCreateNew.setBounds(193, 187, 151, 23);
		login_panel.add(btnCreateNew);
		
		JPanel home_panel = new JPanel();
		panel.add(home_panel, "home_panel");
		home_panel.setLayout(null);
		
		JLabel lblPleaseSelectThe = new JLabel("Select an action");
		lblPleaseSelectThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseSelectThe.setBounds(75, 62, 277, 14);
		home_panel.add(lblPleaseSelectThe);
						
		lblUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblUser.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblUser.setBounds(75, 0, 277, 50);
		home_panel.add(lblUser);
		
		JButton btnInsertNewUser = new JButton("Insert new user");
		btnInsertNewUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "insert_panel");
			}
		});
		btnInsertNewUser.setBounds(61, 109, 129, 50);
		home_panel.add(btnInsertNewUser);
		
		JButton btnUpdateUser = new JButton("Update user");
		btnUpdateUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "update_panel");
			}
		});
		btnUpdateUser.setBounds(233, 109, 129, 50);
		home_panel.add(btnUpdateUser);
		
		JButton btnViewUsers = new JButton("View users");
		btnViewUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "users_panel");
			}
		});
		btnViewUsers.setBounds(147, 181, 129, 50);
		home_panel.add(btnViewUsers);
		
		JPanel insert_panel = new JPanel();
		panel.add(insert_panel, "insert_panel");
		insert_panel.setLayout(null);
		
		JLabel label = new JLabel("Name :");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(10, 81, 72, 14);
		insert_panel.add(label);
		
		JTextField insName = new JTextField();
		insName.setColumns(10);
		insName.setBounds(92, 78, 116, 20);
		insert_panel.add(insName);
		
		JComboBox<String> insAgeCB = new JComboBox<String>();
		insAgeCB.setModel(new DefaultComboBoxModel<String>(new String[] {"<12", "12-18", "18-24", "24-30", "30-45", "45+"}));
		insAgeCB.setBounds(92, 131, 116, 20);
		insert_panel.add(insAgeCB);
		
		JLabel lblAge = new JLabel("Age :");
		lblAge.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAge.setBounds(10, 132, 72, 19);
		insert_panel.add(lblAge);
		
		JTextField insIll = new JTextField();
		insIll.setColumns(10);
		insIll.setBounds(92, 162, 116, 20);
		insert_panel.add(insIll);
		
		JLabel lblIlness = new JLabel("Illness :");
		lblIlness.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIlness.setBounds(10, 162, 72, 20);
		insert_panel.add(lblIlness);
		
		JLabel lblAfm = new JLabel("AFM :");
		lblAfm.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAfm.setBounds(10, 53, 72, 14);
		insert_panel.add(lblAfm);
		
		JLabel lblSurname = new JLabel("Surname :");
		lblSurname.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSurname.setBounds(10, 106, 72, 15);
		insert_panel.add(lblSurname);
		
		JTextField insSur = new JTextField();
		insSur.setColumns(10);
		insSur.setBounds(92, 103, 116, 20);
		insert_panel.add(insSur);
		
		JTextField insAFM = new JTextField();
		insAFM.setColumns(10);
		insAFM.setBounds(92, 50, 116, 20);
		insert_panel.add(insAFM);
		
		JLabel lblPublicKey = new JLabel("Public Key :");
		lblPublicKey.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPublicKey.setBounds(10, 191, 72, 20);
		insert_panel.add(lblPublicKey);
		
		JTextField insPK = new JTextField();
		insPK.setEditable(false);
		insPK.setColumns(10);
		insPK.setBounds(92, 191, 233, 20);
		insert_panel.add(insPK);
		
		JButton btnInsert = new JButton("Insert");
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        PrivateKey priv = null;
		        
		        //if AFM or public key is empty, then don't insert it
		        if (insAFM.getText().isEmpty() || insPK.getText().isEmpty() )
		        {
		        	JOptionPane.showMessageDialog(frame,"AFM, Name and Public Key cannot be empty","ERROR",JOptionPane.ERROR_MESSAGE);
		        }
		        else
		        {
				    // Document doc = new Document("AFM", "0123456789");
			        String AFM = insAFM.getText();
					try {
						priv = keyManager.getPrivateKey(unameTXT.getText());
						//insert the data (user is logged in as uname)
						connector.myInsert(mClient,AFM,priv);					
						//insert the rest of data with update commands
						update(insAFM.getText(),insName.getText(), insSur.getText(), insAgeCB.getSelectedItem().toString(), insIll.getText(),keyManager.getPublicKeyRSA(insName.getText()),priv);
						
						JOptionPane.showMessageDialog(frame,"User added successfully.","User inserted",JOptionPane.PLAIN_MESSAGE);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
		        }
			}
		});
		btnInsert.setBounds(335, 227, 89, 23);
		insert_panel.add(btnInsert);
		
		JButton back_admin = new JButton("Back");
		back_admin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insAFM.setText(null);
				insName.setText(null); 
		        insSur.setText(null);
		        insAgeCB.setSelectedItem("<12");
		        insIll.setText(null);	    
		        insPK.setText(null);	   
			    CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "home_panel");
			}
		});
		back_admin.setBounds(10, 227, 89, 23);
		insert_panel.add(back_admin);
		
		JLabel lblInsertInformationFor = new JLabel("Insert information for new user.");
		lblInsertInformationFor.setBounds(40, 28, 367, 14);
		insert_panel.add(lblInsertInformationFor);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (insName.getText().isEmpty())
				{
					JOptionPane.showMessageDialog(frame,"Please insert a Name to create his personal Public Key","ERROR",JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					try {
						KeyPair kp = keyManager.createRSAkeyPair();
						keyManager.saveKeysToFileRSA(kp, insName.getText());
				        insPK.setText("Generated and added to SmartCard");
					} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		btnGenerate.setBounds(335, 187, 89, 23);
		insert_panel.add(btnGenerate);
		
		JPanel update_panel = new JPanel();
		update_panel.setLayout(null);
		panel.add(update_panel, "update_panel");		
				
		JLabel label_1 = new JLabel("AFM :");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(10, 36, 72, 14);
		update_panel.add(label_1);
		
		JTextField upAFM = new JTextField();
		upAFM.setColumns(10);
		upAFM.setBounds(92, 33, 116, 20);
		update_panel.add(upAFM);		
		
		JTextField upName = new JTextField();
		upName.setEnabled(false);
		upName.setColumns(10);
		upName.setBounds(92, 80, 116, 20);
		update_panel.add(upName);
		
		JLabel label_2 = new JLabel("Name :");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(10, 83, 72, 14);
		update_panel.add(label_2);
		
		JLabel label_3 = new JLabel("Surname :");
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		label_3.setBounds(10, 108, 72, 14);
		update_panel.add(label_3);
		
		JTextField upSur = new JTextField();
		upSur.setEnabled(false);
		upSur.setColumns(10);
		upSur.setBounds(92, 105, 116, 20);
		update_panel.add(upSur);
		
		JComboBox<String> upAgeCB = new JComboBox<String>();
		upAgeCB.setModel(new DefaultComboBoxModel<String>(new String[] {"<12", "12-18", "18-24", "24-30", "30-45", "45+"}));
		upAgeCB.setEnabled(false);
		upAgeCB.setBounds(92, 133, 116, 20);
		update_panel.add(upAgeCB);
		
		JLabel label_4 = new JLabel("Age :");
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		label_4.setBounds(10, 134, 72, 14);
		update_panel.add(label_4);
		
		JTextField upIll = new JTextField();
		upIll.setEnabled(false);
		upIll.setColumns(10);
		upIll.setBounds(92, 160, 116, 20);
		update_panel.add(upIll);
		
		JLabel lblIllness = new JLabel("Illness :");
		lblIllness.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIllness.setBounds(10, 160, 72, 14);
		update_panel.add(lblIllness);		
		
		JCheckBox nameChBx = new JCheckBox("");
		nameChBx.setEnabled(false);
		nameChBx.setBounds(236, 77, 21, 23);
		nameChBx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//when chechbox is selected, enable the field so we can update
				upName.setEnabled(nameChBx.isSelected());
				//can no longer change AFM
				upAFM.setEnabled(false);
			}
		});
		update_panel.add(nameChBx);
		
		JCheckBox surChBx = new JCheckBox("");
		surChBx.setEnabled(false);
		surChBx.setBounds(236, 102, 21, 23);
		surChBx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upSur.setEnabled(surChBx.isSelected());
				upAFM.setEnabled(false);
			}
		});
		update_panel.add(surChBx);
		
		JCheckBox ageChBx = new JCheckBox("");
		ageChBx.setEnabled(false);
		ageChBx.setBounds(236, 130, 21, 23);
		ageChBx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upAgeCB.setEnabled(ageChBx.isSelected());
				upAFM.setEnabled(false);
			}
		});
		update_panel.add(ageChBx);
		
		JCheckBox illChBx = new JCheckBox("");
		illChBx.setEnabled(false);
		illChBx.setBounds(236, 157, 21, 23);
		illChBx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upIll.setEnabled(illChBx.isSelected());
				upAFM.setEnabled(false);
			}
		});
		update_panel.add(illChBx);		
		
		
		
		JLabel lblSelectAUser = new JLabel("Select a user to view details and update.");
		lblSelectAUser.setBounds(34, 11, 240, 14);
		update_panel.add(lblSelectAUser);
		
		JLabel label_5 = new JLabel("Public Key :");
		label_5.setHorizontalAlignment(SwingConstants.RIGHT);
		label_5.setBounds(10, 185, 72, 20);
		update_panel.add(label_5);
		
		JTextField upPK = new JTextField();
		upPK.setEnabled(false);
		upPK.setColumns(10);
		upPK.setBounds(92, 185, 233, 20);
		update_panel.add(upPK);
		
		JCheckBox nameChBxEn = new JCheckBox("");
		nameChBxEn.setEnabled(false);
		nameChBxEn.setBounds(296, 77, 21, 23);
		update_panel.add(nameChBxEn);
		
		JCheckBox surChBxEn = new JCheckBox("");
		surChBxEn.setEnabled(false);
		surChBxEn.setBounds(296, 102, 21, 23);
		update_panel.add(surChBxEn);
		
		JCheckBox ageChBxEn = new JCheckBox("");
		ageChBxEn.setEnabled(false);
		ageChBxEn.setBounds(296, 130, 21, 23);
		update_panel.add(ageChBxEn);
		
		JCheckBox illChBxEn = new JCheckBox("");
		illChBxEn.setEnabled(false);
		illChBxEn.setBounds(296, 157, 21, 23);
		update_panel.add(illChBxEn);
		
		JLabel lblUpdate = new JLabel("Update");
		lblUpdate.setHorizontalAlignment(SwingConstants.CENTER);
		lblUpdate.setBounds(213, 59, 66, 20);
		update_panel.add(lblUpdate);
		
		JLabel lblEncrypt = new JLabel("Encrypt");
		lblEncrypt.setHorizontalAlignment(SwingConstants.CENTER);
		lblEncrypt.setBounds(273, 59, 66, 20);
		update_panel.add(lblEncrypt);
		
		JTextField upSC = new JTextField();
		upSC.setColumns(10);
		upSC.setBounds(337, 84, 87, 20);
		update_panel.add(upSC);
		
		JLabel lblSmartcard = new JLabel("SmartCard :");
		lblSmartcard.setHorizontalAlignment(SwingConstants.CENTER);
		lblSmartcard.setBounds(335, 59, 89, 20);
		update_panel.add(lblSmartcard);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					        
				//KeyManager km = new KeyManager();
		        PrivateKey priv = null;
		        
			    // Document doc = new Document("AFM", "0123456789");
				try {
					priv = keyManager.getPrivateKey(unameTXT.getText());
					
					//insert the rest of data with update commands					
					String AFM = upAFM.getText();					
					
					try {
						if (upName.isEnabled()) 
						{
							
							if (nameChBxEn.isSelected()) //update with encryption
							{
								Document doc1 = new Document("Name", keyManager.encryptRSA(connector.getRsaPK(mClient, upAFM.getText()), upName.getText()) );
								connector.myUpdatePK(AFM, mClient, doc1, priv);
							}
							else //update without encryption
							{
								Document doc1 = new Document("Name", upName.getText());
								connector.myUpdate(AFM, mClient, doc1, priv);
							}
						}
						if (upSur.isEnabled()) 
						{
							if (surChBxEn.isSelected())
							{
								Document doc2 = new Document("Surname",keyManager.encryptRSA(connector.getRsaPK(mClient, upAFM.getText()), upSur.getText()));
								connector.myUpdatePK(AFM, mClient, doc2, priv);
							}
							else
							{
								Document doc2 = new Document("Surname", upSur.getText());
								connector.myUpdate(AFM, mClient, doc2, priv);
							}							
						}
						if (upAgeCB.isEnabled())
						{													
							if (ageChBxEn.isSelected())
							{
								Document doc3 = new Document("Age",  keyManager.encryptRSA(connector.getRsaPK(mClient, upAFM.getText()), upAgeCB.getSelectedItem().toString()));	
								connector.myUpdatePK(AFM, mClient, doc3, priv);
							}
							else
							{
								Document doc3 = new Document("Age",upAgeCB.getSelectedItem().toString());
								connector.myUpdate(AFM, mClient, doc3, priv);
							}							
						}
						if (upIll.isEnabled()) 
						{							
							if (illChBxEn.isSelected())
							{
								Document doc4 = new Document("Illness",keyManager.encryptRSA(connector.getRsaPK(mClient, upAFM.getText()),  upIll.getText()) );
								connector.myUpdatePK(AFM, mClient, doc4, priv);
							}
							else
							{
								Document doc4 = new Document("Illness", upIll.getText() );
								connector.myUpdate(AFM, mClient, doc4, priv);
							}
							
						}
					} catch (Exception e1) {
						// Auto-generated catch block
						e1.printStackTrace();
					}
					
					JOptionPane.showMessageDialog(frame, "User updated successfully.");
				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}
		});
		btnUpdate.setBounds(335, 227, 89, 23);
		update_panel.add(btnUpdate);
		
		JButton btnFind = new JButton("Find");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MongoDatabase db = mClient.getDatabase("HealthCare");
		        MongoCollection<Document> c = db.getCollection( "testGUI");
		        
		        //query the database for one document
		        Document doc = new Document("AFM", upAFM.getText());
		        MongoCursor<Document> cursor  = c.find(doc).iterator();
		        
		        if (cursor.hasNext())
		        {
			        Document doc1 = cursor.next();
			        if (doc1.get("Name") instanceof Binary)
			        {
			        	upName.setText("Encrypted");
			        }
			        else
			        {
			        	upName.setText(doc1.getString("Name"));
			        }

			        if (doc1.get("Surname") instanceof Binary)
			        {
			        	upSur.setText("Encrypted");
			        }
			        else
			        {
			        	upSur.setText(doc1.getString("Surname"));
			        }
			        
			        if (doc1.get("Age") instanceof Binary)
			        {
			        	upAgeCB.addItem("Encrypted");
			        	upAgeCB.setSelectedItem("Encrypted");
			        }
			        else
			        {
			        	upAgeCB.setSelectedItem(doc1.getString("Age"));
			        }
			        
			        if (doc1.get("Illness") instanceof Binary)
			        {
			        	upIll.setText("Encrypted");
			        }
			        else
			        {
			        	upIll.setText(doc1.getString("Illness"));
			        }
			        
			        if (doc1.get("Public Key") instanceof Binary)
			        {
			        	upPK.setText("Has Smart Card");
			        }
			        else
			        {
			        	upPK.setText("Does not has Smart Card");
			        }
			        
			        //upPK.setText(doc1.getString("Public Key"));	    
			        //upIll.setEnabled(true);
			        nameChBx.setEnabled(true);
			        surChBx.setEnabled(true);
			        ageChBx.setEnabled(true);
			        illChBx.setEnabled(true);
			        
			        nameChBxEn.setEnabled(true);
			        surChBxEn.setEnabled(true);
			        ageChBxEn.setEnabled(true);
			        illChBxEn.setEnabled(true);
		        }
		        else
		        {
		        	JOptionPane.showMessageDialog(frame,"No users found with this AFM","User not found",JOptionPane.ERROR_MESSAGE);
		        }
		        
/*		        String surname = doc1.getString("Surname");
		        String age = doc1.getString("Age");
		        String illness = doc1.getString("Illness");*/
			}
		});
		btnFind.setBounds(236, 32, 89, 23);
		update_panel.add(btnFind);
		
		JButton button_1 = new JButton("Back");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upAFM.setText(null);
				upAFM.setEnabled(true);
				upName.setText(null); 
		        upName.setEnabled(false);
		        upSur.setText(null);
		        upSur.setEnabled(false);
		        upAgeCB.setSelectedItem("<12");
		        upAgeCB.removeItem("Encrypted");
		        upAgeCB.setEnabled(false);
		        upIll.setText(null);	    
		        upIll.setEnabled(false);
		        upSC.setText(null);	    
		        
		        nameChBx.setSelected(false);
		        surChBx.setSelected(false);
		        ageChBx.setSelected(false);
		        illChBx.setSelected(false);
		        
		        nameChBxEn.setSelected(false);
		        surChBxEn.setSelected(false);
		        ageChBxEn.setSelected(false);
		        illChBxEn.setSelected(false);
		        
			    CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "home_panel");
			}
		});
		button_1.setBounds(10, 227, 89, 23);
		update_panel.add(button_1);
		
		JButton btnDecrypt = new JButton("Decrypt");
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MongoDatabase db = mClient.getDatabase("HealthCare");
		        MongoCollection<Document> c = db.getCollection( "testGUI");
		        
		        //query the database for one document
		        Document doc = new Document("AFM", upAFM.getText());
		        MongoCursor<Document> cursor  = c.find(doc).iterator();
		        
		        if (cursor.hasNext())
		        {
			        Document doc1 = cursor.next();
			        if (doc1.get("Name") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Name");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							upName.setText(new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(upSC.getText()), data)) );
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
			        
			        if (doc1.get("Surname") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Surname");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							upSur.setText(new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(upSC.getText()), data)) );
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
			        
			        if (doc1.get("Age") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Age");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							upAgeCB.setSelectedItem(new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(upSC.getText()), data)) );
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
			        
			        if (doc1.get("Illness") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Illness");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							upIll.setText(new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(upSC.getText()), data)) );
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
		        }
			}
		});
		btnDecrypt.setBounds(335, 112, 89, 23);
		update_panel.add(btnDecrypt);
		
		JPanel users_panel = new JPanel();
		users_panel.setLayout(null);
		panel.add(users_panel, "users_panel");

		JLabel lblSelectTheConcert = new JLabel("Select a user to view details");
		lblSelectTheConcert.setBounds(29, 28, 245, 14);
		users_panel.add(lblSelectTheConcert);
		
		JLabel label_6 = new JLabel("AFM :");
		label_6.setHorizontalAlignment(SwingConstants.RIGHT);
		label_6.setBounds(27, 56, 72, 14);
		users_panel.add(label_6);
		
		JTextField usrAFM = new JTextField();
		usrAFM.setColumns(10);
		usrAFM.setBounds(104, 53, 116, 20);
		users_panel.add(usrAFM);
		
		JLabel label_7 = new JLabel("Name :");
		label_7.setHorizontalAlignment(SwingConstants.RIGHT);
		label_7.setBounds(29, 88, 72, 14);
		users_panel.add(label_7);
		
		JLabel label_8 = new JLabel("Surname :");
		label_8.setHorizontalAlignment(SwingConstants.RIGHT);
		label_8.setBounds(29, 113, 72, 14);
		users_panel.add(label_8);
		
		JLabel label_9 = new JLabel("Age :");
		label_9.setHorizontalAlignment(SwingConstants.RIGHT);
		label_9.setBounds(29, 138, 72, 14);
		users_panel.add(label_9);
		
		JLabel lblIllness_1 = new JLabel("Illness :");
		lblIllness_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIllness_1.setBounds(29, 163, 72, 14);
		users_panel.add(lblIllness_1);
		
		JTextField usrName = new JTextField();
		usrName.setEnabled(false);
		usrName.setColumns(10);
		usrName.setBounds(104, 85, 116, 20);
		users_panel.add(usrName);
		
		JTextField usrSur = new JTextField();
		usrSur.setEnabled(false);
		usrSur.setColumns(10);
		usrSur.setBounds(104, 110, 116, 20);
		users_panel.add(usrSur);
		
		JTextField usrAge = new JTextField();
		usrAge.setEnabled(false);
		usrAge.setColumns(10);
		usrAge.setBounds(104, 135, 116, 20);
		users_panel.add(usrAge);
		
		JTextField usrIll = new JTextField();
		usrIll.setEnabled(false);
		usrIll.setColumns(10);
		usrIll.setBounds(104, 160, 116, 20);
		users_panel.add(usrIll);
		
		JLabel label_10 = new JLabel("Public Key :");
		label_10.setHorizontalAlignment(SwingConstants.RIGHT);
		label_10.setBounds(21, 188, 72, 20);
		users_panel.add(label_10);
		
		JTextField usrPK = new JTextField();
		usrPK.setEnabled(false);
		usrPK.setColumns(10);
		usrPK.setBounds(103, 188, 116, 20);
		users_panel.add(usrPK);
		
		JLabel lblSmartcardName = new JLabel("SmartCard:");
		lblSmartcardName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSmartcardName.setBounds(259, 11, 72, 20);
		users_panel.add(lblSmartcardName);
		
		JTextField usrSC = new JTextField();
		usrSC.setColumns(10);
		usrSC.setBounds(337, 11, 87, 20);
		users_panel.add(usrSC);

		JButton btnSearch = new JButton("Find");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				MongoDatabase db = mClient.getDatabase("HealthCare");
		        MongoCollection<Document> c = db.getCollection( "testGUI");
		        
		        //query the database for one document
		        Document doc = new Document("AFM", usrAFM.getText());
		        MongoCursor<Document> cursor  = c.find(doc).iterator();
		        
		        if (cursor.hasNext())
		        {
			        Document doc1 = cursor.next();
			        if (doc1.get("Name") instanceof Binary)
			        {
			        	//make a byte array from binary object
			        	Binary data =(Binary) doc1.get("Name");
			        	nameEn =  new byte[data.length()];
			            nameEn = data.getData();
			        	usrName.setText("Encrypted");
			        }
			        else
			        {
						nameEn = null;
			        	usrName.setText(doc1.getString("Name"));
			        }

			        if (doc1.get("Surname") instanceof Binary)
			        {
			        	//make a byte array from binary object
			        	Binary data =(Binary) doc1.get("Surname");
			        	surEn =  new byte[data.length()];
			            surEn = data.getData();
			        	usrSur.setText("Encrypted");
			        }
			        else
			        {
						surEn = null;
			        	usrSur.setText(doc1.getString("Surname"));
			        }
			        
			        if (doc1.get("Age") instanceof Binary)
			        {
			        	//make a byte array from binary object
			        	Binary data =(Binary) doc1.get("Age");
			        	ageEn =  new byte[data.length()];
			            ageEn = data.getData();
			        	usrAge.setText("Encrypted");
			        }
			        else
			        {
						ageEn= null;
			        	usrAge.setText(doc1.getString("Age"));
			        }
			        
			        if (doc1.get("Illness") instanceof Binary)
			        {
			        	//make a byte array from binary object
			        	Binary data =(Binary) doc1.get("Illness");
			        	illEn =  new byte[data.length()];
			            illEn = data.getData();
			        	usrIll.setText("Encrypted");
			        }
			        else
			        {
						illEn = null;
			        	usrIll.setText(doc1.getString("Illness"));
			        }
			        
			        if (doc1.get("Public Key") instanceof Binary)
			        {
			        	usrPK.setText("Has Smart Card");
			        }
			        else
			        {
			        	usrPK.setText("NO SMART CARD");
			        }
		        }
		        else
		        {
		        	JOptionPane.showMessageDialog(frame,"No users found with this AFM","User not found",JOptionPane.ERROR_MESSAGE);
		        }
			}
		});
		btnSearch.setBounds(230, 53, 89, 21);
		users_panel.add(btnSearch);
		
		JButton nameVAL = new JButton("Validate");
		nameVAL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String[] ver = null;
					if (nameEn != null) ver = sendDataToValidatePK(connector, mClient, usrAFM.getText(), "Name", nameEn);
					else ver = sendDataToValidate(connector, mClient, usrAFM.getText(), "Name", usrName.getText());
					
					if (ver[0].equals("true")) JOptionPane.showMessageDialog(frame,"'Name' is verified to be written by user '"+ver[1]+"'.","Verified",JOptionPane.PLAIN_MESSAGE);
					else JOptionPane.showMessageDialog(frame,"'Name' is NOT verified to be written by user '"+ver[1]+"'. Be carefull of false data.","NOT Verified",JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}	

			}
		});
		nameVAL.setBounds(230, 85, 89, 21);
		users_panel.add(nameVAL);
		
		JButton surVAL = new JButton("Validate");
		surVAL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String[] ver = null;
					if (surEn != null) ver = sendDataToValidatePK(connector, mClient, usrAFM.getText(), "Surname", surEn);
					else  ver = sendDataToValidate(connector, mClient, usrAFM.getText(), "Surname", usrSur.getText());
					
					if (ver[0].equals("true")) JOptionPane.showMessageDialog(frame,"'Surname' is verified to be written by user '"+ver[1]+"'.","Verified",JOptionPane.PLAIN_MESSAGE);
					else JOptionPane.showMessageDialog(frame,"'Surname' is NOT verified to be written by user '"+ver[1]+"'. Be carefull of false data.","NOT Verified",JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}	
			}
		});
		surVAL.setBounds(230, 109, 89, 21);
		users_panel.add(surVAL);
		
		JButton ageVAL = new JButton("Validate");
		ageVAL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String[] ver = null;
					if (ageEn != null) ver = sendDataToValidatePK(connector, mClient, usrAFM.getText(), "Age", ageEn);
					else ver=sendDataToValidate(connector, mClient, usrAFM.getText(), "Age", usrAge.getText());
					
					if (ver[0].equals("true")) JOptionPane.showMessageDialog(frame,"'Age' is verified to be written by user '"+ver[1]+"'.","Verified",JOptionPane.PLAIN_MESSAGE);
					else JOptionPane.showMessageDialog(frame,"'Age' is NOT verified to be written by user '"+ver[1]+"'. Be carefull of false data.","NOT Verified",JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}	
			}
		});
		ageVAL.setBounds(230, 134, 89, 21);
		users_panel.add(ageVAL);
		
		JButton illVAL = new JButton("Validate");
		illVAL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String[] ver = null;
					if (illEn != null) ver = sendDataToValidatePK(connector, mClient, usrAFM.getText(), "Illness", illEn);
					else  ver= sendDataToValidate(connector, mClient, usrAFM.getText(), "Illness", usrIll.getText());
					
					if (ver[0].equals("true")) JOptionPane.showMessageDialog(frame,"'Illness' is verified to be written by user '"+ver[1]+"'.","Verified",JOptionPane.PLAIN_MESSAGE);
					else JOptionPane.showMessageDialog(frame,"'Illness' is NOT verified to be written by user '"+ver[1]+"'. Be carefull of false data.","NOT Verified",JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}	
			}
		});
		illVAL.setBounds(230, 159, 89, 21);
		users_panel.add(illVAL);
		
		JButton button_3 = new JButton("Back");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				usrAFM.setText(null);
				usrName.setText(null); 
				usrSur.setText(null);
				usrAge.setText(null);
				usrIll.setText(null); 
				usrSC.setText(null);
				
				nameEn = null;
				surEn = null;
				ageEn= null;
				illEn = null;
				
			    CardLayout card = (CardLayout)panel.getLayout();
			    card.show(panel, "home_panel");
			}
		});
		button_3.setBounds(10, 227, 89, 23);
		users_panel.add(button_3);
		
		JButton button = new JButton("Validate");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[] ver= sendDataToValidatePK(connector, mClient, usrAFM.getText(), "Public Key", connector.getRsaPK(mClient, usrAFM.getText()).getEncoded());
					if (ver[0].equals("true")) JOptionPane.showMessageDialog(frame,"'Public Key' is verified to be written by user '"+ver[1]+"'.","Verified",JOptionPane.PLAIN_MESSAGE);
					else JOptionPane.showMessageDialog(frame,"'Public Key' is NOT verified to be written by user '"+ver[1]+"'. Be carefull of false data.","NOT Verified",JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}	
			}
		});
		button.setBounds(230, 187, 89, 21);
		users_panel.add(button);
		
		JButton button_2 = new JButton("Decrypt");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MongoDatabase db = mClient.getDatabase("HealthCare");
		        MongoCollection<Document> c = db.getCollection( "testGUI");
		        
		        //query the database for one document
		        Document doc = new Document("AFM", usrAFM.getText());
		        MongoCursor<Document> cursor  = c.find(doc).iterator();
		        
		        if (cursor.hasNext())
		        {
			        Document doc1 = cursor.next();
			        if (doc1.get("Name") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Name");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							usrName.setText(new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(usrSC.getText()), data)) );
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
			        
			        if (doc1.get("Surname") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Surname");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							usrSur.setText(new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(usrSC.getText()), data)) );
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
			        
			        if (doc1.get("Age") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Age");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							usrAge.setText((new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(usrSC.getText()), data)) ));
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
			        
			        if (doc1.get("Illness") instanceof Binary)
			        {
			        	Binary data1 = (Binary) doc1.get("Illness");
			            //make a byte array from binary object
			            byte[] data = new byte[data1.length()];
			            data = data1.getData();
			            
			        	try {
							usrIll.setText(new String(keyManager.decryptRSA(keyManager.getPrivateKeyRSA(usrSC.getText()), data)) );
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frame,"SmartCard does not match.\n CRITICAL ERROR, restart application","ERROR",JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
			        }
		        }
				}
		});
		button_2.setBounds(337, 40, 87, 21);
		users_panel.add(button_2);
			
		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{login_panel, home_panel, insert_panel, update_panel}));
		
		frame.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{login_panel, home_panel, insert_panel}));
	}	
	
	private void update (String AFM,String name, String surname, String age, String illness,PublicKey pubKeyRSA, PrivateKey priv) throws Exception
	{		
		
		Document doc1 = new Document("Name", name  );
		Document doc2 = new Document("Surname", surname);
		Document doc3 = new Document("Age",  age);
		Document doc4 = new Document("Illness", illness );
		
		//Document doc5 = new Document("Public Key", pubKeyRSA.getEncoded().toString() );
		
		try {
			connector.myUpdate(AFM, mClient, doc1, priv);
			connector.myUpdate(AFM, mClient, doc2, priv);
			connector.myUpdate(AFM, mClient, doc3, priv);
			connector.myUpdate(AFM, mClient, doc4, priv);
			connector.storeRsaPK(mClient, pubKeyRSA, AFM, priv);
			//connector.myUpdate(AFM, mClient, doc5, priv);
		} catch (Exception e1) {
			// Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String[] sendDataToValidate (Database connector,MongoClient m,String AFM,String field,String value) throws Exception
	{
		//MongoClient m = connector.Connect();
		MongoDatabase db = m.getDatabase("HealthCare");
        MongoCollection<Document> c = db.getCollection( "testGUI");
        
        //query the database for one document
        Document doc = new Document("AFM", AFM);
        MongoCursor<Document> cursor  = c.find(doc).iterator();
        
        Document doc1 = cursor.next();
        //String data = doc1.getString(field);
        String saltedData = value + ":" + AFM; 
        ArrayList _name = (ArrayList) doc1.get("_"+field);
        
        Binary binSignedData = (Binary) _name.toArray()[0];
        String pkName = _name.toArray()[1].toString();
       
        
        byte[] signedData = new byte[binSignedData.length()];
        signedData = binSignedData.getData();
        
        cursor.close();                	        
        
        boolean val = connector.validate(m,saltedData,signedData,pkName);
        String[] result = {String.valueOf(val),pkName};
        return result;
	}
	
	private String[] sendDataToValidatePK (Database connector,MongoClient m,String AFM,String field,byte[] value) throws Exception
	{
		//MongoClient m = connector.Connect();
		MongoDatabase db = m.getDatabase("HealthCare");
        MongoCollection<Document> c = db.getCollection( "testGUI");
        
        //query the database for one document
        Document doc = new Document("AFM", AFM);
        MongoCursor<Document> cursor  = c.find(doc).iterator();
        
        Document doc1 = cursor.next();
        //String data = doc1.getString(field);
        String salt = ":" + AFM; 
        
        byte[] saltB = salt.getBytes();
    	byte[] dataB = new byte[value.length + saltB.length];

    	for (int i = 0; i < dataB.length; ++i)
    	{
    	    dataB[i] = i < value.length ? value[i] : saltB[i - value.length];
    	}    	
    	
        ArrayList _name = (ArrayList) doc1.get("_"+field);
        
        Binary binSignedData = (Binary) _name.toArray()[0];
        String pkName = _name.toArray()[1].toString();
       
        
        byte[] signedData = new byte[binSignedData.length()];
        signedData = binSignedData.getData();
        
        cursor.close();                	        
        
        boolean val = connector.validatePK(m,dataB,signedData,pkName);
        String[] result = {String.valueOf(val),pkName};
        return result;
	}
}
