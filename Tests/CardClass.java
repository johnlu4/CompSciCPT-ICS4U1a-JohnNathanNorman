package Tests;

import java.awt.image.BufferedImage;

import Tests.Sigils.BifurcatedStrike;
import Tests.Sigils.Fledgling;
import Tests.Sigils.MightyLeap;
import Tests.Sigils.RabbitHole;
import Tests.Sigils.Stinky;
import Tests.Sigils.TouchOfDeath;
import Tests.Sigils.TrifurcatedStrike;

public class CardClass {
    // Properties
    public String strName;
    public BufferedImage imgCardImage;
    
    public int intHealth;
    public int intAttack;
    public int intCost;
    private SigilClass sigil = null;  // Card HAS-A sigil (composition)
    public boolean blnRevealed = false; // Track if card has been revealed to opponent

    // Methods

    public void attack() {
        System.out.println(strName + " attacks for " + intAttack + " damage!");
    }
    
    public void sigilActivate(PlayerClass player, PlayerClass opponent) {
        if (sigil != null) {
            sigil.activateSigilEffect(this, player, opponent);
        }
    }
    
    // Getter and setter for sigil
    public SigilClass getSigil() {
        return sigil;
    }
    
    public void setSigil(SigilClass sigil) {
        this.sigil = sigil;
    }
    
    public boolean hasSigil() {
        return sigil != null;
    }

    // Constructor with SigilClass object
    public CardClass(String strName, BufferedImage imgCardImage, int[] arrStats, SigilClass sigil) {
        // Initialization code
        this.strName = strName;
        this.imgCardImage = imgCardImage;
        this.intHealth = arrStats[0];
        this.intAttack = arrStats[1];
        this.intCost = arrStats[2];
        this.sigil = sigil;
    }
    
    // Constructor with String sigil name (for backwards compatibility)
    public CardClass(String strName, BufferedImage imgCardImage, int[] arrStats, String strSigil) {
        this.strName = strName;
        this.imgCardImage = imgCardImage;
        this.intHealth = arrStats[0];
        this.intAttack = arrStats[1];
        this.intCost = arrStats[2];
        this.sigil = parseSigil(strSigil);
    }
    
    // Helper method to convert string sigil name to SigilClass object
    private SigilClass parseSigil(String strSigil) {
        if (strSigil == null || strSigil.equalsIgnoreCase("N/A")) {
            return null;
        }
        
        switch (strSigil.toLowerCase()) {
            case "bifurcated strike":
                return new BifurcatedStrike();
            case "trifurcated strike":
                return new TrifurcatedStrike();
            case "mighty leap":
                return new MightyLeap();
            case "touch of death":
                return new TouchOfDeath();
            case "fledgling":
                return new Fledgling();
            case "rabbit hole":
                return new RabbitHole();
            case "stinky":
                return new Stinky();
            default:
                System.out.println("Unknown sigil: " + strSigil);
                return null;
        }
    }
}

