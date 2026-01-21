package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class Airborne extends SigilClass {
    
    public Airborne() {
        this.strName = "Airborne";
        this.strDescription = "strike the opposing player directly, ignoring blockers";
        this.strSigilActivation = "AttackPhase";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " uses Airborne!");
        // Airborne ignores blockers and deals direct damage
        player.intScale += card.intAttack;
        System.out.println("  → Airborne flies over blockers! Direct damage to scale: +" + card.intAttack + " (Total: " + player.intScale + ")");
    }
}