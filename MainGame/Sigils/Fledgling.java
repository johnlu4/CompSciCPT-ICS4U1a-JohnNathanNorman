package MainGame.Sigils;

import MainGame.CardClass;
import MainGame.PlayerClass;
import MainGame.SigilClass;

public class Fledgling extends SigilClass {
    
    public Fledgling() {
        this.strName = "Fledgling";
        this.strDescription = "Gains +1 attack after surviving a turn";
        this.strSigilActivation = "DrawingPhase";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " uses Fledgling!");
        
        // Transform specific cards based on their name
        if (card.strName.equalsIgnoreCase("Wolf Cub")) {
            // Transform Wolf Cub into Wolf (stats from extracards.csv: 3 cost, 2 attack, 2 HP)
            card.strName = "Wolf";
            card.intCost = 3;
            card.intAttack = 2;
            card.intHealth = 2;
            card.setSigil(null); // Wolf has no sigil
            System.out.println("  → Wolf Cub evolved into Wolf! (Cost: 3, ATK: 2, HP: 2)");
        } else if (card.strName.equalsIgnoreCase("Raven Egg")) {
            // Transform Raven Egg into Raven (stats from extracards.csv: 2 cost, 3 attack, 2 HP, airborne)
            card.strName = "Raven";
            card.intCost = 2;
            card.intAttack = 3;
            card.intHealth = 2;
            // Raven has Airborne sigil
            card.setSigil(new MainGame.Sigils.Airborne());
            System.out.println("  → Raven Egg hatched into Raven! (Cost: 2, ATK: 3, HP: 2, Sigil: Airborne)");
        } else {
            // Default behavior: just increase attack by 1
            card.intAttack++;
            System.out.println("  → " + card.strName + " evolved! Attack increased to " + card.intAttack);
        }
    }
}
