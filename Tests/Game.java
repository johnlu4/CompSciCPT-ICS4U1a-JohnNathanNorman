package Tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {
    // Properties
    private PlayerClass p1;
    private PlayerClass p2;
    public boolean blnStarted = false;
    public boolean isInitializationPhase = true; // True during first round
    private String currentPhase = "DrawingPhase";
    private JAnimation animationPanel;
    private SuperSocketMaster ssm;
    private Main mainInstance;
    private boolean blnIsHost; // Track if this client is the host
    
    // Attack animation queue
    private List<AttackAction> attackQueue = new ArrayList<>();
    private boolean isProcessingAttacks = false;
    private int intCurrentAttackIndex = 0;

    String strBigDeck[][] = new String[56][6];
    String strEvoDeck[][] = new String[2][4];

    // Constructor
    public Game(PlayerClass p1, PlayerClass p2, JAnimation animationPanel, SuperSocketMaster ssm, Main mainInstance, boolean blnIsHost) {
        this.p1 = p1;
        this.p2 = p2;
        this.animationPanel = animationPanel;
        this.ssm = ssm;
        this.mainInstance = mainInstance;
        this.blnIsHost = blnIsHost;
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
                String sigilName = card.getSigil() != null ? card.getSigil().getName() : "N/A";
                System.out.println("  " + (i+1) + ". " + card.strName + " (Cost:" + card.intCost + ", HP:" + card.intHealth + ", ATK:" + card.intAttack + ", Sigil:" + sigilName + ")");
            }
        }
        CardClass squirrel1 = p1.drawSquirrel();
        if (squirrel1 != null) {
            String sigilName = squirrel1.getSigil() != null ? squirrel1.getSigil().getName() : "N/A";
            System.out.println("  5. " + squirrel1.strName + " (Cost:" + squirrel1.intCost + ", HP:" + squirrel1.intHealth + ", ATK:" + squirrel1.intAttack + ", Sigil:" + sigilName + ")");
        }
        
        System.out.println("Player 2 draws:");
        for (int i = 0; i < 4; i++) {
            CardClass card = p2.drawCard();
            if (card != null) {
                String sigilName = card.getSigil() != null ? card.getSigil().getName() : "N/A";
                System.out.println("  " + (i+1) + ". " + card.strName + " (Cost:" + card.intCost + ", HP:" + card.intHealth + ", ATK:" + card.intAttack + ", Sigil:" + sigilName + ")");
            }
        }
        CardClass squirrel2 = p2.drawSquirrel();
        if (squirrel2 != null) {
            String sigilName = squirrel2.getSigil() != null ? squirrel2.getSigil().getName() : "N/A";
            System.out.println("  5. " + squirrel2.strName + " (Cost:" + squirrel2.intCost + ", HP:" + squirrel2.intHealth + ", ATK:" + squirrel2.intAttack + ", Sigil:" + sigilName + ")");
        }
        System.out.println("Each player drew 4 cards + 1 squirrel to start\n");

        // Set initial phase
        currentPhase = "DrawingPhase";
        
        // Reset draw flags for the first drawing phase (they shouldn't draw yet, but flag should be ready)
        p1.resetDrawPhase();
        p2.resetDrawPhase();

        // Notify animation panel
        animationPanel.repaint();

        blnStarted = true;
    }

    // Initialize randomized decks for both players using InscryptionDeck logic Reads bloodcardlist.csv and Squireldeck.csv to populate player decks
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
                
                // Send system message for phase change
                if (mainInstance != null) {
                    mainInstance.SendSystemMessage("~~ Attack Phase ~~");
                }
                
                // Notify other client of phase change
                if (ssm != null) {
                    ssm.sendText("NEXT_PHASE");
                }
                
                // End initialization phase after first ready-up
                if (isInitializationPhase) {
                    isInitializationPhase = false;
                    System.out.println("Initialization phase complete");
                }
                
                // Only host executes attack phase, then sends results
                if (blnIsHost) {
                    executeAttackPhase();
                }
                // Client just waits for attack animations and damage updates from host
            } else {
                System.out.println("Waiting for players to ready up...");
            }
        }
        // Note: Removed the "AttackPhase" else-if to prevent double execution
        // The attack phase should only execute once when transitioning from DrawingPhase
        // Add more phases as needed
        animationPanel.repaint();
    }

    // Player draws from their main deck param playerNumber 1 for P1, 2 for P2
    public boolean playerDrawCard(int intPlayerNumber) {
        PlayerClass player = (intPlayerNumber == 1) ? p1 : p2;
        
        // Can't draw during initialization phase
        if (isInitializationPhase) {
            System.out.println("Cannot draw cards during the first Drawing Phase!");
            return false;
        }
        
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

    // Player draws a squirrel card param playerNumber 1 for P1, 2 for P2
    public boolean playerDrawSquirrel(int intPlayerNumber) {
        PlayerClass player = (intPlayerNumber == 1) ? p1 : p2;
        
        // Can't draw during initialization phase
        if (isInitializationPhase) {
            System.out.println("Cannot draw cards during the first Drawing Phase!");
            return false;
        }
        
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

    // Player readies up for next phase param playerNumber 1 for P1, 2 for P2
    public void playerReady(int intPlayerNumber) {
        PlayerClass player = (intPlayerNumber == 1) ? p1 : p2;
        if(player.isReady == true){
            System.out.println(player.strPlayerName + " is already ready!");
            return;
        }
        player.isReady = true;
        System.out.println(player.strPlayerName + " is ready!");
        
        // Send ready message to both clients (via Main's SendSystemMessage)
        if (intPlayerNumber == 1 && mainInstance != null) {
            mainInstance.SendSystemMessage(player.strPlayerName + " is ready!");
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

    // Execute the attack phase with alternating attacks from bottom and top slots
    // Bottom slot 0, Top slot 0, Bottom slot 1, Top slot 1, etc.
    private void executeAttackPhase() {
        System.out.println("\n=== ATTACK PHASE START ===");
        
        // Build attack queue
        attackQueue.clear();
        intCurrentAttackIndex = 0;
        
        // Attack left to right, alternating bottom (P1) and top (P2)
        for (int slot = 0; slot < 4; slot++) {
            // Bottom slot attacks (Player 1)
            CardClass bottomCard = p1.placedSlots[slot];
            if (bottomCard != null && bottomCard.intAttack > 0) {
                attackQueue.add(new AttackAction(p1, p2, slot, bottomCard, true));
            }
            
            // Top slot attacks (Player 2)
            CardClass topCard = p2.placedSlots[slot];
            if (topCard != null && topCard.intAttack > 0) {
                attackQueue.add(new AttackAction(p2, p1, slot, topCard, false));
            }
        }
        
        // Start processing attacks
        if (!attackQueue.isEmpty()) {
            isProcessingAttacks = true;
            processNextAttack();
        } else {
            System.out.println("No attacks this phase");
            System.out.println("\n=== ATTACK PHASE END ===");
            completeAttackPhase();
        }
    }
    
    // Public method for client to execute attack phase when commanded by host
    public void executeAttackPhaseFromNetwork() {
        executeAttackPhase();
    }
    
    // Process the next attack in the queue
    private void processNextAttack() {
        if (intCurrentAttackIndex >= attackQueue.size()) {
            // All attacks processed
            System.out.println("\n=== ATTACK PHASE END ===");
            isProcessingAttacks = false;
            completeAttackPhase();
            return;
        }
        
        AttackAction action = attackQueue.get(intCurrentAttackIndex);
        
        // Check if the attacking card is still alive (HP > 0)
        // If the card died from a previous attack, skip this attack
        if (action.attackingCard.intHealth <= 0) {
            System.out.println("\n[Skipping] " + action.attackingCard.strName + " is destroyed and cannot attack");
            intCurrentAttackIndex++;
            processNextAttack();
            return;
        }
        
        String playerName = action.isBottomAttacking ? p1.strPlayerName : p2.strPlayerName;
        System.out.println("\n[" + playerName + " Slot " + action.intSlotIndex + "] " + action.attackingCard.strName + " attacks!");
        
        // Send attack animation to client (if host)
        if (blnIsHost && ssm != null) {
            ssm.sendText("ATTACK_ANIM:" + action.intSlotIndex + ":" + action.isBottomAttacking);
        }
        
        // Start animation
        animationPanel.startAttackAnimation(action.attackingCard, action.intSlotIndex, action.isBottomAttacking);
        
        // Perform the actual attack logic (damage calculation)
        performAttack(action.attacker, action.defender, action.intSlotIndex, action.attackingCard, action.isBottomAttacking);
    }
    
    // Called when attack animation completes
    public void onAttackAnimationComplete() {
        // Only host progresses through attack queue
        if (blnIsHost) {
            intCurrentAttackIndex++;
            processNextAttack();
        }
        // Client does nothing - waits for next ATTACK_ANIM message from host
    }
    
    // Complete the attack phase and transition to next phase
    
    private void completeAttackPhase() {
        System.out.println("Current Scale - P1: " + p1.intScale + " | P2: " + p2.intScale);
        animationPanel.repaint();
        
        // Check scale difference after attack phase
        checkScaleDifference();
        
        currentPhase = "DrawingPhase";
        // Reset drawing phase tracking for both players
        p1.resetDrawPhase();
        p2.resetDrawPhase();
        System.out.println("Drawing Phase started - Players can draw a card");
        
        // Send system message
        if (mainInstance != null) {
            mainInstance.SendSystemMessage("~~ Drawing Phase ~~");
        }
        
        // Notify other client of phase change back to drawing
        if (ssm != null) {
            ssm.sendText("RETURN_TO_DRAWING");
        }
        
        animationPanel.repaint();
    }
    
    //Inner class to store attack actions
    private class AttackAction {
        PlayerClass attacker;
        PlayerClass defender;
        int intSlotIndex;
        CardClass attackingCard;
        boolean isBottomAttacking;
        
        AttackAction(PlayerClass attacker, PlayerClass defender, int intSlotIndex, CardClass attackingCard, boolean isBottomAttacking) {
            this.attacker = attacker;
            this.defender = defender;
            this.intSlotIndex = intSlotIndex;
            this.attackingCard = attackingCard;
            this.isBottomAttacking = isBottomAttacking;
        }
    }

    // Perform an attack from an attacking card to the opposite slot
    private void performAttack(PlayerClass attacker, PlayerClass defender, int intSlotIndex, CardClass attackingCard, boolean isBottomAttacking) {
        int intDamage = attackingCard.intAttack;
        CardClass opposingCard = defender.placedSlots[intSlotIndex];
        
        // Treat cards with 0 or less HP as empty slots (already destroyed)
        if (opposingCard != null && opposingCard.intHealth <= 0) {
            System.out.println("  → Opposing card is already destroyed, treating as empty slot");
            defender.placedSlots[intSlotIndex] = null;
            opposingCard = null;
        }
        
        if (opposingCard != null) {
            // There's a card in the opposite slot - damage it
            System.out.println("  → Attacking " + opposingCard.strName + " (HP: " + opposingCard.intHealth + ")");
            opposingCard.intHealth -= intDamage;
            System.out.println("  → " + opposingCard.strName + " takes " + intDamage + " damage! (HP now: " + opposingCard.intHealth + ")");
            
            // Remove card immediately if health drops to 0 or below
            if (opposingCard.intHealth <= 0) {
                System.out.println("  → " + opposingCard.strName + " was destroyed!");
                defender.placedSlots[intSlotIndex] = null;
                
                // Add blood to defender (for sacrificing in next turn)
                defender.intBlood += 1;
                System.out.println("  → " + defender.strPlayerName + " gains 1 blood (now: " + defender.intBlood + ")");
                
                // Send damage result to client (if host)
                if (blnIsHost && ssm != null) {
                    ssm.sendText("DAMAGE_RESULT:" + intSlotIndex + ":" + isBottomAttacking + ":destroyed:" + defender.intBlood);
                }
                
                // Trigger repaint to show card destruction immediately
                animationPanel.repaint();
            } else {
                // Send damage result to client (if host)
                if (blnIsHost && ssm != null) {
                    ssm.sendText("DAMAGE_RESULT:" + intSlotIndex + ":" + isBottomAttacking + ":" + opposingCard.intHealth + ":" + defender.intBlood);
                }
            }
        } else {
            // Empty slot - direct damage to opponent's scale
            System.out.println("  → No opposing card! Direct damage to " + defender.strPlayerName);
            if (isBottomAttacking) {
                // P1 attacking, damage P2's scale (increase P1's scale)
                attacker.intScale += intDamage;
                System.out.println("  → " + attacker.strPlayerName + "'s scale increases by " + intDamage + " (now: " + attacker.intScale + ")");
            } else {
                // P2 attacking, damage P1's scale (decrease P1's scale)
                attacker.intScale += intDamage;
                System.out.println("  → " + attacker.strPlayerName + "'s scale increases by " + intDamage + " (now: " + attacker.intScale + ")");
            }
            
            // Send scale update to client (if host)
            if (blnIsHost && ssm != null) {
                ssm.sendText("SCALE_UPDATE:" + p1.intScale + ":" + p2.intScale);
            }
        }
    }

    // Check scale difference between players
    private void checkScaleDifference() {
        int intScaleDiff = p1.intScale - p2.intScale;
        
        if (intScaleDiff >= 5) {
            // P1 is dominating, P2 loses a life
            p2.intLives -= 1;
            System.out.println("Scale tipped! " + p2.strPlayerName + " loses 1 life. Lives remaining: " + p2.intLives);
            
            // Send system message
            if (mainInstance != null) {
                mainInstance.SendSystemMessage("Scale tipped! " + p2.strPlayerName + " loses 1 life! Lives: " + p2.intLives);
            }
            
            p1.intScale = 0;
            p2.intScale = 0;
            
            // Send scale reset and life update to client (if host)
            if (blnIsHost && ssm != null) {
                ssm.sendText("SCALE_UPDATE:" + p1.intScale + ":" + p2.intScale);
                ssm.sendText("LIFE_UPDATE:" + p1.intLives + ":" + p2.intLives);
            }
            
            animationPanel.repaint();
        } else if (intScaleDiff <= -5) {
            // P2 is dominating, P1 loses a life
            p1.intLives -= 1;
            System.out.println("Scale tipped! " + p1.strPlayerName + " loses 1 life. Lives remaining: " + p1.intLives);
            
            // Send system message
            if (mainInstance != null) {
                mainInstance.SendSystemMessage("Scale tipped! " + p1.strPlayerName + " loses 1 life! Lives: " + p1.intLives);
            }
            
            p1.intScale = 0;
            p2.intScale = 0;
            
            // Send scale reset and life update to client (if host)
            if (blnIsHost && ssm != null) {
                ssm.sendText("SCALE_UPDATE:" + p1.intScale + ":" + p2.intScale);
                ssm.sendText("LIFE_UPDATE:" + p1.intLives + ":" + p2.intLives);
            }
            
            animationPanel.repaint();
        }
    }

    // methods for gameplay logic, e.g., executeAttack, checkWinCondition, etc.
    /**
     * Attempts to play a card from a player's hand to the board
     * @param playerNumber The player number (1 or 2)
     * @param card The CardClass object to play
     * @param cardSlot The slot index (0-3) to place the card
     * @return true if card was successfully played, false otherwise
     */
    public boolean playCard(int intPlayerNumber, CardClass card, int intCardSlot) {
        PlayerClass player = (intPlayerNumber == 1) ? p1 : p2;
        
        // Validate inputs
        if (card == null) {
            System.out.println("Error: Card is null");
            return false;
        }
        
        if (intCardSlot < 0 || intCardSlot > 3) {
            System.out.println("Error: Invalid card slot " + intCardSlot);
            return false;
        }
        
        // Check if it's the correct phase
        if (!currentPhase.equals("DrawingPhase")) {
            System.out.println("Error: Can only play cards during Drawing Phase");
            return false;
        }
        
        // Attempt to place the card (this checks blood cost and slot availability)
        boolean success = player.placeCard(intCardSlot, card);
        
        if (success) {
            System.out.println(player.strPlayerName + " played " + card.strName + " in slot " + intCardSlot);
            animationPanel.repaint();
        } else {
            System.out.println("Failed to play card. Check blood cost or slot availability.");
        }
        
        return success;
    }
    
    /**
     * Sync client back to drawing phase after host completes attack phase
     */
    public void syncReturnToDrawing() {
        currentPhase = "DrawingPhase";
        // Reset drawing phase tracking for both players
        p1.resetDrawPhase();
        p2.resetDrawPhase();
        System.out.println("Synced to Drawing Phase - Players can draw a card");
        animationPanel.repaint();
    }
    
}