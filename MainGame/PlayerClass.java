package Tests;

import java.util.ArrayList;


public class PlayerClass{
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
    public int deckIndex = 0;
    public int squirrelIndex = 0;
    
    // Drawing phase tracking
    public boolean hasDrawnThisTurn = false;
    public boolean isReady = false;

    // Methods
    // Place a card in a slot (single sacrifice or no sacrifice)
    public boolean placeCard(int intSlotIndex, CardClass card){
        if (intSlotIndex < 0 || intSlotIndex >= placedSlots.length) return false;
        if (card == null) return false;
        
        // Squirrels (cost 0) cannot be placed on occupied slots
        if (card.intCost == 0 && placedSlots[intSlotIndex] != null){
            System.out.println("Cannot place squirrel on occupied slot - no sacrifice allowed!");
            return false;
        }
        
        if (placedSlots[intSlotIndex] != null){
            // Check if card in slot is already dead (HP <= 0)
            if (placedSlots[intSlotIndex].intHealth <= 0){
                System.out.println("Cannot sacrifice a dead card!");
                return false;
            }
            // check if enough blood w/ sacrifice to replace
            if (card.intCost > intBlood + 1){
                return false;
            }else{
                placedSlots[intSlotIndex] = null;
                intBlood += 1;
            }
        }
        if (card.intCost > intBlood) return false; // not enough blood

        intBlood = 0;
        placedSlots[intSlotIndex] = card;
        hand.remove(card); // Remove from hand when placed
        return true;
    }
    
    // Place a card with multiple sacrifices (for cards requiring 2+ blood)
    public boolean placeCard(int intSlotIndex, CardClass card, ArrayList<Integer> sacrificeSlots){
        if (intSlotIndex < 0 || intSlotIndex >= placedSlots.length) return false;
        if (card == null) return false;
        if (sacrificeSlots == null) sacrificeSlots = new ArrayList<>();
        
        // Squirrels (cost 0) cannot be sacrificed with
        if (card.intCost == 0){
            System.out.println("Squirrels don't cost blood!");
            return false;
        }
        
        // Validate all sacrifice slots
        for (int intSacrificeSlot : sacrificeSlots){
            if (intSacrificeSlot < 0 || intSacrificeSlot >= placedSlots.length){
                System.out.println("Invalid sacrifice slot: " + intSacrificeSlot);
                return false;
            }
            if (placedSlots[intSacrificeSlot] == null){
                System.out.println("Cannot sacrifice empty slot: " + intSacrificeSlot);
                return false;
            }
            if (placedSlots[intSacrificeSlot].intHealth <= 0){
                System.out.println("Cannot sacrifice dead card in slot: " + intSacrificeSlot);
                return false;
            }
        }
        
        // Calculate total blood available (current blood + sacrifices)
        int intTotalBlood = intBlood + sacrificeSlots.size();
        
        // If placing on an occupied slot that's not being sacrificed, add 1 more blood
        if (placedSlots[intSlotIndex] != null && !sacrificeSlots.contains(intSlotIndex)){
            if (placedSlots[intSlotIndex].intHealth > 0){
                intTotalBlood += 1;
            }
        }
        
        // Check if we have enough blood
        if (card.intCost > intTotalBlood){
            System.out.println("Not enough blood! Need: " + card.intCost + ", Have: " + intTotalBlood);
            return false;
        }
        
        // Perform sacrifices
        for (int intSacrificeSlot : sacrificeSlots){
            System.out.println("Sacrificing card in slot " + intSacrificeSlot + ": " + placedSlots[intSacrificeSlot].strName);
            placedSlots[intSacrificeSlot] = null;
        }
        
        // If placing on an occupied slot that wasn't sacrificed, clear it
        if (placedSlots[intSlotIndex] != null && !sacrificeSlots.contains(intSlotIndex)){
            System.out.println("Replacing card in slot " + intSlotIndex);
            placedSlots[intSlotIndex] = null;
        }
        
        // Reset blood to 0 after payment
        intBlood = 0;
        
        // Place the card
        placedSlots[intSlotIndex] = card;
        hand.remove(card); // Remove from hand when placed
        
        System.out.println("Successfully placed " + card.strName + " in slot " + intSlotIndex);
        return true;
    }

    // Place a squirrel card in a slot (free - no blood cost)
    public boolean placeSquirrel(int intSlotIndex){
        if (intSlotIndex < 0 || intSlotIndex >= placedSlots.length) return false;
        if (placedSlots[intSlotIndex] != null) return false; // slot occupied
        
        // Draw a squirrel and place it for free
        CardClass squirrel = drawSquirrel();
        if (squirrel == null){
            System.out.println("No squirrels left in deck!");
            return false;
        }
        
        // Place squirrel without blood cost
        placedSlots[intSlotIndex] = squirrel;
        hand.remove(squirrel); // Remove from hand when placed
        System.out.println(strPlayerName + " placed a free squirrel in slot " + intSlotIndex);
        return true;
    }

    // Draw a card from the main deck and add it to hand
    public CardClass drawCard(){
        if (deckIndex >= strDeck.length || strDeck[deckIndex][0] == null){
            return null; // Deck exhausted
        }
        
        CardClass card = createCardFromDeckData(deckIndex, false);
        if (card != null){
            hand.add(card);
            deckIndex++;
        }
        return card;
    }

    // Draw a squirrel card and add it to hand
    public CardClass drawSquirrel(){
        if (squirrelIndex >= strSquirrelDeck.length || strSquirrelDeck[squirrelIndex][0] == null){
            return null; // Squirrel deck exhausted
        }
        
        CardClass card = createCardFromDeckData(squirrelIndex, true);
        if (card != null){
            hand.add(card);
            squirrelIndex++;
        }
        return card;
    }

    // Create a CardClass instance from String deck data
    private CardClass createCardFromDeckData(int intIndex, boolean isSquirrel){
        String[][] sourceDeck = isSquirrel ? strSquirrelDeck : strDeck;
        
        if (sourceDeck[intIndex][0] == null){
            return null;
        }

        // CSV format: name, cost, HP, attack, sigil
        String name = sourceDeck[intIndex][0];
        int intCost = Integer.parseInt(sourceDeck[intIndex][1]);
        int intHp = Integer.parseInt(sourceDeck[intIndex][2]);
        int intAttack = Integer.parseInt(sourceDeck[intIndex][3]);
        String sigil = sourceDeck[intIndex][4];

        // Create and return a CardClass instance
        int[] stats ={intHp, intAttack, intCost};
        return new CardClass(name, null, stats, sigil);
    }

    
    // Reset drawing phase tracking
    public void resetDrawPhase(){
        hasDrawnThisTurn = false;
        isReady = false;
    }

    // Constructor
    public PlayerClass(String strPlayerName){
        this.strPlayerName = strPlayerName;

    }
}
