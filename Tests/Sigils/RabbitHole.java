package Tests.Sigils;

import Tests.CardClass;
import Tests.PlayerClass;
import Tests.SigilClass;

public class RabbitHole extends SigilClass {
    
    public RabbitHole() {
        this.strName = "Rabbit Hole";
        this.strDescription = "When played, creates a Rabbit in your hand";
        this.strSigilActivation = "DrawingPhase";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        System.out.println(card.strName + " uses Rabbit Hole!");
        
        // Create a Rabbit card with stats from extracards.csv: 0 cost, 0 attack, 1 HP
        int[] rabbitStats = {1, 0, 0}; // HP, Attack, Cost
        CardClass rabbit = new CardClass("Rabbit", null, rabbitStats, (String)null);
        
        // Add rabbit to player's hand
        player.hand.add(rabbit);
        System.out.println("  → A Rabbit appeared in " + player.strPlayerName + "'s hand! (Cost: 0, ATK: 0, HP: 1)");
    }
}
