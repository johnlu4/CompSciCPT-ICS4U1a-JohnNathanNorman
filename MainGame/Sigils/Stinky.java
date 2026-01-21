package MainGame.Sigils;

import MainGame.CardClass;
import MainGame.PlayerClass;
import MainGame.SigilClass;

public class Stinky extends SigilClass {
    
    public Stinky() {
        this.strName = "Stinky";
        this.strDescription = "Opposing creatures adjacent to this card lose 1 attack";
        this.strSigilActivation = "AttackPhase";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " uses Stinky!");
        
        // Reduce attack of opposing card directly in front by 1 (minimum 0)
        CardClass frontCard = opponent.placedSlots[intSlotIndex];
        if (frontCard != null && frontCard.intAttack > 0) {
            frontCard.intAttack = Math.max(0, frontCard.intAttack - 1);
            System.out.println("  → " + frontCard.strName + " is affected by Stinky! Attack reduced to " + frontCard.intAttack);
        }
    }
}
