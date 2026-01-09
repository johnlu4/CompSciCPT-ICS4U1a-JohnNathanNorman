package Tests;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class PlayerClass {
    // Properties
    public String strPlayerName;
    public int intBlood = 0;
    public int intLives = 2;


    private List<CardClass> drawPile = new ArrayList<>();
    private CardClass[] placedSlots = new CardClass[4];

    // Methods
    public CardClass drawCard(){
        return drawPile.remove(0);
    }

    public boolean placeCard(int slotIndex, CardClass card){
        if (slotIndex < 0 || slotIndex >= placedSlots.length) return false;
        if (card == null) return false;
        if (placedSlots[slotIndex] != null) return false; // slot occupied
        if (card.intCost > intBlood) return false; // not enough money

        intBlood -= card.intCost;
        placedSlots[slotIndex] = card;
        return true;
    }



    // Constructor
    public PlayerClass(String strPlayerName) {
        this.strPlayerName = strPlayerName;
    }
}
