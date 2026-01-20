package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class TrifurcatedStrike extends SigilClass {
    
    public TrifurcatedStrike() {
        this.strName = "Trifurcated Strike";
        this.strDescription = "Attacks each opposing space to the left, right, and center";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent) {
        System.out.println(card.strName + " uses Trifurcated Strike!");
        // TODO: Implement the logic to attack three spaces
    }
}
