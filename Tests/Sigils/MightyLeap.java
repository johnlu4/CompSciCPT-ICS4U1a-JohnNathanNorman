package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class MightyLeap extends SigilClass {
    
    public String strSigilActivation = "Passive";

    public MightyLeap() {
        this.strName = "Mighty Leap";
        this.strDescription = "Blocks the opposing creature with Airborne sigil";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " has Mighty Leap!");
        // This is a passive ability - cards with Mighty Leap can block Airborne creatures
        // Implementation is handled in the game's attack phase logic
    }
}
