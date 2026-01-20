package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class MightyLeap extends SigilClass {
    
    public MightyLeap() {
        this.strName = "Mighty Leap";
        this.strDescription = "Attacks the opposing player directly, ignoring blockers";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent) {
        System.out.println(card.strName + " uses Mighty Leap!");
        player.intScale += 1;
        System.out.println("Direct damage to opponent! Player scale: " + player.intScale);
    }
}
