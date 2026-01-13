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
        String strBigDeck[][] = new String[278][6];
        String strEvoDeck[][] = new String[2][3];
        String strSqDeck1[][] = new String[0][3];
        String strSqDeck2[][] = new String[0][3];
        int intCount = 0;
        String strLine = ("");
        int intRandom = (int)(Math.random()*100+1);
        
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
                //strLine = thefile.readLine();
                if(strLine != null){
                    strBigDeck[intCount][0] = strLine;
                    System.out.println("cur 0 "+strBigDeck[intCount][0]);
                    strLine = thefile.readLine();
                    //System.out.println("next "+strLine);
                    strBigDeck[intCount][1] = strLine;
                    System.out.println("cur 1 "+strBigDeck[intCount][1]);
                    strLine = thefile.readLine();
                    //System.out.println("next "+strLine);
                    strBigDeck[intCount][2] = strLine;
                    System.out.println("cur 2 "+strBigDeck[intCount][2]);
                    strLine = thefile.readLine();
                    //System.out.println("next "+strLine);
                    strBigDeck[intCount][3] = strLine;
                    System.out.println("cur 3 "+strBigDeck[intCount][3]);
                    strLine = thefile.readLine();
                    strBigDeck[intCount][4] = strLine;
                    System.out.println("cur 4 "+strBigDeck[intCount][4]);
                    intRandom = (int)(Math.random()*100+1);
                    strBigDeck[intCount][5] = intRandom+"";
                    System.out.println("cur 5 "+strBigDeck[intCount][5]);
                    strLine = thefile.readLine();
                    //System.out.println("next "+strLine);
                    intCount = intCount +1;
                    //System.out.println("next "+strLine);
                }else{
                }
            }catch(IOException e){
                strLine = null;
            }    
        }
    }
}