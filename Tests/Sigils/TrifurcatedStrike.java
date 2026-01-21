package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class TrifurcatedStrike extends SigilClass {

    public String strSigilActivation = "AttackPhase";
    
    public TrifurcatedStrike() {
        this.strName = "Trifurcated Strike";
        this.strDescription = "Attacks each opposing space to the left, right, and center";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " uses Trifurcated Strike!");
        
        int intDamage = card.intAttack;
        
        // Attack left slot (slotIndex - 1)
        if (intSlotIndex > 0) {
            CardClass leftCard = opponent.placedSlots[intSlotIndex - 1];
            if (leftCard != null && leftCard.intHealth > 0) {
                leftCard.intHealth -= intDamage;
                System.out.println("  → Trifurcated Strike hits " + leftCard.strName + " (left) for " + intDamage + " damage! (HP: " + leftCard.intHealth + ")");
                if (leftCard.intHealth <= 0) {
                    System.out.println("  → " + leftCard.strName + " was destroyed!");
                    opponent.placedSlots[intSlotIndex - 1] = null;
                    opponent.intBlood += 1;
                }
            } else {
                player.intScale += intDamage;
                System.out.println("  → Trifurcated Strike hits empty slot (left)! Direct damage: +" + intDamage);
            }
        }
        
        // Attack center slot (slotIndex)
        CardClass centerCard = opponent.placedSlots[intSlotIndex];
        if (centerCard != null && centerCard.intHealth > 0) {
            centerCard.intHealth -= intDamage;
            System.out.println("  → Trifurcated Strike hits " + centerCard.strName + " (center) for " + intDamage + " damage! (HP: " + centerCard.intHealth + ")");
            if (centerCard.intHealth <= 0) {
                System.out.println("  → " + centerCard.strName + " was destroyed!");
                opponent.placedSlots[intSlotIndex] = null;
                opponent.intBlood += 1;
            }
        } else {
            player.intScale += intDamage;
            System.out.println("  → Trifurcated Strike hits empty slot (center)! Direct damage: +" + intDamage);
        }
        
        // Attack right slot (slotIndex + 1)
        if (intSlotIndex < 3) {
            CardClass rightCard = opponent.placedSlots[intSlotIndex + 1];
            if (rightCard != null && rightCard.intHealth > 0) {
                rightCard.intHealth -= intDamage;
                System.out.println("  → Trifurcated Strike hits " + rightCard.strName + " (right) for " + intDamage + " damage! (HP: " + rightCard.intHealth + ")");
                if (rightCard.intHealth <= 0) {
                    System.out.println("  → " + rightCard.strName + " was destroyed!");
                    opponent.placedSlots[intSlotIndex + 1] = null;
                    opponent.intBlood += 1;
                }
            } else {
                player.intScale += intDamage;
                System.out.println("  → Trifurcated Strike hits empty slot (right)! Direct damage: +" + intDamage);
            }
        }
    }
}
