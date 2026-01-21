package MainGame.Sigils;

import MainGame.CardClass;
import MainGame.PlayerClass;
import MainGame.SigilClass;

public class RabbitHole extends SigilClass {
    
    private boolean hasActivated = false; // Track if sigil has already activated
    
    public RabbitHole() {
        this.strName = "Rabbit Hole";
        this.strDescription = "When played, creates a Rabbit in your hand";
        this.strSigilActivation = "DrawingPhase";
    }
    
    @Override
    public void activateSigilEffect(CardClass card, PlayerClass player, PlayerClass opponent, int intSlotIndex) {
        // Only activate once
        if (hasActivated) {
            return;
        }
        
        System.out.println(card.strName + " uses Rabbit Hole!");
        
        // Create a Rabbit card with stats from extracards.csv: 0 cost, 0 attack, 1 HP
        int[] rabbitStats = {1, 0, 0}; // HP, Attack, Cost
        CardClass rabbit = new CardClass("Rabbit", null, rabbitStats, (String)null);
        
        // Add rabbit to player's hand
        player.hand.add(rabbit);
        System.out.println("  → A Rabbit appeared in " + player.strPlayerName + "'s hand! (Cost: 0, ATK: 0, HP: 1)");
        
        hasActivated = true; // Mark as activated
    }
}
