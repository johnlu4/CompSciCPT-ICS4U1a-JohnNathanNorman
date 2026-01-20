package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class BifurcatedStrike extends SigilClass {
    
    public BifurcatedStrike() {
        this.strName = "Bifurcated Strike";
        this.strDescription = "Attacks each opposing space to the left and right of the attacking card";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent) {
        System.out.println(card.strName + " uses Bifurcated Strike!");
        // TODO: Implement the logic to attack adjacent spaces
    }
}
