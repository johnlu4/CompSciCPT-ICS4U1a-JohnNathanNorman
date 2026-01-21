package Tests;

public abstract class SigilClass {
    // Properties
    protected String strName;
    protected String strDescription;
    protected String strSigilActivation = "Passive"; // Default activation phase

    // Abstract method that concrete sigil classes must implement
    public abstract void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex);
    
    // Getters
    public String getName() {
        return strName;
    }
    
    public String getDescription() {
        return strDescription;
    }
    
    public String getActivationPhase() {
        return strSigilActivation;
    }
}
