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
        int intRow;
        int intRow2;
        String strTempName;
        String strTempCost;
        String strTempHP;
        String strTempAttack;
        String strTempSigil;
        String strTempOrder;
        
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
                    //System.out.println("cur 0 "+strBigDeck[intCount][0]);
                    strLine = thefile.readLine();
                    //System.out.println("next "+strLine);
                    strBigDeck[intCount][1] = strLine;
                   // System.out.println("cur 1 "+strBigDeck[intCount][1]);
                    strLine = thefile.readLine();
                    //System.out.println("next "+strLine);
                    strBigDeck[intCount][2] = strLine;
                    //System.out.println("cur 2 "+strBigDeck[intCount][2]);
                    strLine = thefile.readLine();
                    //System.out.println("next "+strLine);
                    strBigDeck[intCount][3] = strLine;
                    //System.out.println("cur 3 "+strBigDeck[intCount][3]);
                    strLine = thefile.readLine();
                    strBigDeck[intCount][4] = strLine;
                    //System.out.println("cur 4 "+strBigDeck[intCount][4]);
                    intRandom = (int)(Math.random()*100+1);
                    strBigDeck[intCount][5] = intRandom+"";
                    //System.out.println("cur 5 "+strBigDeck[intCount][5]);
                    strLine = thefile.readLine();
                    intCount = intCount +1;
                }
            }catch(IOException e){
                strLine = null;
            }    
        }
        for(intRow2 = 0; intRow2 < 278; intRow++){
            for(intRow = 0; intRow <278-1-intRow2;intRow++){
                try{
                    if(Integer.parseInt(strBigDeck[intRow][5]) >= Integer.parseInt(strBigDeck[intRow+1][5])){
                        strTempName = strBigDeck[intRow][0];
                        strTempCost = strBigDeck[intRow][1];
                        strTempHP = strBigDeck[intRow][2];
                        strTempAttack = strBigDeck[intRow][3];
                        strTempSigil = strBigDeck[intRow][4];
                        strTempOrder = strBigDeck[intRow][5];

                        strBigDeck[intRow][0] = strBigDeck[intRow+1][0];
                        strBigDeck[intRow][1] = strBigDeck[intRow+1][1];
                        strBigDeck[intRow][2] = strBigDeck[intRow+1][2];
                        strBigDeck[intRow][3] = strBigDeck[intRow+1][3];
                        strBigDeck[intRow][4] = strBigDeck[intRow+1][4];
                        strBigDeck[intRow][5] = strBigDeck[intRow+1][5];

                        strBigDeck[intRow+1][0] = strTempName;
                        strBigDeck[intRow+1][1] = strTempCost;
                        strBigDeck[intRow+1][2] = strTempHP;
                        strBigDeck[intRow+1][3] = strTempAttack;
                        strBigDeck[intRow+1][4] = strTempSigil;
                        strBigDeck[intRow+1][5] = strTempOrder;
                        
                        //System.out.println(strTempName);
                        //System.out.println(strTempCost);
                        //System.out.println(strTempHP);
                        //System.out.println(strTempAttack);
                        //System.out.println(strTempSigil);
                        //System.out.println(strTempOrder);

                    }
                }catch(java.lang.NumberFormatException e){
                    //System.out.println("Null");
                }
            }
        }
        for(intCount = 0; intCount <= 56;intCount++){
            System.out.println(strBigDeck[intCount][0]);
            System.out.println(strBigDeck[intCount][1]);
            System.out.println(strBigDeck[intCount][2]);
            System.out.println(strBigDeck[intCount][3]);
            System.out.println(strBigDeck[intCount][4]);
            System.out.println(strBigDeck[intCount][5]);
        }
    }
}