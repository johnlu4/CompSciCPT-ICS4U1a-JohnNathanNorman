package Tests;

public class Game {
    // Properties
    private PlayerClass p1;
    private PlayerClass p2;
    private String currentPhase = "DrawingPhase";
    private JAnimation animationPanel;
    
    public int intScale = 0;

    // Constructor
    public Game(PlayerClass p1, PlayerClass p2, JAnimation animationPanel) {
        this.p1 = p1;
        this.p2 = p2;
        this.animationPanel = animationPanel;
        animationPanel.setGame(this);
    }

    // Methods
    public void startGame() {
        // Initialize player decks, blood, etc.
        // For now, assume decks are set elsewhere
        p1.intBlood = 0; // Example starting blood
        p2.intBlood = 0;

        // Set initial phase
        currentPhase = "DrawingPhase";

        // Notify animation panel
        animationPanel.repaint();
    }

    public void nextPhase() {
        if (currentPhase.equals("DrawingPhase")) {
            currentPhase = "AttackPhase";
        } else if (currentPhase.equals("AttackPhase")) {
            currentPhase = "DrawingPhase";
            // Reset for next turn, e.g., draw cards
        }
        // Add more phases as needed
        animationPanel.repaint();
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public PlayerClass getP1() {
        return p1;
    }

    public PlayerClass getP2() {
        return p2;
    }

    // methods for gameplay logic, e.g., executeAttack, checkWinCondition, etc.


}