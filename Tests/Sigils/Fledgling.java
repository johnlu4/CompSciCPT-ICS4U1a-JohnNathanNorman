package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class Fledgling extends SigilClass {
    
    public Fledgling() {
        this.strName = "Fledgling";
        this.strDescription = "Gains +1 attack after surviving a turn";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent) {
        System.out.println(card.strName + " uses Fledgling!");
        card.intAttack++;
        System.out.println(card.strName + " attack increased to " + card.intAttack);
    }
}
