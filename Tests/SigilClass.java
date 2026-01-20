package Tests;

public abstract class SigilClass {
    // Properties
    protected String strName;
    protected String strDescription;

    // Abstract method that concrete sigil classes must implement
    public abstract void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent);
    
    // Getters
    public String getName() {
        return strName;
    }
    
    public String getDescription() {
        return strDescription;
    }
}
