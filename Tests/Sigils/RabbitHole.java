package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class RabbitHole extends SigilClass {
    
    public RabbitHole() {
        this.strName = "Rabbit Hole";
        this.strDescription = "When played, creates a Rabbit in your hand";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent) {
        System.out.println(card.strName + " uses Rabbit Hole!");
        // TODO: Implement logic to add rabbit card to player's hand
    }
}
