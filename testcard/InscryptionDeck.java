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
        String strP1Deck[][] = new String[20][5];
        String strP2Deck[][] = new String[20][5];
        String strBigDeck[][] = new String[56][6];
        String strEvoDeck[][] = new String[2][4];
        String strSqDeck1[][] = new String[0][4];
        String strSqDeck2[][] = new String[0][4];
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
        boolean blnP1DeckFill = false;
        boolean blnP2DeckFill = false;
        int intRow3 = 0;
        int intRow4 = 0;
        int intRow5 = 0;

        BufferedReader thefile = null;
        strP1Deck[19][4] = ("Blank");
        strP2Deck[19][4] = ("Blank");
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

        // read the card info into a 2-dimensional array
        while(strLine != null){
            String strArray[];
            int intCol;
            try{
                //strLine = thefile.readLine();
                if(strLine != null){
                    strArray = strLine.split(",");
                    for (intCol = 0; intCol < 5; intCol++) {
                        strBigDeck[intCount][intCol] = strArray[intCol];
                    }
                    intRandom = (int)(Math.random()*100+1);
                    strBigDeck[intCount][5] = intRandom+"";
                    //System.out.println("cur 5 "+strBigDeck[intCount][5]);
                    
                    strLine = thefile.readLine();
                    intCount = intCount +1;
                }
                //System.out.println("" + intCount + " rows");
            }catch(IOException e){
                strLine = null;
            }    
        }

        for(intRow2 = 0; intRow2 < 56; intRow2++){
            for(intRow = 0; intRow <56-1-intRow2;intRow++){
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
                        
                    }
                }catch(java.lang.NumberFormatException e){
                    System.out.println("Null");
                }
            }
        }
        //for(intCount = 0; intCount < 56;intCount++){
            //System.out.print(strBigDeck[intCount][0] + ", ");
            //System.out.print(strBigDeck[intCount][1] + ", ");
            //System.out.print(strBigDeck[intCount][2] + ", ");
            //System.out.print(strBigDeck[intCount][3] + ", ");
            //System.out.print(strBigDeck[intCount][4] + ", ");
            //System.out.println(strBigDeck[intCount][5]);
        //}
        while(blnP1DeckFill != true){
            try{
                if(strP1Deck[19][4].equals("Blank") && strP2Deck[19][4].equals("Blank") ){
                    intRandom = (int)(Math.random()*100+1);
                    //System.out.println(intRandom);
                    if(intRandom < 50 && strP1Deck[19][4].equals("Blank")){
                        strP1Deck[intRow4][0] = strBigDeck[intRow3][0];
                        strP1Deck[intRow4][1] = strBigDeck[intRow3][1];
                        strP1Deck[intRow4][2] = strBigDeck[intRow3][2];
                        strP1Deck[intRow4][3] = strBigDeck[intRow3][3];
                        strP1Deck[intRow4][4] = strBigDeck[intRow3][4];
                        intRow4 = intRow4+1;
                        intRow3 = intRow3 + 1;
                    }else if(intRandom > 50 && strP2Deck[19][4].equals("Blank")){
                        strP2Deck[intRow5][0] = strBigDeck[intRow3][0];
                        strP2Deck[intRow5][1] = strBigDeck[intRow3][1];
                        strP2Deck[intRow5][2] = strBigDeck[intRow3][2];
                        strP2Deck[intRow5][3] = strBigDeck[intRow3][3];
                        strP2Deck[intRow5][4] = strBigDeck[intRow3][4];
                        intRow5 = intRow5+1; 
                        intRow3 = intRow3 + 1;
                    }

                }else{
                    blnP1DeckFill = true;
                    System.out.println("Done");
                }
            }catch(java.lang.NullPointerException e){
                blnP1DeckFill = true;
                System.out.println("AHHHHH");
            }
        }
         for(intCount = 0; intCount < 20 ;intCount++){
            System.out.print(strP1Deck[intCount][0] + ", ");
            System.out.print(strP1Deck[intCount][1] + ", ");
            System.out.print(strP1Deck[intCount][2] + ", ");
            System.out.print(strP1Deck[intCount][3] + ", ");
            System.out.print(strP1Deck[intCount][4] + ", ");
            System.out.println("");
        }
        System.out.println("P2");
        for(intCount = 0; intCount < 20 ;intCount++){
            System.out.print(strP2Deck[intCount][0] + ", ");
            System.out.print(strP2Deck[intCount][1] + ", ");
            System.out.print(strP2Deck[intCount][2] + ", ");
            System.out.print(strP2Deck[intCount][3] + ", ");
            System.out.print(strP2Deck[intCount][4] + ", ");
            System.out.println("");
        }
    }
}