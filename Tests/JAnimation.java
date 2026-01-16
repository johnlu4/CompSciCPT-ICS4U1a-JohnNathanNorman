package Tests;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JAnimation extends JPanel implements MouseListener {
    // Properties
    private Game game;

    private BufferedImage bg = getImage("Game.png");

    // Utility Methods
    public BufferedImage getImage(String strImagePath){
        BufferedImage Image = null;
        String resourcePath = strImagePath.startsWith("/") ? strImagePath : "/Tests/" + strImagePath;
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            System.out.println("Resource not found: " + resourcePath);
            return null;
        }
        try {
            Image = ImageIO.read(is);
        } catch (Exception e) {
            System.out.println("Failed to read image: " + resourcePath + " -> " + e.getMessage());
            Image = null;
        }
        return Image;
    }
    
    // Methods

    public void initRound() {
        // Initialize round properties - now handled by Game
    }

    @Override
    public void paintComponent(Graphics paint){
        super.paintComponent(paint);

        paint.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        paint.setColor(new Color(95, 78, 51));

        paint.fillRect(0,0, 1280, 720);

        // Draw current phase
        paint.setColor(Color.WHITE);
        if (game != null) {
            paint.drawString("Phase: " + game.getCurrentPhase(), 600, 50);
        }

        // Draw players' names
        if (game != null) {
            if (game.getP1() != null) {
                paint.drawString(game.getP1().strPlayerName, 50, 50);
            }

            if (game.getP2() != null) {
                paint.drawString(game.getP2().strPlayerName, 1080, 50);
            }
        }

    }

    public void setGame(Game game) {
        this.game = game;
        repaint();
    }

    // Constructor
    public JAnimation() {
        super();
        setPreferredSize(new Dimension(1280, 720));
        addMouseListener(this); // Enable mouse listening for game interactions
    }

    // MouseListener methods for handling game inputs
    @Override
    public void mouseClicked(MouseEvent event) {
        if (game != null) {
            int x = event.getX();
            int y = event.getY();
            if (y >= 200 && y <= 300) {
                int slotWidth = 1280 / 4; // 4 slots
                int slotIndex = x / slotWidth;
                if (slotIndex >= 0 && slotIndex < 4) {
                    // Call game method to place card (placeholder - need selected card logic)
                    System.out.println("Clicked on slot " + slotIndex);
                    // game.placeCard(slotIndex, selectedCard); // implement in Game
                }
            }
            // Add more logic for card selection, etc.
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {}

    @Override
    public void mouseReleased(MouseEvent event) {}

    @Override
    public void mouseEntered(MouseEvent event) {}

    @Override
    public void mouseExited(MouseEvent event) {}
}
