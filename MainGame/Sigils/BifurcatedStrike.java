package MainGame.Sigils;

import MainGame.CardClass;
import MainGame.PlayerClass;
import MainGame.SigilClass;

public class BifurcatedStrike extends SigilClass {

    public BifurcatedStrike() {
        this.strName = "Bifurcated Strike";
        this.strDescription = "Attacks each opposing space to the left and right of the attacking card";
        this.strSigilActivation = "AttackPhase";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " uses Bifurcated Strike!");
        
        int intDamage = card.intAttack;
        
        // Attack left slot (slotIndex - 1)
        if (intSlotIndex > 0) {
            CardClass leftCard = opponent.placedSlots[intSlotIndex - 1];
            if (leftCard != null && leftCard.intHealth > 0) {
                leftCard.intHealth -= intDamage;
                System.out.println("  → Bifurcated Strike hits " + leftCard.strName + " (left) for " + intDamage + " damage! (HP: " + leftCard.intHealth + ")");
                if (leftCard.intHealth <= 0) {
                    System.out.println("  → " + leftCard.strName + " was destroyed!");
                    opponent.placedSlots[intSlotIndex - 1] = null;
                    opponent.intBlood += 1;
                }
            } else {
                // Empty slot - direct damage to scale
                player.intScale += intDamage;
                System.out.println("  → Bifurcated Strike hits empty slot (left)! Direct damage to opponent's scale: +" + intDamage);
            }
        }
        
        // Attack right slot (slotIndex + 1)
        if (intSlotIndex < 3) {
            CardClass rightCard = opponent.placedSlots[intSlotIndex + 1];
            if (rightCard != null && rightCard.intHealth > 0) {
                rightCard.intHealth -= intDamage;
                System.out.println("  → Bifurcated Strike hits " + rightCard.strName + " (right) for " + intDamage + " damage! (HP: " + rightCard.intHealth + ")");
                if (rightCard.intHealth <= 0) {
                    System.out.println("  → " + rightCard.strName + " was destroyed!");
                    opponent.placedSlots[intSlotIndex + 1] = null;
                    opponent.intBlood += 1;
                }
            } else {
                // Empty slot - direct damage to scale
                player.intScale += intDamage;
                System.out.println("  → Bifurcated Strike hits empty slot (right)! Direct damage to opponent's scale: +" + intDamage);
            }
        }
    }
}
