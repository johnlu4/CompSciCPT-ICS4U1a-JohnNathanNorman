package MainGame.Sigils;

import MainGame.CardClass;
import MainGame.PlayerClass;
import MainGame.SigilClass;

public class TouchOfDeath extends SigilClass {
    
    public TouchOfDeath() {
        this.strName = "Touch of Death";
        this.strDescription = "Instantly kills any card it damages";
        this.strSigilActivation = "AttackPhase";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " uses Touch of Death!");
        
        // Find the opposing card in the same slot
        if (intSlotIndex >= 0 && intSlotIndex < opponent.placedSlots.length) {
            CardClass opposingCard = opponent.placedSlots[intSlotIndex];
            if (opposingCard != null && opposingCard.intHealth > 0) {
                // Instantly kill the opposing card
                opposingCard.intHealth = 0;
                System.out.println("  → " + opposingCard.strName + " was instantly killed by Touch of Death!");
            }
        }

    }
}
