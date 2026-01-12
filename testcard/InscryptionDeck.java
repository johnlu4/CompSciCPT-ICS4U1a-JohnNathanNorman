import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

public class InscryptionDeck{
    public static void main(String[] args){
        String strP1Deck[][] = new String[19][3];
        String strP2Deck[][] = new String[19][3];
        String strBigDeck[][] = new String[332][4];
        String strEvoDeck[][] = new String[2][3];
        String strSqDeck[][] = new String[0][3];
        int intCount;
        int intCountCount;
        int intCountCountCount;
        String strLine = ("");
        
        BufferedReader thefile = null;
        try{
		    thefile = new BufferedReader(new FileReader("bloodcardlist.csv"));	
		}catch(FileNotFoundException e){
			//System.out.println("ERROR file not found");
			System.out.println(e.toString());
		}
        //for(intCount = 0; intCount != 44; intCount++){
        while(strLine != null){
             try{
                strLine = thefile.readLine();
                System.out.println(strLine);
            }catch(IOException e){
                strLine = null;
            }
            for(intCountCount = 0; intCountCount != 3; intCountCount++){
                for(intCount = 0; intCount != 269; intCountCount++){
                    strBigDeck[intCount][0] = strLine; 
                    strBigDeck[intCount+1][1] = strLine;
                    strBigDeck[intCount+2][2] = strLine;
                    strBigDeck[intCount+3][3] = strLine;  
                    //System.out.println(strBigDeck[intCount][0]);
                    //System.out.println(strBigDeck[intCount+1][0]);
                    //System.out.println(strBigDeck[intCount+2][0]);
                    //System.out.println(strBigDeck[intCount+3][0]);
                    try{
                        strLine = thefile.readLine();
                        System.out.println(strLine);
                    }catch(IOException e){
                        strLine = null;
                        System.out.println("AHHHHHHH");
                    }
                }
            }
        }
        
        
    }
}