package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class TouchOfDeath extends SigilClass {
    
    public TouchOfDeath() {
        this.strName = "Touch of Death";
        this.strDescription = "Instantly kills any card it damages";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent) {
        System.out.println(card.strName + " uses Touch of Death!");
        // TODO: Implement instant kill logic for damaged card
    }
}
