import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class crypt extends JFrame {
	
	private Scanner theReader;
	private FileWriter theWriter;
	
	private ArrayList<String> TheCharacters = new ArrayList<String>(Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            " ", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+",
            "[", "]", "{", "}", ";", ":", "<", ">", "?", "|", ".", ",", "/", "\'", "\"", "\\"));
	private ArrayList<String> KeyConvert = new ArrayList<String>();
	private ArrayList<String> KeyConvertAlt = new ArrayList<String>();
	private String PassKey;
	
	private int alter = 0;
	private boolean printKey = false;
	
	private final JFileChooser fileSearcher = new JFileChooser();
	private int returnFile;
	private JButton getInFile = new JButton("Get File");
	private String inputFilePath;
	private File iFile;
    private JButton getOutFile = new JButton("Get File");
    private String outputFilePath;
    private File oFile;
    private JButton EnButton = new JButton("Encrypt");
    private JButton DeButton = new JButton("Decrypt");
    private JTextField cryptPass = new JTextField(30);
	
    //Empties the Key values
    private void reset() {
    	KeyConvert = new ArrayList<String>();
    	KeyConvertAlt = new ArrayList<String>();
    	PassKey = "";
    	alter = 0;
    }
    
	//Returns a String ArrayList of data in provided file
	private ArrayList<String> FileToArray(File FileTo) {
		ArrayList<String> aNewArray = new ArrayList<String>();
		try {
			theReader = new Scanner(FileTo);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (theReader.hasNextLine()) {
			aNewArray.add(theReader.nextLine());
		}
		return aNewArray;
    }
	
	//Creates KeyConvert (ArrayList of coded values)
	private void GenerateKeyConvert() {
		String KeyVal = "";
		int r = 0;
		if (PassKey != null) {
			for (int i = 0; i < TheCharacters.size() * 2; i++) {
				if ((i + r) * r > 1000) {
					r = i;
				}
				if (i == 0) {
					KeyVal += PassKey.charAt(PassKey.length() - 1);
				}
				else {
					
					while (!CheckIfValid(KeyVal)) {
						
						KeyVal += TheCharacters.get(AlteredIndex((i + TheCharacters.indexOf(PassKey.charAt(AlteredIndex(r, PassKey.length() - 1)) + "")) * r, TheCharacters.size() - 1));
						
						r += 1;
						KeyVal = KeyVal.replaceAll("\\s+","");
						
						if (CheckRepeatChar(KeyVal)) {
							KeyVal = "";
						}
						
					}
					
				}
				
				if (i < TheCharacters.size()) {
					KeyConvert.add(KeyVal);
				}
				else {
					KeyConvertAlt.add(KeyVal);
				}
				KeyVal = "";
			}
		}
		else {
			System.out.println("Error: PassKey is null!");
		}
		
		if (printKey) {
			System.out.println("TheCharacters");
			System.out.println(TheCharacters);
			System.out.println(TheCharacters.size());
			System.out.println("");
			
			System.out.println("KeyConvert");
			System.out.println(KeyConvert);
			System.out.println(KeyConvert.size());
			System.out.println("");
			
			System.out.println("KeyConvertAlt");
			System.out.println(KeyConvertAlt);
			System.out.println(KeyConvertAlt.size());
			System.out.println("");
		}
	}
	
	//Checks to see if provided value would be a valid fit for KeyConvert (ArrayList of coded values)
	private boolean CheckIfValid(String testValue) {
		if (testValue.equals("")) {
			return false;
		}
		else if (testValue.equals(" ")) {
			return false;
		}
		else if (AlreadyUsed(testValue, KeyConvert)) {
			return false;
		}
		else if (AlreadyUsed(testValue, KeyConvertAlt)) {
			return false;
		}
		else if (KeyConvert.size() == 0) {
			return true;
		}
		else if (KeyConvert.size() == TheCharacters.size() && KeyConvertAlt.size() == 0) {
			return true;
		}
		return true;
	}
	
	//Checks if String is already in ArrayList
	private boolean AlreadyUsed(String used, ArrayList<String> inList) {
		for (int x = 0; x < inList.size(); x++) {
			if (used.equals(inList.get(x))) {
				return true;
			}
		}
		return false;
	}
	
	//Checks if the latest 2 characters in string are the same
	private boolean CheckRepeatChar(String re) {
		if ((re.length() >= 2)&&(re.charAt(re.length() - 1) == re.charAt(re.length() - 2))) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//Adjusts provided index to fix boundary of provided index length
	private int AlteredIndex(int oldIndex, int maxLength) {
		if (oldIndex < maxLength) {
			return oldIndex;
		}
		else {
			return oldIndex % maxLength;
		}
	}
	
	//Encrypts data in provided file then sends it to another provided file
	private void Encrypt(File targetFile, File destinationFile) {
		String enLine = "";
		ArrayList<String> targetArray = FileToArray(targetFile);
		ArrayList<String> destinationArray = new ArrayList<String>();
		for (int i = 0; i < targetArray.size(); i++) {
			enLine = Convert(targetArray.get(i), "Encrypt");
			destinationArray.add(enLine);
			enLine = "";
			alter++;
		}
		SendToFile(destinationFile, destinationArray);
	}
	
	//Decrypts data in provided file then sends it to another provided file
	private void Decrypt(File targetFile, File destinationFile) {
		String deLine = "";
		ArrayList<String> targetArray = FileToArray(targetFile);
		ArrayList<String> destinationArray = new ArrayList<String>();
		for (int i = 0; i < targetArray.size(); i++) {
			deLine = Convert(targetArray.get(i), "Decrypt");
			destinationArray.add(deLine);
			deLine = "";
		}
		SendToFile(destinationFile, destinationArray);
	}
	
	//Encrypts/Decrypts provided String based on provided mode
	private String Convert(String s, String mode) {
		String theLine = "";
		
		if (KeyConvert == null) {
			System.out.println("Error: KeyConvert is null!");
		}
		else if (mode.equals("Encrypt")) {
			theLine = "";
			for (int j = 0; j < s.length(); j++) {
				
				for (int k = 0; k < TheCharacters.size(); k++) {
					if ((s.charAt(j) + "").equals(TheCharacters.get(k))) {
						if (alter % 2 == 0) {
							theLine += KeyConvert.get(k) + " ";
						}
						else {
							theLine += KeyConvertAlt.get(k) + " ";
						}
						break;
					}
				}
				alter++;
			}
			if (theLine.length() > 0) {
				theLine = theLine.substring(0, theLine.length() - 1);
			}
		}
		else if (mode.equals("Decrypt")) {
			theLine = "";
			String[] splitString = s.split("\\s+");
			
			for (int j = 0; j < splitString.length; j++) {
				
				for (int k = 0; k < KeyConvert.size(); k++) {
					if (splitString[j].equals(KeyConvert.get(k)) || splitString[j].equals(KeyConvertAlt.get(k))) {
						theLine += TheCharacters.get(k);
						break;
					}
				}
				
			}
		}
		else {
			System.out.println("Error: Convert Method Could Not Determine Mode!");
		}
		
		return theLine;
	}
	
	//Sends provided data to provided file
	private void SendToFile(File file, ArrayList<String> Data) {
		try {
			theWriter = new FileWriter(file);
			for (int i = 0; i < Data.size(); i++) {
				if (i != Data.size() - 1) {
					theWriter.write(Data.get(i) + "\n");
				}
				else {
					theWriter.write(Data.get(i));
				}
	            
	        }
	        theWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Sets up the password and key based on provided String
	private void SetPass(String pass) {
		PassKey = pass.replaceAll("\\s+","");
		GenerateKeyConvert();
	}
	
	//Checks the password and selected files to make sure everything is ready
	private boolean checkStatus() {
		
		boolean error = false;
		String message = "";
		
		if (cryptPass.getText() == null || cryptPass.getText().length() == 0) {
			error = true;
			message = "Password is blank";
		}
		else if (cryptPass.getText().length() < 3 || cryptPass.getText().chars().distinct().count() < 3) {
			error = true;
			message = "Password requires at least 3 distinct characters";
		}
		else if (inputFilePath == null || outputFilePath == null || inputFilePath == "" || outputFilePath == "") {
			error = true;
			message = "Choose files before Encrypting or Decrypting";
		}
		
		if (error) {
			JOptionPane.showMessageDialog(null, message,"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else {
			return true;
		}
	}
	
    //Main
	public static void main(String[] args) {
		JFrame hud = new crypt();
        hud.setVisible(true);
    }
	
	//JFrame
	public crypt() {
		
		setTitle("Crypt");
		setLayout(new GridLayout(5, 3));
		
		JPanel insertCrypt = new JPanel();
		JPanel FileFinder = new JPanel();
        JPanel Convertion = new JPanel();
        
        JLabel cryptLabel = new JLabel("Key: ");
        insertCrypt.add(cryptLabel);
        cryptPass.setText("Password");
        insertCrypt.add(cryptPass);
        
        //Input File Select
        JLabel inLabel = new JLabel("Input File: <?>");
        FileFinder.add(inLabel);
        FileFinder.add(getInFile);
        ActionListener iFButton = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnFile = fileSearcher.showOpenDialog(crypt.this);
                if (returnFile == fileSearcher.APPROVE_OPTION) {
                    iFile = fileSearcher.getSelectedFile();
                    inputFilePath = iFile.getPath();
                    inLabel.setText("Input File: " + iFile.getName());
                }
            }
        };
        getInFile.addActionListener(iFButton);
        
        //Output File Select
        JLabel outLabel = new JLabel("Output File: <?>");
        FileFinder.add(outLabel);
        FileFinder.add(getOutFile);
        ActionListener oFButton = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e ) {
                returnFile = fileSearcher.showOpenDialog(crypt.this);
                if (returnFile == fileSearcher.APPROVE_OPTION) {
                    oFile = fileSearcher.getSelectedFile();
                    outputFilePath = oFile.getPath();
                    outLabel.setText("Output File: " + oFile.getName());
                }
            }
        };
        getOutFile.addActionListener(oFButton);
        
        //Encrypt Button
        JLabel EncryptLabel = new JLabel("");
        Convertion.add(EncryptLabel);
        Convertion.add(EnButton);
        ActionListener EnButtonL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e ) {
            	if (checkStatus()) {
            		SetPass(cryptPass.getText());
                	Encrypt(new File(inputFilePath), new File(outputFilePath));
                	reset();
            	}
            }
        };
        EnButton.addActionListener(EnButtonL);
        
        //Decrypt Button
        JLabel DecryptLabel = new JLabel("");
        Convertion.add(DecryptLabel);
        Convertion.add(DeButton);
        ActionListener DeButtonL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e ) {
            	if (checkStatus()) {
            		SetPass(cryptPass.getText());
                	Decrypt(new File(inputFilePath), new File(outputFilePath));
                	reset();
            	}
            }
        };
        DeButton.addActionListener(DeButtonL);
        
        add(insertCrypt);
        add(FileFinder);
        add(Convertion);
        
		pack();
        setSize(400, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
}
