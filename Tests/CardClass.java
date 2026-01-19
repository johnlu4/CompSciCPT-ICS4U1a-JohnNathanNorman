package Tests;

import java.awt.image.BufferedImage;

public class CardClass {
    // Properties
    public String strName;
    public BufferedImage imgCardImage;
    
    public int intHealth;
    public int intAttack;
    public int intCost;
    public String strSigil = null;

    // Methods

    public void attack() {
        System.out.println(strName + " attacks for " + intAttack + " damage!");
    }
    
    public void sigilActivate(PlayerClass p){

    }


    // Constructor
    public CardClass(String strName, BufferedImage imgCardImage, int[] arrStats, String strSigil) {
        // Initialization code
        this.strName = strName;
        this.imgCardImage = imgCardImage;
        this.intHealth = arrStats[0];
        this.intAttack = arrStats[1];
        this.intCost = arrStats[2];
        this.strSigil = strSigil;
    }    
}
