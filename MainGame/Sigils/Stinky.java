package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class Stinky extends SigilClass {
    
    public Stinky() {
        this.strName = "Stinky";
        this.strDescription = "Opposing creatures adjacent to this card lose 1 attack";
        this.strSigilActivation = "DrawingPhase";
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
        
        // Reduce attack of adjacent opposing cards by 1 (minimum 0)
        // Left adjacent slot
        if (intSlotIndex > 0) {
            CardClass leftCard = opponent.placedSlots[intSlotIndex - 1];
            if (leftCard != null && leftCard.intAttack > 0) {
                leftCard.intAttack = Math.max(0, leftCard.intAttack - 1);
                System.out.println("  → " + leftCard.strName + " is affected by Stinky! Attack reduced to " + leftCard.intAttack);
            }
        }
        
        // Right adjacent slot
        if (intSlotIndex < 3) {
            CardClass rightCard = opponent.placedSlots[intSlotIndex + 1];
            if (rightCard != null && rightCard.intAttack > 0) {
                rightCard.intAttack = Math.max(0, rightCard.intAttack - 1);
                System.out.println("  → " + rightCard.strName + " is affected by Stinky! Attack reduced to " + rightCard.intAttack);
            }
        }
    }
}
