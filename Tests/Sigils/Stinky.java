package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class Stinky extends SigilClass {
    
    public Stinky() {
        this.strName = "Stinky";
        this.strDescription = "Opposing creatures adjacent to this card lose 1 attack";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent) {
        System.out.println(card.strName + " uses Stinky!");
        // TODO: Implement logic to reduce attack of adjacent opponent cards
    }
}
