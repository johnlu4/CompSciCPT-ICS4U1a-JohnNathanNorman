package Tests;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
    private SuperSocketMaster ssm;

    private BufferedImage bg = getImage("Game.png");
    private BufferedImage bellImage = getImage("Bell.png");
    private BufferedImage RegularCardBackImage = getImage("RegularCardBack.png");
    private BufferedImage SquirrelCardBackImage = getImage("SquirrelCardBack.png");
    
    // Selected card tracking
    private int selectedCardIndex = -1; // -1 means no card selected

    // Slot position constants
    // Bottom 4 card slots (Player 1)
    private static final int BOTTOM_SLOT_Y = 465;
    private static final int BOTTOM_SLOT_0_X = 466;
    private static final int BOTTOM_SLOT_1_X = 610;
    private static final int BOTTOM_SLOT_2_X = 740;
    private static final int BOTTOM_SLOT_3_X = 880;

    // Squirrel slot and Deck slot (top)
    private static final int TOP_RIGHT_SQUIRREL_X = 1030;
    private static final int TOP_RIGHT_DECK_X = 1185;
    private static final int TOP_RIGHT_SLOT_Y = 30;
    
    // Squirrel slot and Deck slot (bottom)
    private static final int BOTTOM_RIGHT_SQUIRREL_X = 1030;
    private static final int BOTTOM_RIGHT_DECK_X = 1185;
    private static final int BOTTOM_RIGHT_SLOT_Y = 575;

    // Death Slot
    private int DEATH_SLOT_X = 1175;
    private int DEATH_SLOT_Y = 280;
    
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

    /**
     * Get the X coordinate for a given slot index
     * @param slotIndex The slot (0-3)
     * @return The X coordinate of the slot center
     */
    private int getSlotX(int slotIndex) {
        switch (slotIndex) {
            case 0: return BOTTOM_SLOT_0_X;
            case 1: return BOTTOM_SLOT_1_X;
            case 2: return BOTTOM_SLOT_2_X;
            case 3: return BOTTOM_SLOT_3_X;
            default: return BOTTOM_SLOT_0_X;
        }
    }

    /**
     * Draw a card image at the specified slot position (bottom slots - Player 1)
     * @param g Graphics context
     * @param strCardName Name of the card
     * @param slotIndex Slot index (0-3)
     */
    private void drawCardAtSlot(Graphics g, String strCardName, int slotIndex) {
        BufferedImage cardImage = getCardImage(strCardName);
        if (cardImage == null) return;
        
        int slotX = getSlotX(slotIndex);
        // Center the card at the slot position
        // Card size: 120x150 (same as debug rectangles)
        int cardX = slotX - 60; // Center horizontally
        int cardY = BOTTOM_SLOT_Y - 75; // Center vertically
        
        g.drawImage(cardImage, cardX, cardY, 120, 150, this);
        
        // Draw damage and health indicators
        if (game != null) {
            PlayerClass p1 = game.getP1();
            if (p1 != null && p1.placedSlots[slotIndex] != null) {
                CardClass card = p1.placedSlots[slotIndex];
                
                // Draw damage indicator (overlay)
                BufferedImage damageImg = getImage(cardsprites + "Damage_" + card.intAttack + ".png");
                if (damageImg != null) {
                    g.drawImage(damageImg, cardX, cardY, 120, 150, this);
                }
                
                // Draw health indicator (overlay)
                BufferedImage healthImg = getImage(cardsprites + "Health_" + card.intHealth + ".png");
                if (healthImg != null) {
                    g.drawImage(healthImg, cardX, cardY, 120, 150, this);
                }
            }
        }
    }

    /**
     * Draw a card at top slot (Player 2) - shows card back during DrawingPhase
     * @param g Graphics context
     * @param card The card to draw
     * @param slotIndex Slot index (0-3)
     * @param showFaceUp Whether to show the card face-up or as a card back
     */
    private void drawCardAtTopSlot(Graphics g, CardClass card, int slotIndex, boolean showFaceUp) {
        int slotX = getSlotX(slotIndex);
        int cardX = slotX - 60;
        int cardY = 150; // Top slots Y position (higher than before)
        
        BufferedImage imageToShow;
        if (showFaceUp) {
            imageToShow = getCardImage(card.strName);
        } else {
            // Show card back based on card cost (0 = squirrel)
            imageToShow = (card.intCost == 0) ? SquirrelCardBackImage : RegularCardBackImage;
        }
        
        if (imageToShow != null) {
            g.drawImage(imageToShow, cardX, cardY, 120, 150, this);
        }
        
        // Draw damage and health indicators (only if face up)
        if (showFaceUp && card != null) {
            // Draw damage indicator (overlay)
            BufferedImage damageImg = getImage(cardsprites + "Damage_" + card.intAttack + ".png");
            if (damageImg != null) {
                g.drawImage(damageImg, cardX, cardY, 120, 150, this);
            }
            
            // Draw health indicator (overlay)
            BufferedImage healthImg = getImage(cardsprites + "Health_" + card.intHealth + ".png");
            if (healthImg != null) {
                g.drawImage(healthImg, cardX, cardY, 120, 150, this);
            }
        }
    }

    @Override
    public void paintComponent(Graphics paint){
        super.paintComponent(paint);

        paint.setColor(new Color(75, 58, 31));

        paint.fillRect(0,0, 1280, 720);
        paint.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        
        if (bellImage != null) {
            paint.drawImage(bellImage, 0, 0, getWidth(), getHeight(), this);
        }

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
                        drawCardAtSlot(paint, p1.placedSlots[i].strName, i);
                    }
                }
                // Draw P1's hand at bottom
                drawHand(paint, p1);
            }
            if (p2 != null) {
                boolean showFaceUp = !game.getCurrentPhase().equals("DrawingPhase");
                for (int i = 0; i < 4; i++) {
                    if (p2.placedSlots[i] != null) {
                        drawCardAtTopSlot(paint, p2.placedSlots[i], i, showFaceUp);
                    }
                }
            }
        }

        // Draw card back images for deck and squirrel slots
        drawCardBackImages(paint);

        // Draw scale indicator
        drawScaleIndicator(paint);

        // DEBUG: Draw clickable area rectangles
        // drawDebugRectangles(paint);

    }

    /**
     * Draw card back images for deck and squirrel slots
     * @param g Graphics context
     */
    private void drawCardBackImages(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw bottom deck slot (Regular card back)
        if (RegularCardBackImage != null) {
            int cardX = BOTTOM_RIGHT_DECK_X - 60;
            int cardY = BOTTOM_RIGHT_SLOT_Y - 75;
            g.drawImage(RegularCardBackImage, cardX, cardY, 120, 150, this);
        }
        
        // Draw bottom squirrel slot (Squirrel card back)
        if (SquirrelCardBackImage != null) {
            int cardX = BOTTOM_RIGHT_SQUIRREL_X - 60;
            int cardY = BOTTOM_RIGHT_SLOT_Y - 75;
            g.drawImage(SquirrelCardBackImage, cardX, cardY, 120, 150, this);
        }
        
        // Draw top deck slot (Regular card back, flipped 180 degrees)
        if (RegularCardBackImage != null) {
            AffineTransform oldTransform = g2d.getTransform();
            // Translate to top-left corner of where we want the card
            // Then translate to center, rotate 180, and offset back
            int cardX = TOP_RIGHT_DECK_X - 60;
            int cardY = TOP_RIGHT_SLOT_Y;
            g2d.translate(cardX + 60, cardY + 75);  // Move to center of card position
            g2d.rotate(Math.PI);  // Rotate 180 degrees
            g2d.drawImage(RegularCardBackImage, -60, -75, 120, 150, this);  // Draw centered
            g2d.setTransform(oldTransform);
        }
        
        // Draw top squirrel slot (Squirrel card back, flipped 180 degrees)
        if (SquirrelCardBackImage != null) {
            AffineTransform oldTransform = g2d.getTransform();
            // Translate to top-left corner of where we want the card
            // Then translate to center, rotate 180, and offset back
            int cardX = TOP_RIGHT_SQUIRREL_X - 60;
            int cardY = TOP_RIGHT_SLOT_Y;
            g2d.translate(cardX + 60, cardY + 75);  // Move to center of card position
            g2d.rotate(Math.PI);  // Rotate 180 degrees
            g2d.drawImage(SquirrelCardBackImage, -60, -75, 120, 150, this);  // Draw centered
            g2d.setTransform(oldTransform);
        }
    }

    /**
     * Draw the scale indicator showing balance between players
     * @param g Graphics context
     */
    private void drawScaleIndicator(Graphics g) {
        if (game == null) return;
        
        PlayerClass p1 = game.getP1();
        PlayerClass p2 = game.getP2();
        if (p1 == null || p2 == null) return;
        
        // Scale bar position (center right of screen)
        int scaleX = 330;
        int scaleTopY = 70;
        int scaleBottomY = 630;
        int scaleCenterY = (scaleTopY + scaleBottomY) / 2;
        int scaleHeight = scaleBottomY - scaleTopY;
        
        // Calculate scale difference (positive = P1 winning, negative = P2 winning)
        int scaleDiff = p1.intScale - p2.intScale;
        
        // Calculate dot position (scale range is typically -10 to +10, map to screen space)
        // Clamp to reasonable range for display
        int maxScaleDiff = 10;
        scaleDiff = Math.max(-maxScaleDiff, Math.min(maxScaleDiff, scaleDiff));
        
        // Map scale difference to pixel position
        // Positive scaleDiff = dot moves up (P1 winning)
        // Negative scaleDiff = dot moves down (P2 winning)
        int dotY = scaleCenterY - (scaleDiff * (scaleHeight / 2) / maxScaleDiff);
        
        // Draw the scale dot
        g.setColor(Color.BLACK);
        g.fillOval(scaleX - 8, dotY - 8, 16, 16);
    }

    /**
     * Draw debug rectangles for all clickable areas
     * @param g Graphics context
     */
    private void drawDebugRectangles(Graphics g) {
        // Set semi-transparent color for debug rectangles
        g.setColor(new Color(255, 0, 0, 100)); // Red with alpha
        
        // Draw bottom 4 card slots (Player 1)
        g.drawRect(BOTTOM_SLOT_0_X - 60, BOTTOM_SLOT_Y - 75, 120, 150);
        g.drawRect(BOTTOM_SLOT_1_X - 60, BOTTOM_SLOT_Y - 75, 120, 150);
        g.drawRect(BOTTOM_SLOT_2_X - 60, BOTTOM_SLOT_Y - 75, 120, 150);
        g.drawRect(BOTTOM_SLOT_3_X - 60, BOTTOM_SLOT_Y - 75, 120, 150);
        
        // Label the slots
        g.setColor(Color.YELLOW);
        g.drawString("Slot 0", BOTTOM_SLOT_0_X - 20, BOTTOM_SLOT_Y);
        g.drawString("Slot 1", BOTTOM_SLOT_1_X - 20, BOTTOM_SLOT_Y);
        g.drawString("Slot 2", BOTTOM_SLOT_2_X - 20, BOTTOM_SLOT_Y);
        g.drawString("Slot 3", BOTTOM_SLOT_3_X - 20, BOTTOM_SLOT_Y);
        
        // Draw bell area
        g.setColor(new Color(0, 255, 0, 100)); // Green with alpha
        g.drawRect(BELL_X, BELL_Y, BELL_WIDTH, BELL_HEIGHT);
        g.setColor(Color.YELLOW);
        g.drawString("Bell", BELL_X + 30, BELL_Y + 60);
        
        // Draw bottom squirrel slot
        g.setColor(new Color(0, 0, 255, 100)); // Blue with alpha
        g.drawRect(BOTTOM_RIGHT_SQUIRREL_X - 60, BOTTOM_RIGHT_SLOT_Y - 75, 120, 150);
        g.setColor(Color.YELLOW);
        g.drawString("Squirrel", BOTTOM_RIGHT_SQUIRREL_X - 25, BOTTOM_RIGHT_SLOT_Y);
        
        // Draw bottom deck slot
        g.setColor(new Color(255, 255, 0, 100)); // Yellow with alpha
        g.drawRect(BOTTOM_RIGHT_DECK_X - 60, BOTTOM_RIGHT_SLOT_Y - 75, 120, 150);
        g.setColor(Color.WHITE);
        g.drawString("Deck", BOTTOM_RIGHT_DECK_X - 15, BOTTOM_RIGHT_SLOT_Y);
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
                if(i == selectedCardIndex){
                    g.setColor(Color.GREEN);
                }else{
                    g.setColor(Color.ORANGE);
                }
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

    public void setSSM(SuperSocketMaster ssm) {
        this.ssm = ssm;
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
            
            // Check bottom 4 card slots (Player 1) - place card if one is selected
            if (selectedCardIndex >= 0 && p1 != null && selectedCardIndex < p1.hand.size()) {
                CardClass selectedCard = p1.hand.get(selectedCardIndex);
                
                // Slot 0
                if (x >= BOTTOM_SLOT_0_X - 60 && x <= BOTTOM_SLOT_0_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    System.out.println("Clicked on bottom slot 0 - Attempting to place card");
                    if (p1.placeCard(0, selectedCard)) {
                        // Send network message
                        if (ssm != null) {
                            ssm.sendText("PLACE_CARD:0:" + selectedCard.strName);
                        }
                        selectedCardIndex = -1; // Deselect after placing
                        repaint();
                    }
                    return;
                }
                // Slot 1
                if (x >= BOTTOM_SLOT_1_X - 60 && x <= BOTTOM_SLOT_1_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    System.out.println("Clicked on bottom slot 1 - Attempting to place card");
                    if (p1.placeCard(1, selectedCard)) {
                        // Send network message
                        if (ssm != null) {
                            ssm.sendText("PLACE_CARD:1:" + selectedCard.strName);
                        }
                        selectedCardIndex = -1;
                        repaint();
                    }
                    return;
                }
                // Slot 2
                if (x >= BOTTOM_SLOT_2_X - 60 && x <= BOTTOM_SLOT_2_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    System.out.println("Clicked on bottom slot 2 - Attempting to place card");
                    if (p1.placeCard(2, selectedCard)) {
                        // Send network message
                        if (ssm != null) {
                            ssm.sendText("PLACE_CARD:2:" + selectedCard.strName);
                        }
                        selectedCardIndex = -1;
                        repaint();
                    }
                    return;
                }
                // Slot 3
                if (x >= BOTTOM_SLOT_3_X - 60 && x <= BOTTOM_SLOT_3_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    System.out.println("Clicked on bottom slot 3 - Attempting to place card");
                    if (p1.placeCard(3, selectedCard)) {
                        // Send network message
                        if (ssm != null) {
                            ssm.sendText("PLACE_CARD:3:" + selectedCard.strName);
                        }
                        selectedCardIndex = -1;
                        repaint();
                    }
                    return;
                }
            }
            
            // Check bottom squirrel slot
            if (x >= BOTTOM_RIGHT_SQUIRREL_X - 60 && x <= BOTTOM_RIGHT_SQUIRREL_X + 60 &&
                y >= BOTTOM_RIGHT_SLOT_Y - 75 && y <= BOTTOM_RIGHT_SLOT_Y + 75) {
                System.out.println("Clicked on bottom squirrel slot at (" + x + ", " + y + ")");
                // game.placeSquirrel(1); // Player 1
                return;
            }
            
            // Check bottom deck slot
            if (x >= BOTTOM_RIGHT_DECK_X - 60 && x <= BOTTOM_RIGHT_DECK_X + 60 &&
                y >= BOTTOM_RIGHT_SLOT_Y - 75 && y <= BOTTOM_RIGHT_SLOT_Y + 75) {
                System.out.println("Clicked on bottom deck slot at (" + x + ", " + y + ")");
                // Draw card functionality
                return;
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
