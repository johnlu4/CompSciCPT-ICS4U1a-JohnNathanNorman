package Tests;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.awt.image.BufferedImage;


public class PlayerClass {
    // Properties
    public String strPlayerName;
    public int intBlood = 0;
    public int intLives = 2;
    public int intScale = 0;


    public String strDeck[][] = new String[20][5];
    public String strSquirrelDeck[][] = new String[10][5];
    public CardClass[] placedSlots = new CardClass[4];
    
    // Hand and deck tracking
    public ArrayList<CardClass> hand = new ArrayList<>();
    private int deckIndex = 0;
    private int squirrelIndex = 0;
    
    // Drawing phase tracking
    public boolean hasDrawnThisTurn = false;
    public boolean isReady = false;

    // Methods
    public boolean placeCard(int slotIndex, CardClass card){
        if (slotIndex < 0 || slotIndex >= placedSlots.length) return false;
        if (card == null) return false;
        if (placedSlots[slotIndex] != null){
            // check if enough blood w/ sacrifice to replace
            if (card.intCost > intBlood + 1){
                return false;
            }else {
                placedSlots[slotIndex] = null;
                intBlood += 1;
            }
        }
        if (card.intCost > intBlood) return false; // not enough money

        intBlood = 0;
        placedSlots[slotIndex] = card;
        hand.remove(card); // Remove from hand when placed
        return true;
    }

    /**
     * Place a squirrel card in a slot (free - no blood cost)
     * param slotIndex The slot (0-3) to place the squirrel
     * return true if squirrel was placed successfully, false otherwise
     */
    public boolean placeSquirrel(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= placedSlots.length) return false;
        if (placedSlots[slotIndex] != null) return false; // slot occupied
        
        // Draw a squirrel and place it for free
        CardClass squirrel = drawSquirrel();
        if (squirrel == null) {
            System.out.println("No squirrels left in deck!");
            return false;
        }
        
        // Place squirrel without blood cost
        placedSlots[slotIndex] = squirrel;
        hand.remove(squirrel); // Remove from hand when placed
        System.out.println(strPlayerName + " placed a free squirrel in slot " + slotIndex);
        return true;
    }

    /**
     * Draw a card from the main deck and add it to hand
     * @return The drawn CardClass object, or null if deck is empty
     */
    public CardClass drawCard() {
        if (deckIndex >= strDeck.length || strDeck[deckIndex][0] == null) {
            return null; // Deck exhausted
        }
        
        CardClass card = createCardFromDeckData(deckIndex, false);
        if (card != null) {
            hand.add(card);
            deckIndex++;
        }
        return card;
    }

    /**
     * Draw a squirrel card and add it to hand
     * @return The drawn squirrel CardClass object, or null if squirrel deck is empty
     */
    public CardClass drawSquirrel() {
        if (squirrelIndex >= strSquirrelDeck.length || strSquirrelDeck[squirrelIndex][0] == null) {
            return null; // Squirrel deck exhausted
        }
        
        CardClass card = createCardFromDeckData(squirrelIndex, true);
        if (card != null) {
            hand.add(card);
            squirrelIndex++;
        }
        return card;
    }

    /**
     * Create a CardClass instance from String deck data
     * @param index The index in the deck array
     * @param isSquirrel Whether to use squirrel deck or main deck
     * @return A new CardClass instance
     */
    private CardClass createCardFromDeckData(int index, boolean isSquirrel) {
        String[][] sourceDeck = isSquirrel ? strSquirrelDeck : strDeck;
        
        if (sourceDeck[index][0] == null) {
            return null;
        }

        // Parse CSV format: name, cost, HP, attack, sigil
        String name = sourceDeck[index][0];
        int cost = Integer.parseInt(sourceDeck[index][1]);
        int hp = Integer.parseInt(sourceDeck[index][2]);
        int attack = Integer.parseInt(sourceDeck[index][3]);
        String sigil = sourceDeck[index][4];

        // Create and return a CardClass instance
        int[] stats = {hp, attack, cost};
        return new CardClass(name, null, stats, sigil);
    }

    /**
     * Reset deck indices for a new game
     */
    public void resetDeck() {
        deckIndex = 0;
        squirrelIndex = 0;
        hand.clear();
        intScale = 0;
        for (int i = 0; i < placedSlots.length; i++) {
            placedSlots[i] = null;
        }
    }
    
    /**
     * Reset drawing phase tracking
     */
    public void resetDrawPhase() {
        hasDrawnThisTurn = false;
        isReady = false;
    }

    // Constructor
    public PlayerClass(String strPlayerName) {
        this.strPlayerName = strPlayerName;

    }
}
