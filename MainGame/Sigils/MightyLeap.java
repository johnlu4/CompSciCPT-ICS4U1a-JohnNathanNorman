package MainGame.Sigils;

import MainGame.CardClass;
import MainGame.PlayerClass;
import MainGame.SigilClass;

public class MightyLeap extends SigilClass {
    
    public MightyLeap() {
        this.strName = "Mighty Leap";
        this.strDescription = "Blocks the opposing creature with Airborne sigil";
        this.strSigilActivation = "Passive";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " has Mighty Leap!");
        // This is a passive ability - cards with Mighty Leap can block Airborne creatures
        // Implementation is handled in the game's attack phase logic
    }
}
