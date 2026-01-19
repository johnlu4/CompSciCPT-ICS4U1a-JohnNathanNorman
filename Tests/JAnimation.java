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

    public String cardsprites = "cardsprites/";

    private Game game;

    private BufferedImage bg = getImage("Game.png");
    private BufferedImage bellImage = getImage("Bell.png");
    private BufferedImage RegularCardBackImage = getImage("RegularCardBack.png");
    private BufferedImage SquirrelCardBackImage = getImage("SquirrelCardBack.png");
    
    // Selected card tracking
    private int selectedCardIndex = -1; // -1 means no card selected

    // Slot position constants
    // Bottom 4 card slots (Player 1)
    private static final int BOTTOM_SLOT_Y = 500;
    private static final int BOTTOM_SLOT_0_X = 407;
    private static final int BOTTOM_SLOT_1_X = 548;
    private static final int BOTTOM_SLOT_2_X = 800;
    private static final int BOTTOM_SLOT_3_X = 1120;
    
    // Squirrel slot (bottom)
    private static final int BOTTOM_SQUIRREL_X = 1150;
    private static final int BOTTOM_SQUIRREL_Y = 620;
    
    // Slot dimensions
    private static final int SLOT_WIDTH = 320;
    private static final int SLOT_HEIGHT = 140;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 280;
    
    // Bell position constants
    private static final int BELL_X = 991;
    private static final int BELL_Y = 298;
    private static final int BELL_WIDTH = 110;
    private static final int BELL_HEIGHT = 110;
    
    // Hand positioning constants
    private static final int HAND_Y = 575;
    private static final int HAND_CARD_WIDTH = 120;
    private static final int HAND_CARD_HEIGHT = 168;
    private static final int HAND_START_X = 425;
    private static final int HAND_MAX_WIDTH = 500;

    // Utility Methods
    public BufferedImage getImage(String strImagePath){
        BufferedImage Image = null;
        InputStream is = null;
        String resourcePath = null;
        
        // Try multiple locations
        String[] pathsToTry = {
            strImagePath.startsWith("/") ? strImagePath : "/" + strImagePath,
            "/Tests/" + strImagePath,
            "/cardsprites/" + strImagePath
        };
        
        for (String path : pathsToTry) {
            is = getClass().getResourceAsStream(path);
            if (is != null) {
                resourcePath = path;
                break;
            }
        }
        
        if (is == null) {
            System.out.println("Resource not found in any location: " + strImagePath);
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

    public BufferedImage getCardImage(String strCardName){
        // Replace spaces with underscores for filename compatibility
        String fileName = strCardName.replace(" ", "_");
        return getImage(cardsprites + fileName + ".png");
    }

    public BufferedImage placeCardImage(String strCardName, int slotIndex){
        BufferedImage cardImage = getCardImage(strCardName);
        if (cardImage == null) return null;

        int slotWidth = 1280 / 4; // Assuming 4 slots
        BufferedImage placedImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        Graphics g = placedImage.getGraphics();
        g.drawImage(cardImage, slotIndex * slotWidth, 200, slotWidth, 100, null);
        g.dispose();
        return placedImage;
    }
    

    // Methods

    public void initRound() {
        // Initialize round properties - now handled by Game
    }

    @Override
    public void paintComponent(Graphics paint){
        super.paintComponent(paint);

        paint.setColor(new Color(75, 58, 31));

        paint.fillRect(0,0, 1280, 720);
        paint.drawImage(bg, 0, 0, getWidth(), getHeight(), this);

        paint.drawImage(bellImage, 0, 0, 0, 0, this);

        // Draw current phase
        paint.setColor(Color.WHITE);
        if (game != null) {
            paint.drawString("Phase: " + game.getCurrentPhase(), 600, 50);
        }

        // Draw players' names
        if (game != null) {
            if (game.getP1() != null) {
                paint.drawString(game.getP1().strPlayerName, 600, 700);
            }

            if (game.getP2() != null) {
                paint.drawString(game.getP2().strPlayerName, 600, 50);
            }
        }

        // Draw placed cards for both players
        if (game != null) {
            PlayerClass p1 = game.getP1();
            PlayerClass p2 = game.getP2();
            if (p1 != null) {
                for (int i = 0; i < 4; i++) {
                    if (p1.placedSlots[i] != null) {
                        BufferedImage cardImg = placeCardImage(p1.placedSlots[i].strName, i);
                        if (cardImg != null) {
                            paint.drawImage(cardImg, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                }
                // Draw P1's hand at bottom
                drawHand(paint, p1);
            }
            if (p2 != null) {
                for (int i = 0; i < 4; i++) {
                    if (p2.placedSlots[i] != null) {
                        BufferedImage cardImg = placeCardImage(p2.placedSlots[i].strName, i);
                        if (cardImg != null) {
                            paint.drawImage(cardImg, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                }
            }
        }

    }

    /**
     * Draw the player's hand with dynamic card spacing
     * @param g Graphics context
     * @param player The player whose hand to draw
     */
    private void drawHand(Graphics g, PlayerClass player) {
        if (player.hand.isEmpty()) return;
        
        int handSize = player.hand.size();
        // Calculate spacing between cards based on hand size
        int totalSpace = HAND_MAX_WIDTH;
        int cardSpacing;
        
        if (handSize == 1) {
            cardSpacing = 0;
        } else {
            // Compress cards closer as hand size increases
            cardSpacing = Math.min(HAND_CARD_WIDTH, totalSpace / handSize);
        }
        
        // Center the hand
        int startX = HAND_START_X + (HAND_MAX_WIDTH - (cardSpacing * (handSize - 1) + HAND_CARD_WIDTH)) / 2;
        
        for (int i = 0; i < handSize; i++) {
            CardClass card = player.hand.get(i);
            BufferedImage cardImage = getCardImage(card.strName);
            
            int cardX = startX + (i * cardSpacing);
            int cardY = HAND_Y;
            
            // Elevate selected card
            if (i == selectedCardIndex) {
                cardY -= 30; // Move selected card up by 30 pixels
            }
            
            if (cardImage != null) {
                g.drawImage(cardImage, cardX, cardY, HAND_CARD_WIDTH, HAND_CARD_HEIGHT, this);
                
                // Draw card border for visibility
                g.setColor(i == selectedCardIndex ? Color.GREEN : Color.YELLOW);
                g.drawRect(cardX, cardY, HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            } else {
                // Draw placeholder if image not found
                g.setColor(Color.GRAY);
                g.fillRect(cardX, cardY, HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
                g.setColor(Color.WHITE);
                g.drawRect(cardX, cardY, HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
                g.drawString(card.strName, cardX + 5, cardY + 20);
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
            
            // Check bell click (Ready button replacement)
            if (x >= BELL_X && x <= BELL_X + BELL_WIDTH &&
                y >= BELL_Y && y <= BELL_Y + BELL_HEIGHT) {
                System.out.println("Bell clicked - Player ready");
                if (game.getCurrentPhase().equals("DrawingPhase")) {
                    game.playerReady(1);
                }
                return;
            }
            
            // Check hand card clicks
            PlayerClass p1 = game.getP1();
            if (p1 != null && !p1.hand.isEmpty()) {
                int handSize = p1.hand.size();
                int cardSpacing = handSize == 1 ? 0 : Math.min(HAND_CARD_WIDTH, HAND_MAX_WIDTH / handSize);
                int startX = HAND_START_X + (HAND_MAX_WIDTH - (cardSpacing * (handSize - 1) + HAND_CARD_WIDTH)) / 2;
                
                for (int i = 0; i < handSize; i++) {
                    int cardX = startX + (i * cardSpacing);
                    int cardY = (i == selectedCardIndex) ? (HAND_Y - 30) : HAND_Y;
                    
                    if (x >= cardX && x <= cardX + HAND_CARD_WIDTH &&
                        y >= cardY && y <= cardY + HAND_CARD_HEIGHT) {
                        selectedCardIndex = i;
                        System.out.println("Selected card " + i + ": " + p1.hand.get(i).strName);
                        repaint();
                        return;
                    }
                }
            }
            
            // Check bottom 4 card slots (Player 1)
            if (y >= BOTTOM_SLOT_Y - SLOT_HEIGHT/2 && y <= BOTTOM_SLOT_Y + SLOT_HEIGHT/2) {
                int slotIndex = x / SLOT_WIDTH;
                if (slotIndex >= 0 && slotIndex < 4) {
                    System.out.println("Clicked on bottom slot " + slotIndex + " at (" + x + ", " + y + ")");
                    // game.placeCard(1, selectedCard, slotIndex); // Player 1
                }
            }
            
            // Check bottom squirrel slot
            if (x >= BOTTOM_SQUIRREL_X - 75 && x <= BOTTOM_SQUIRREL_X + 75 &&
                y >= BOTTOM_SQUIRREL_Y - 70 && y <= BOTTOM_SQUIRREL_Y + 70) {
                System.out.println("Clicked on bottom squirrel slot at (" + x + ", " + y + ")");
                // game.placeSquirrel(1); // Player 1
            }
            
            // Add more logic for card selection, etc.
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (game != null) {

        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (game != null) {
        
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {}

    @Override
    public void mouseExited(MouseEvent event) {}
}
