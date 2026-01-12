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
        String strBigDeck[][] = new String[218][4];
        String strEvoDeck[][] = new String[2][3];
        String strSqDeck[][] = new String[0][3];
        int intCount = 0;
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
        try{
            strLine = thefile.readLine();
            //System.out.println(strLine);
        }catch(IOException e){
            strLine = null;
        }
        while(strLine != null){
            try{
                strLine = thefile.readLine();
                strBigDeck[intCount][0] = strLine;
                System.out.println("deck "+strBigDeck[intCount][0]);
                strLine = thefile.readLine();
                System.out.println("next "+strLine);
                strBigDeck[intCount][1] = strLine;
                System.out.println("deck "+strBigDeck[intCount][1]);
                strLine = thefile.readLine();
                System.out.println("next "+strLine);
                strBigDeck[intCount][2] = strLine;
                System.out.println("deck "+strBigDeck[intCount][2]);
                strLine = thefile.readLine();
                System.out.println("next "+strLine);
                strBigDeck[intCount][3] = strLine;
                System.out.println("deck"+strBigDeck[intCount][3]);
                strLine = thefile.readLine();
                System.out.println("next "+strLine);
                intCount = intCount +1;
                //System.out.println("next "+strLine);
            }catch(IOException e){
                strLine = null;
            }
            //strBigDeck[intCount][0] = strLine; 
            //strBigDeck[intCount][1] = strLine;
            //strBigDeck[intCount][2] = strLine;
           // strBigDeck[intCount][3] = strLine;
            //intCount = intCount +1;
            //System.out.println(strBigDeck[intCount][0]);
            //System.out.println(strBigDeck[intCount][1]);
            //System.out.println(strBigDeck[intCount][2]);
            //System.out.println(strBigDeck[intCount][3]);
        }
        
        
    }
}