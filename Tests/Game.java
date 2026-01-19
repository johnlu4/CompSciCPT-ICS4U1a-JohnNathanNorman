package Tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Game {
    // Properties
    private PlayerClass p1;
    private PlayerClass p2;
    public boolean blnStarted = false;
    public boolean isInitializationPhase = true; // True during first round
    private String currentPhase = "DrawingPhase";
    private JAnimation animationPanel;
    private SuperSocketMaster ssm;
    
    public int intScale = 0;

    String strBigDeck[][] = new String[56][6];
    String strEvoDeck[][] = new String[2][4];

    // Constructor
    public Game(PlayerClass p1, PlayerClass p2, JAnimation animationPanel, SuperSocketMaster ssm) {
        this.p1 = p1;
        this.p2 = p2;
        this.animationPanel = animationPanel;
        this.ssm = ssm;
        animationPanel.setGame(this);
    }

    // Methods
    public void startGame() {
        // Initialize player decks, blood, etc.
        p1.intBlood = 0;
        p2.intBlood = 0;

        p1.intLives = 2;
        p2.intLives = 2;

        // Initialize randomized decks and squirrels
        initializeDecks();

        // Draw 4 initial cards and 1 squirrel for each player
        System.out.println("\n=== Initial Card Draw ===");
        System.out.println("Player 1 draws:");
        for (int i = 0; i < 4; i++) {
            CardClass card = p1.drawCard();
            if (card != null) {
                System.out.println("  " + (i+1) + ". " + card.strName + " (Cost:" + card.intCost + ", HP:" + card.intHealth + ", ATK:" + card.intAttack + ", Sigil:" + card.strSigil + ")");
            }
        }
        CardClass squirrel1 = p1.drawSquirrel();
        if (squirrel1 != null) {
            System.out.println("  5. " + squirrel1.strName + " (Cost:" + squirrel1.intCost + ", HP:" + squirrel1.intHealth + ", ATK:" + squirrel1.intAttack + ", Sigil:" + squirrel1.strSigil + ")");
        }
        
        System.out.println("Player 2 draws:");
        for (int i = 0; i < 4; i++) {
            CardClass card = p2.drawCard();
            if (card != null) {
                System.out.println("  " + (i+1) + ". " + card.strName + " (Cost:" + card.intCost + ", HP:" + card.intHealth + ", ATK:" + card.intAttack + ", Sigil:" + card.strSigil + ")");
            }
        }
        CardClass squirrel2 = p2.drawSquirrel();
        if (squirrel2 != null) {
            System.out.println("  5. " + squirrel2.strName + " (Cost:" + squirrel2.intCost + ", HP:" + squirrel2.intHealth + ", ATK:" + squirrel2.intAttack + ", Sigil:" + squirrel2.strSigil + ")");
        }
        System.out.println("Each player drew 4 cards + 1 squirrel to start\n");

        // Set initial phase
        currentPhase = "DrawingPhase";

        // Notify animation panel
        animationPanel.repaint();

        blnStarted = true;
    }

    /**
     * Initialize randomized decks for both players using InscryptionDeck logic
     * Reads bloodcardlist.csv and Squireldeck.csv to populate player decks
     */
    private void initializeDecks() {
        String strP1Deck[][] = new String[20][5];
        String strP2Deck[][] = new String[20][5];
        String strBigDeck[][] = new String[56][6];
        String strSqDeck1[][] = new String[10][5];
        String strSqDeck2[][] = new String[10][5];
        int intCount = 0;
        String strLine = "";
        int intRandom;
        int intRow;
        int intRow2;
        String strTempName, strTempCost, strTempHP, strTempAttack, strTempSigil, strTempOrder;
        boolean blnDecksFilled = false;
        int intRow3 = 0;
        int intRow4 = 0;
        int intRow5 = 0;

        BufferedReader thefile = null;
        strP1Deck[19][4] = "Blank";
        strP2Deck[19][4] = "Blank";

        // Read blood cards from CSV
        try {
            thefile = new BufferedReader(new FileReader("testcard/bloodcardlist.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("Error loading bloodcardlist.csv: " + e.toString());
            return;
        }

        try {
            strLine = thefile.readLine();
        } catch (IOException e) {
            strLine = null;
        }

        // Load all cards into strBigDeck
        while (strLine != null) {
            try {
                if (strLine != null) {
                    String strArray[] = strLine.split(",");
                    for (int intCol = 0; intCol < 5; intCol++) {
                        strBigDeck[intCount][intCol] = strArray[intCol];
                    }
                    intRandom = (int)(Math.random() * 100 + 1);
                    strBigDeck[intCount][5] = intRandom + "";
                    strLine = thefile.readLine();
                    intCount++;
                }
            } catch (IOException e) {
                strLine = null;
            }
        }

        // Sort cards by random number (bubble sort)
        for (intRow2 = 0; intRow2 < 56; intRow2++) {
            for (intRow = 0; intRow < 56 - 1 - intRow2; intRow++) {
                try {
                    if (Integer.parseInt(strBigDeck[intRow][5]) >= Integer.parseInt(strBigDeck[intRow + 1][5])) {
                        strTempName = strBigDeck[intRow][0];
                        strTempCost = strBigDeck[intRow][1];
                        strTempHP = strBigDeck[intRow][2];
                        strTempAttack = strBigDeck[intRow][3];
                        strTempSigil = strBigDeck[intRow][4];
                        strTempOrder = strBigDeck[intRow][5];

                        strBigDeck[intRow][0] = strBigDeck[intRow + 1][0];
                        strBigDeck[intRow][1] = strBigDeck[intRow + 1][1];
                        strBigDeck[intRow][2] = strBigDeck[intRow + 1][2];
                        strBigDeck[intRow][3] = strBigDeck[intRow + 1][3];
                        strBigDeck[intRow][4] = strBigDeck[intRow + 1][4];
                        strBigDeck[intRow][5] = strBigDeck[intRow + 1][5];

                        strBigDeck[intRow + 1][0] = strTempName;
                        strBigDeck[intRow + 1][1] = strTempCost;
                        strBigDeck[intRow + 1][2] = strTempHP;
                        strBigDeck[intRow + 1][3] = strTempAttack;
                        strBigDeck[intRow + 1][4] = strTempSigil;
                        strBigDeck[intRow + 1][5] = strTempOrder;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing card data");
                }
            }
        }

        // Distribute cards randomly to both players
        while (!blnDecksFilled) {
            try {
                if (strP1Deck[19][4].equals("Blank") || strP2Deck[19][4].equals("Blank")) {
                    intRandom = (int)(Math.random() * 100 + 1);
                    if (intRandom < 50 && strP1Deck[19][4].equals("Blank")) {
                        strP1Deck[intRow4][0] = strBigDeck[intRow3][0];
                        strP1Deck[intRow4][1] = strBigDeck[intRow3][1];
                        strP1Deck[intRow4][2] = strBigDeck[intRow3][2];
                        strP1Deck[intRow4][3] = strBigDeck[intRow3][3];
                        strP1Deck[intRow4][4] = strBigDeck[intRow3][4];
                        intRow4++;
                        intRow3++;
                    } else if (intRandom > 50 && strP2Deck[19][4].equals("Blank")) {
                        strP2Deck[intRow5][0] = strBigDeck[intRow3][0];
                        strP2Deck[intRow5][1] = strBigDeck[intRow3][1];
                        strP2Deck[intRow5][2] = strBigDeck[intRow3][2];
                        strP2Deck[intRow5][3] = strBigDeck[intRow3][3];
                        strP2Deck[intRow5][4] = strBigDeck[intRow3][4];
                        intRow5++;
                        intRow3++;
                    }
                } else {
                    blnDecksFilled = true;
                    System.out.println("Decks initialized for both players");
                }
            } catch (NullPointerException e) {
                blnDecksFilled = true;
                System.out.println("Error during deck distribution");
            }
        }

        // Copy to player objects
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 5; j++) {
                p1.strDeck[i][j] = strP1Deck[i][j];
                p2.strDeck[i][j] = strP2Deck[i][j];
            }
        }

        // Debug: Print Player 1 deck
        System.out.println("\n=== Player 1 Deck ===");
        for (int i = 0; i < 20; i++) {
            if (p1.strDeck[i][0] != null) {
                System.out.print(i + ": " + p1.strDeck[i][0] + ", ");
                System.out.print("Cost:" + p1.strDeck[i][1] + ", ");
                System.out.print("HP:" + p1.strDeck[i][2] + ", ");
                System.out.print("ATK:" + p1.strDeck[i][3] + ", ");
                System.out.println("Sigil:" + p1.strDeck[i][4]);
            }
        }

        // Debug: Print Player 2 deck
        System.out.println("\n=== Player 2 Deck ===");
        for (int i = 0; i < 20; i++) {
            if (p2.strDeck[i][0] != null) {
                System.out.print(i + ": " + p2.strDeck[i][0] + ", ");
                System.out.print("Cost:" + p2.strDeck[i][1] + ", ");
                System.out.print("HP:" + p2.strDeck[i][2] + ", ");
                System.out.print("ATK:" + p2.strDeck[i][3] + ", ");
                System.out.println("Sigil:" + p2.strDeck[i][4]);
            }
        }

        // Close blood card file
        try {
            thefile.close();
        } catch (IOException e) {
            System.out.println("Error closing bloodcardlist.csv");
        }

        // Load squirrel decks
        BufferedReader rodent = null;
        try {
            rodent = new BufferedReader(new FileReader("testcard/Squireldeck.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("Error loading Squireldeck.csv: " + e.toString());
            return;
        }

        intCount = 0;
        try {
            strLine = rodent.readLine();
            while (strLine != null) {
                if (strLine != null) {
                    String strArray[] = strLine.split(",");
                    for (int intCol = 0; intCol < 5; intCol++) {
                        strSqDeck1[intCount][intCol] = strArray[intCol];
                        strSqDeck2[intCount][intCol] = strArray[intCol];
                    }
                    strLine = rodent.readLine();
                    intCount++;
                }
            }
            System.out.println("Loaded " + intCount + " squirrel cards for each player");
        } catch (IOException e) {
            System.out.println("Error reading squirrel deck");
        }

        // Copy squirrel decks to player objects
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                p1.strSquirrelDeck[i][j] = strSqDeck1[i][j];
                p2.strSquirrelDeck[i][j] = strSqDeck2[i][j];
            }
        }

        // Close squirrel file
        try {
            rodent.close();
        } catch (IOException e) {
            System.out.println("Error closing Squireldeck.csv");
        }
    }

    public void nextPhase() {
        if (currentPhase.equals("DrawingPhase")) {
            // Check if both players are ready
            if (p1.isReady && p2.isReady) {
                currentPhase = "AttackPhase";
                System.out.println("Both players ready - Starting Attack Phase");
                
                // End initialization phase after first ready-up
                if (isInitializationPhase) {
                    isInitializationPhase = false;
                    System.out.println("Initialization phase complete");
                }
            } else {
                System.out.println("Waiting for players to ready up...");
            }
        } else if (currentPhase.equals("AttackPhase")) {
            currentPhase = "DrawingPhase";
            // Reset drawing phase tracking for both players
            p1.resetDrawPhase();
            p2.resetDrawPhase();
            System.out.println("Drawing Phase started - Players can draw a card");
        }
        // Add more phases as needed
        animationPanel.repaint();
    }

    /**
     * Player draws from their main deck
     * @param playerNumber 1 for P1, 2 for P2
     * @return true if card was drawn successfully
     */
    public boolean playerDrawCard(int playerNumber) {
        PlayerClass player = (playerNumber == 1) ? p1 : p2;
        
        if (player.hasDrawnThisTurn) {
            System.out.println(player.strPlayerName + " has already drawn this turn!");
            return false;
        }
        
        CardClass card = player.drawCard();
        if (card != null) {
            player.hasDrawnThisTurn = true;
            System.out.println(player.strPlayerName + " drew: " + card.strName);
            animationPanel.repaint();
            return true;
        } else {
            System.out.println(player.strPlayerName + " - Deck is empty!");
            return false;
        }
    }

    /**
     * Player draws a squirrel card
     * @param playerNumber 1 for P1, 2 for P2
     * @return true if squirrel was drawn successfully
     */
    public boolean playerDrawSquirrel(int playerNumber) {
        PlayerClass player = (playerNumber == 1) ? p1 : p2;
        
        if (player.hasDrawnThisTurn) {
            System.out.println(player.strPlayerName + " has already drawn this turn!");
            return false;
        }
        
        CardClass squirrel = player.drawSquirrel();
        if (squirrel != null) {
            player.hasDrawnThisTurn = true;
            System.out.println(player.strPlayerName + " drew a squirrel: " + squirrel.strName);
            animationPanel.repaint();
            return true;
        } else {
            System.out.println(player.strPlayerName + " - No squirrels left!");
            return false;
        }
    }

    /**
     * Player readies up for next phase
     * @param playerNumber 1 for P1, 2 for P2
     */
    public void playerReady(int playerNumber) {
        PlayerClass player = (playerNumber == 1) ? p1 : p2;
        if(player.isReady == true){
            System.out.println(player.strPlayerName + " is already ready!");
            return;
        }
        player.isReady = true;
        System.out.println(player.strPlayerName + " is ready!");
        
        // Send PLAYER_READY message to other player (only for player 1 - local player)
        if (playerNumber == 1 && ssm != null) {
            ssm.sendText("PLAYER_READY");
        }
        
        // Check if both players are ready
        if (p1.isReady && p2.isReady) {
            nextPhase();
        } else {
            System.out.println("Waiting for other player...");
        }
        animationPanel.repaint();
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public PlayerClass getP1() {
        return p1;
    }

    public PlayerClass getP2() {
        return p2;
    }

    // methods for gameplay logic, e.g., executeAttack, checkWinCondition, etc.
    /**
     * Attempts to play a card from a player's hand to the board
     * @param playerNumber The player number (1 or 2)
     * @param card The CardClass object to play
     * @param cardSlot The slot index (0-3) to place the card
     * @return true if card was successfully played, false otherwise
     */
    public boolean playCard(int playerNumber, CardClass card, int cardSlot) {
        PlayerClass player = (playerNumber == 1) ? p1 : p2;
        
        // Validate inputs
        if (card == null) {
            System.out.println("Error: Card is null");
            return false;
        }
        
        if (cardSlot < 0 || cardSlot > 3) {
            System.out.println("Error: Invalid card slot " + cardSlot);
            return false;
        }
        
        // Check if it's the correct phase
        if (!currentPhase.equals("DrawingPhase")) {
            System.out.println("Error: Can only play cards during Drawing Phase");
            return false;
        }
        
        // Attempt to place the card (this checks blood cost and slot availability)
        boolean success = player.placeCard(cardSlot, card);
        
        if (success) {
            System.out.println(player.strPlayerName + " played " + card.strName + " in slot " + cardSlot);
            animationPanel.repaint();
        } else {
            System.out.println("Failed to play card. Check blood cost or slot availability.");
        }
        
        return success;
    }
    
}