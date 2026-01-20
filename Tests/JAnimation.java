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
import java.util.ArrayList;


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
    private int intSelectedCardIndex = -1; // -1 means no card selected
    
    // Sacrifice selection tracking
    private ArrayList<Integer> selectedSacrificeSlots = new ArrayList<>(); // Slots marked for sacrifice

    // Animation state
    private boolean blnIsAnimating = false;
    private CardClass animatingCard = null;
    private int intAnimStartX, intAnimStartY;
    private int intAnimEndX, intAnimEndY;
    private int intAnimatingToSlot = -1;
    private float animProgress = 0.0f;
    private float ANIM_SPEED = 0.08f; // Animation speed (0-1 per frame)
    
    // Attack animation state
    private boolean blnIsAttackAnimating = false;
    private CardClass attackingCard = null;
    private int intAttackStartX, intAttackStartY;
    private int intAttackTargetX, intAttackTargetY;
    private int intAttackFromSlot = -1;
    private boolean blnAttackFromBottom = true; // true = P1 (bottom), false = P2 (top)
    private float attackProgress = 0.0f;
    private float ATTACK_ANIM_SPEED = 0.15f; // Faster for attack animation
    private boolean blnAttackReturning = false; // true when returning to original position
    
    // Draw animation state
    private boolean blnIsDrawAnimating = false;
    private CardClass drawingCard = null;
    private int intDrawStartX, intDrawStartY;
    private int intDrawEndX, intDrawEndY;
    private float drawProgress = 0.0f;
    private float DRAW_ANIM_SPEED = 0.12f; // Animation speed for drawing cards

    // Slot position constants
    // Bottom 4 card slots (Player 1)
    private int BOTTOM_SLOT_Y = 465;
    private int BOTTOM_SLOT_0_X = 466;
    private int BOTTOM_SLOT_1_X = 610;
    private int BOTTOM_SLOT_2_X = 740;
    private int BOTTOM_SLOT_3_X = 880;

    // Squirrel slot and Deck slot (top)
    private int TOP_RIGHT_SQUIRREL_X = 1030;
    private int TOP_RIGHT_DECK_X = 1185;
    private int TOP_RIGHT_SLOT_Y = 30;
    
    // Squirrel slot and Deck slot (bottom)
    private int BOTTOM_RIGHT_SQUIRREL_X = 1030;
    private int BOTTOM_RIGHT_DECK_X = 1185;
    private int BOTTOM_RIGHT_SLOT_Y = 575;

    // Death Slot
    private int intDeathSlotX = 1175;
    private int intDeathSlotY = 280;
    
    // Slot dimensions
    private int SLOT_WIDTH = 320;
    private int SLOT_HEIGHT = 140;
    private int CARD_WIDTH = 200;
    private int CARD_HEIGHT = 280;
    
    // Bell position constants
    private int BELL_X = 991;
    private int BELL_Y = 298;
    private int BELL_WIDTH = 110;
    private int BELL_HEIGHT = 110;
    
    // Hand positioning constants
    private int HAND_Y = 575;
    private int HAND_CARD_WIDTH = 120;
    private int HAND_CARD_HEIGHT = 168;
    private int HAND_START_X = 425;
    private int HAND_MAX_WIDTH = 500;

    // Utility Methods
    public BufferedImage getImage(String strImagePath){
        BufferedImage Image = null;
        InputStream is = null;
        String resourcePath = null;
        
        // Try multiple locations
        String[] pathsToTry = new String[3];
        if (strImagePath.startsWith("/")) {
            pathsToTry[0] = strImagePath;
        } else {
            pathsToTry[0] = "/" + strImagePath;
        }
        pathsToTry[1] = "/Tests/" + strImagePath;
        pathsToTry[2] = "/cardsprites/" + strImagePath;
        
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

    // Get the X coordinate for a given slot index
    private int getSlotX(int slotIndex) {
        switch (slotIndex) {
            case 0: return BOTTOM_SLOT_0_X;
            case 1: return BOTTOM_SLOT_1_X;
            case 2: return BOTTOM_SLOT_2_X;
            case 3: return BOTTOM_SLOT_3_X;
            default: return BOTTOM_SLOT_0_X;
        }
    }

    // Draw a card image at the specified slot position (bottom slots - Player 1)
    private void drawCardAtSlot(Graphics g, String strCardName, int slotIndex) {
        // Skip drawing if this slot is being animated to
        if (blnIsAnimating && slotIndex == intAnimatingToSlot) {
            return;
        }
        
        // Skip drawing if this card is attacking from this slot
        if (blnIsAttackAnimating && slotIndex == intAttackFromSlot && blnAttackFromBottom) {
            return;
        }
        
        BufferedImage cardImage = getCardImage(strCardName);
        if (cardImage == null) return;
        
        int slotX = getSlotX(slotIndex);
        // Center the card at the slot position
        // Card size: 120x150 (same as debug rectangles)
        int cardX = slotX - 60; // Center horizontally
        int cardY = BOTTOM_SLOT_Y - 75; // Center vertically
        
        // Draw red highlight if this card is selected for sacrifice
        if (selectedSacrificeSlots.contains(slotIndex)) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(255, 0, 0, 100)); // Semi-transparent red
            g2d.fillRect(cardX, cardY, 120, 150);
            g2d.setColor(Color.RED);
            g2d.drawRect(cardX, cardY, 120, 150);
            g2d.drawRect(cardX + 1, cardY + 1, 118, 148); // Thicker border
        }
        
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

    // Draw a card at top slot (Player 2) - shows card back during DrawingPhase
    private void drawCardAtTopSlot(Graphics g, CardClass card, int slotIndex, boolean blnShowFaceUp) {
        // Skip drawing if this card is attacking from this slot
        if (blnIsAttackAnimating && slotIndex == intAttackFromSlot && !blnAttackFromBottom) {
            return;
        }
        
        int slotX = getSlotX(slotIndex);
        int cardX = slotX - 60;
        int cardY = 150; // Top slots Y position (higher than before)
        
        // Show card if it's been revealed before OR if we're supposed to show it face up
        boolean shouldShowFaceUp = card.blnRevealed || blnShowFaceUp;
        
        // Mark card as revealed if we're showing it face up
        if (blnShowFaceUp) {
            card.blnRevealed = true;
        }
        
        BufferedImage imageToShow;
        if (shouldShowFaceUp) {
            imageToShow = getCardImage(card.strName);
        } else {
            // Show card back based on card cost (0 = squirrel)
            if (card.intCost == 0) {
                imageToShow = SquirrelCardBackImage;
            } else {
                imageToShow = RegularCardBackImage;
            }
        }
        
        if (imageToShow != null) {
            g.drawImage(imageToShow, cardX, cardY, 120, 150, this);
        }
        
        // Draw damage and health indicators (only if face up or revealed)
        if (shouldShowFaceUp && card != null) {
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

        // Update animation if active
        if (blnIsAnimating) {
            animProgress += ANIM_SPEED;
            if (animProgress >= 1.0f) {
                animProgress = 1.0f;
                blnIsAnimating = false;
                animatingCard = null;
                intAnimatingToSlot = -1;
            }
        }
        
        // Update attack animation if active
        if (blnIsAttackAnimating) {
            attackProgress += ATTACK_ANIM_SPEED;
            if (attackProgress >= 1.0f) {
                attackProgress = 1.0f;
                if (!blnAttackReturning) {
                    // Reached target, now return
                    blnAttackReturning = true;
                    attackProgress = 0.0f;
                } else {
                    // Animation complete
                    blnIsAttackAnimating = false;
                    attackingCard = null;
                    intAttackFromSlot = -1;
                    blnAttackReturning = false;
                    // Notify game that animation is complete
                    if (game != null) {
                        game.onAttackAnimationComplete();
                    }
                }
            }
        }
        
        // Update draw animation if active
        if (blnIsDrawAnimating) {
            drawProgress += DRAW_ANIM_SPEED;
            if (drawProgress >= 1.0f) {
                drawProgress = 1.0f;
                blnIsDrawAnimating = false;
                drawingCard = null;
            }
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
                // Always show cards face up during AttackPhase, hide during DrawingPhase
                boolean showFaceUp = game.getCurrentPhase().equals("AttackPhase");
                
                // Reveal all placed cards during AttackPhase (including 0 attack cards)
                if (showFaceUp) {
                    for (int i = 0; i < 4; i++) {
                        if (p2.placedSlots[i] != null) {
                            p2.placedSlots[i].blnRevealed = true;
                        }
                    }
                }
                
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

        // Draw animating card on top of everything
        if (blnIsAnimating && animatingCard != null) {
            drawAnimatingCard(paint);
        }
        
        // Draw attacking card on top of everything
        if (blnIsAttackAnimating && attackingCard != null) {
            drawAttackingCard(paint);
        }
        
        // Draw card being drawn on top of everything
        if (blnIsDrawAnimating && drawingCard != null) {
            drawDrawingCard(paint);
        }

        // DEBUG: Draw clickable area rectangles
        // drawDebugRectangles(paint);

    }

    // Draw card back images for deck and squirrel slots
    private void drawCardBackImages(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw bottom deck slot - always show card back
        if (!blnIsDrawAnimating && RegularCardBackImage != null) {
            int cardX = BOTTOM_RIGHT_DECK_X - 60;
            int cardY = BOTTOM_RIGHT_SLOT_Y - 75;
            g.drawImage(RegularCardBackImage, cardX, cardY, 120, 150, this);
        }
        
        // Draw bottom squirrel slot - always show card back
        if (!blnIsDrawAnimating && SquirrelCardBackImage != null) {
            int cardX = BOTTOM_RIGHT_SQUIRREL_X - 60;
            int cardY = BOTTOM_RIGHT_SLOT_Y - 75;
            g.drawImage(SquirrelCardBackImage, cardX, cardY, 120, 150, this);
        }
        
        // Draw top deck slot (Regular card back, flipped 180 degrees)
        if (RegularCardBackImage != null) {
            AffineTransform oldTransform = g2d.getTransform();
            int cardX = TOP_RIGHT_DECK_X - 60;
            int cardY = TOP_RIGHT_SLOT_Y;
            g2d.translate(cardX + 60, cardY + 75);
            g2d.rotate(Math.PI);
            g2d.drawImage(RegularCardBackImage, -60, -75, 120, 150, this);
            g2d.setTransform(oldTransform);
        }
        
        // Draw top squirrel slot (Squirrel card back, flipped 180 degrees)
        if (SquirrelCardBackImage != null) {
            AffineTransform oldTransform = g2d.getTransform();
            int cardX = TOP_RIGHT_SQUIRREL_X - 60;
            int cardY = TOP_RIGHT_SLOT_Y;
            g2d.translate(cardX + 60, cardY + 75);
            g2d.rotate(Math.PI);
            g2d.drawImage(SquirrelCardBackImage, -60, -75, 120, 150, this);
            g2d.setTransform(oldTransform);
        }
    }

    // Start animating a card from hand position to slot position
    private void startCardAnimation(CardClass card, int handIndex, int slotIndex) {
        if (card == null || game == null) return;
        
        PlayerClass p1 = game.getP1();
        if (p1 == null) return;
        
        // Store the card and target slot
        animatingCard = card;
        intAnimatingToSlot = slotIndex;
        
        // Calculate start position (hand position)
        int handSize = p1.hand.size(); // Current hand size (before removal)
        int cardSpacing = handSize == 1 ? 0 : Math.min(HAND_CARD_WIDTH, HAND_MAX_WIDTH / handSize);
        int startX = HAND_START_X + (HAND_MAX_WIDTH - (cardSpacing * (handSize - 1) + HAND_CARD_WIDTH)) / 2;
        
        intAnimStartX = startX + (handIndex * cardSpacing);
        intAnimStartY = HAND_Y;
        if (handIndex == intSelectedCardIndex) {
            intAnimStartY -= 30; // Account for elevation
        }
        
        // Calculate end position (slot position)
        intAnimEndX = getSlotX(slotIndex) - 60;
        intAnimEndY = BOTTOM_SLOT_Y - 75;
        
        // Start animation
        animatingCard = card;
        animProgress = 0.0f;
        blnIsAnimating = true;
    }

    // Start an attack animation from a card's slot to opponent's slot
    public void startAttackAnimation(CardClass card, int slotIndex, boolean blnFromBottom) {
        if (card == null) return;
        
        attackingCard = card;
        intAttackFromSlot = slotIndex;
        blnAttackFromBottom = blnFromBottom;
        blnAttackReturning = false;
        attackProgress = 0.0f;
        
        int slotX = getSlotX(slotIndex);
        
        if (blnFromBottom) {
            // Bottom card attacking upward
            intAttackStartX = slotX - 60;
            intAttackStartY = BOTTOM_SLOT_Y - 75;
            intAttackTargetX = slotX - 60;
            intAttackTargetY = 150; // Top slot position
        } else {
            // Top card attacking downward
            intAttackStartX = slotX - 60;
            intAttackStartY = 150;
            intAttackTargetX = slotX - 60;
            intAttackTargetY = BOTTOM_SLOT_Y - 75; // Bottom slot position
        }
        
        blnIsAttackAnimating = true;
    }
    
    // Start drawing animation from deck/squirrel slot to hand
    public void startDrawAnimation(CardClass card, boolean blnFromDeck) {
        if (card == null) return;
        
        drawingCard = card;
        drawProgress = 0.0f;
        
        // Start position (deck or squirrel slot)
        if (blnFromDeck) {
            intDrawStartX = BOTTOM_RIGHT_DECK_X - 60;
            intDrawStartY = BOTTOM_RIGHT_SLOT_Y - 75;
        } else {
            intDrawStartX = BOTTOM_RIGHT_SQUIRREL_X - 60;
            intDrawStartY = BOTTOM_RIGHT_SLOT_Y - 75;
        }
        
        // End position (hand - rightmost position)
        if (game != null) {
            PlayerClass p1 = game.getP1();
            if (p1 != null) {
                int handSize = p1.hand.size();
                int cardSpacing;
                if (handSize == 1) {
                    cardSpacing = 0;
                } else {
                    cardSpacing = Math.min(HAND_CARD_WIDTH, HAND_MAX_WIDTH / handSize);
                }
                int startX = HAND_START_X + (HAND_MAX_WIDTH - (cardSpacing * (handSize - 1) + HAND_CARD_WIDTH)) / 2;
                
                // Target the rightmost position (where the new card will be)
                intDrawEndX = startX + ((handSize - 1) * cardSpacing);
                intDrawEndY = HAND_Y;
            }
        }
        
        blnIsDrawAnimating = true;
    }
    
    // Draw the card currently being drawn to hand
    private void drawDrawingCard(Graphics g) {
        if (drawingCard == null) return;
        
        // Interpolate position using ease-out curve
        float t = 1.0f - (1.0f - drawProgress) * (1.0f - drawProgress);
        int currentX = (int)(intDrawStartX + (intDrawEndX - intDrawStartX) * t);
        int currentY = (int)(intDrawStartY + (intDrawEndY - intDrawStartY) * t);
        
        // Draw the card
        BufferedImage cardImage = getCardImage(drawingCard.strName);
        if (cardImage != null) {
            g.drawImage(cardImage, currentX, currentY, 120, 150, this);
            
            // Draw blue border for drawing card
            g.setColor(Color.CYAN);
            g.drawRect(currentX, currentY, 120, 150);
        }
    }
    
    // Draw the card currently performing an attack
    private void drawAttackingCard(Graphics g) {
        if (attackingCard == null) return;
        
        int currentX, currentY;
        
        if (!blnAttackReturning) {
            // Moving toward target
            float t = 1.0f - (1.0f - attackProgress) * (1.0f - attackProgress);
            currentX = (int)(intAttackStartX + (intAttackTargetX - intAttackStartX) * t);
            currentY = (int)(intAttackStartY + (intAttackTargetY - intAttackStartY) * t);
        } else {
            // Returning to original position
            float t = 1.0f - (1.0f - attackProgress) * (1.0f - attackProgress);
            currentX = (int)(intAttackTargetX + (intAttackStartX - intAttackTargetX) * t);
            currentY = (int)(intAttackTargetY + (intAttackStartY - intAttackTargetY) * t);
        }
        
        // Draw the card
        BufferedImage cardImage = getCardImage(attackingCard.strName);
        if (cardImage != null) {
            g.drawImage(cardImage, currentX, currentY, 120, 150, this);
            
            // Draw red border for attacking card
            g.setColor(Color.RED);
            g.drawRect(currentX, currentY, 120, 150);
            
            // Draw current stats on attacking card
            BufferedImage damageImg = getImage(cardsprites + "Damage_" + attackingCard.intAttack + ".png");
            if (damageImg != null) {
                g.drawImage(damageImg, currentX, currentY, 120, 150, this);
            }
            
            BufferedImage healthImg = getImage(cardsprites + "Health_" + attackingCard.intHealth + ".png");
            if (healthImg != null) {
                g.drawImage(healthImg, currentX, currentY, 120, 150, this);
            }
        }
    }

    // Draw the card currently being animated
    private void drawAnimatingCard(Graphics g) {
        if (animatingCard == null) return;
        
        // Interpolate position using ease-out curve
        float t = 1.0f - (1.0f - animProgress) * (1.0f - animProgress);
        int currentX = (int)(intAnimStartX + (intAnimEndX - intAnimStartX) * t);
        int currentY = (int)(intAnimStartY + (intAnimEndY - intAnimStartY) * t);
        
        // Draw the card
        BufferedImage cardImage = getCardImage(animatingCard.strName);
        if (cardImage != null) {
            g.drawImage(cardImage, currentX, currentY, HAND_CARD_WIDTH, HAND_CARD_HEIGHT, this);
            
            // Draw green border for animated card
            g.setColor(Color.GREEN);
            g.drawRect(currentX, currentY, HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        }
    }

    // Draw the scale indicator showing balance between players
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

    // Draw the player's hand with dynamic card spacing
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
            
            // Skip drawing if this card is currently being animated
            if (blnIsAnimating && card == animatingCard) {
                continue;
            }
            
            BufferedImage cardImage = getCardImage(card.strName);
            
            int cardX = startX + (i * cardSpacing);
            int cardY = HAND_Y;
            
            // Elevate selected card
            if (i == intSelectedCardIndex) {
                cardY -= 30; // Move selected card up by 30 pixels
            }
            
            if (cardImage != null) {
                g.drawImage(cardImage, cardX, cardY, HAND_CARD_WIDTH, HAND_CARD_HEIGHT, this);
                
                // Draw card border for visibility
                if(i == intSelectedCardIndex){
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
                int cardSpacing;
                if (handSize == 1) {
                    cardSpacing = 0;
                } else {
                    cardSpacing = Math.min(HAND_CARD_WIDTH, HAND_MAX_WIDTH / handSize);
                }
                int startX = HAND_START_X + (HAND_MAX_WIDTH - (cardSpacing * (handSize - 1) + HAND_CARD_WIDTH)) / 2;
                
                for (int i = 0; i < handSize; i++) {
                    int cardX = startX + (i * cardSpacing);
                    int cardY;
                    if (i == intSelectedCardIndex) {
                        cardY = HAND_Y - 30;
                    } else {
                        cardY = HAND_Y;
                    }
                    
                    if (x >= cardX && x <= cardX + HAND_CARD_WIDTH &&
                        y >= cardY && y <= cardY + HAND_CARD_HEIGHT) {
                        // Clear sacrifice selection when selecting a different hand card
                        if (intSelectedCardIndex != i) {
                            selectedSacrificeSlots.clear();
                        }
                        intSelectedCardIndex = i;
                        System.out.println("Selected card " + i + ": " + p1.hand.get(i).strName);
                        repaint();
                        return;
                    }
                }
            }
            
            // Check if clicking on placed cards for sacrifice selection
            // This allows players to select cards to sacrifice when they have a card requiring 2+ blood selected
            if (intSelectedCardIndex >= 0 && p1 != null && intSelectedCardIndex < p1.hand.size()) {
                CardClass selectedCard = p1.hand.get(intSelectedCardIndex);
                
                // Only allow sacrifice selection if card costs 2+ blood
                if (selectedCard.intCost >= 2) {
                    int clickedSlot = -1;
                    
                    // Check which slot was clicked
                    if (x >= BOTTOM_SLOT_0_X - 60 && x <= BOTTOM_SLOT_0_X + 60 &&
                        y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                        clickedSlot = 0;
                    } else if (x >= BOTTOM_SLOT_1_X - 60 && x <= BOTTOM_SLOT_1_X + 60 &&
                        y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                        clickedSlot = 1;
                    } else if (x >= BOTTOM_SLOT_2_X - 60 && x <= BOTTOM_SLOT_2_X + 60 &&
                        y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                        clickedSlot = 2;
                    } else if (x >= BOTTOM_SLOT_3_X - 60 && x <= BOTTOM_SLOT_3_X + 60 &&
                        y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                        clickedSlot = 3;
                    }
                    
                    // If a placed card was clicked, toggle sacrifice selection
                    if (clickedSlot >= 0 && p1.placedSlots[clickedSlot] != null) {
                        CardClass cardInSlot = p1.placedSlots[clickedSlot];
                        
                        // Don't allow sacrificing dead cards
                        if (cardInSlot.intHealth > 0) {
                            if (selectedSacrificeSlots.contains(clickedSlot)) {
                                // Deselect this sacrifice
                                selectedSacrificeSlots.remove(Integer.valueOf(clickedSlot));
                                System.out.println("Deselected slot " + clickedSlot + " for sacrifice");
                                repaint();
                                return;
                            } else {
                                // Add to sacrifice selection
                                selectedSacrificeSlots.add(clickedSlot);
                                int totalBlood = p1.intBlood + selectedSacrificeSlots.size();
                                System.out.println("Selected slot " + clickedSlot + " for sacrifice. Total blood: " + totalBlood + "/" + selectedCard.intCost);
                                
                                // If we now have enough blood, automatically place the card on this slot
                                if (totalBlood >= selectedCard.intCost) {
                                    System.out.println("Sufficient blood reached! Placing card on slot " + clickedSlot);
                                    
                                    // Start animation BEFORE placing card (while it's still in hand)
                                    startCardAnimation(selectedCard, intSelectedCardIndex, clickedSlot);
                                    
                                    // Place the card with sacrifices
                                    if (p1.placeCard(clickedSlot, selectedCard, selectedSacrificeSlots)) {
                                        // Send network message with full card stats and sacrifice slots
                                        if (ssm != null) {
                                            String sigilName = selectedCard.getSigil() != null ? selectedCard.getSigil().getName() : "N/A";
                                            // Convert sacrifice slots to comma-separated string
                                            String sacrificeList = "";
                                            for (int i = 0; i < selectedSacrificeSlots.size(); i++) {
                                                sacrificeList += selectedSacrificeSlots.get(i);
                                                if (i < selectedSacrificeSlots.size() - 1) sacrificeList += ",";
                                            }
                                            if (sacrificeList.isEmpty()) sacrificeList = "none";
                                            ssm.sendText("PLACE_CARD:" + clickedSlot + ":" + selectedCard.strName + ":" + 
                                                        selectedCard.intCost + ":" + selectedCard.intHealth + ":" + 
                                                        selectedCard.intAttack + ":" + sigilName + ":" + sacrificeList);
                                        }
                                        intSelectedCardIndex = -1; // Deselect after placing
                                        selectedSacrificeSlots.clear(); // Clear sacrifice selection after placing
                                    } else {
                                        // If placement failed, cancel animation
                                        blnIsAnimating = false;
                                        animatingCard = null;
                                        intAnimatingToSlot = -1;
                                    }
                                }
                                repaint();
                                return;
                            }
                        }
                    }
                }
            }
            
            // Check bottom 4 card slots (Player 1) - place card if one is selected
            if (intSelectedCardIndex >= 0 && p1 != null && intSelectedCardIndex < p1.hand.size()) {
                CardClass selectedCard = p1.hand.get(intSelectedCardIndex);
                
                // Helper to check if placement would be valid and start animation if so
                int targetSlot = -1;
                
                // Check which slot was clicked
                if (x >= BOTTOM_SLOT_0_X - 60 && x <= BOTTOM_SLOT_0_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    targetSlot = 0;
                } else if (x >= BOTTOM_SLOT_1_X - 60 && x <= BOTTOM_SLOT_1_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    targetSlot = 1;
                } else if (x >= BOTTOM_SLOT_2_X - 60 && x <= BOTTOM_SLOT_2_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    targetSlot = 2;
                } else if (x >= BOTTOM_SLOT_3_X - 60 && x <= BOTTOM_SLOT_3_X + 60 &&
                    y >= BOTTOM_SLOT_Y - 75 && y <= BOTTOM_SLOT_Y + 75) {
                    targetSlot = 3;
                }
                
                // If a slot was clicked, try to place the card
                if (targetSlot >= 0) {
                    System.out.println("Clicked on bottom slot " + targetSlot + " - Attempting to place card");
                    
                    // Start animation BEFORE placing card (while it's still in hand)
                    startCardAnimation(selectedCard, intSelectedCardIndex, targetSlot);
                    
                    boolean placementSuccess = false;
                    
                    // Use new placeCard with sacrifices if we have any selected
                    if (!selectedSacrificeSlots.isEmpty() && selectedCard.intCost >= 2) {
                        placementSuccess = p1.placeCard(targetSlot, selectedCard, selectedSacrificeSlots);
                    } else {
                        // Use old placeCard for single-slot or no sacrifice
                        placementSuccess = p1.placeCard(targetSlot, selectedCard);
                    }
                    
                    // Now place the card (this removes it from hand)
                    if (placementSuccess) {
                        // Send network message with full card stats and sacrifice slots
                        // Format: PLACE_CARD:slotIndex:cardName:cost:hp:attack:sigil:sacrificeSlots
                        if (ssm != null) {
                            String sigilName = selectedCard.getSigil() != null ? selectedCard.getSigil().getName() : "N/A";
                            // Convert sacrifice slots to comma-separated string
                            String sacrificeList = "";
                            for (int i = 0; i < selectedSacrificeSlots.size(); i++) {
                                sacrificeList += selectedSacrificeSlots.get(i);
                                if (i < selectedSacrificeSlots.size() - 1) sacrificeList += ",";
                            }
                            if (sacrificeList.isEmpty()) sacrificeList = "none";
                            ssm.sendText("PLACE_CARD:" + targetSlot + ":" + selectedCard.strName + ":" + 
                                        selectedCard.intCost + ":" + selectedCard.intHealth + ":" + 
                                        selectedCard.intAttack + ":" + sigilName + ":" + sacrificeList);
                        }
                        intSelectedCardIndex = -1; // Deselect after placing
                        selectedSacrificeSlots.clear(); // Clear sacrifice selection after placing
                        repaint();
                    } else {
                        // If placement failed, cancel animation
                        blnIsAnimating = false;
                        animatingCard = null;
                        intAnimatingToSlot = -1;
                    }
                    return;
                }
            }
            
            // Check bottom squirrel slot
            if (x >= BOTTOM_RIGHT_SQUIRREL_X - 60 && x <= BOTTOM_RIGHT_SQUIRREL_X + 60 &&
                y >= BOTTOM_RIGHT_SLOT_Y - 75 && y <= BOTTOM_RIGHT_SLOT_Y + 75) {
                System.out.println("Clicked on bottom squirrel slot at (" + x + ", " + y + ")");
                
                // Draw a squirrel card for Player 1
                if (game.getCurrentPhase().equals("DrawingPhase")) {
                    // Can't draw during initialization phase
                    if (game.isInitializationPhase) {
                        System.out.println("Cannot draw cards during the first Drawing Phase!");
                        return;
                    }
                    // Use game method to properly track drawing
                    p1 = game.getP1();
                    if (p1 != null && !p1.hasDrawnThisTurn) {
                        // Get the card we'll draw for animation
                        int squirrelIdx = p1.getSquirrelIndex();
                        if (squirrelIdx < p1.strSquirrelDeck.length && p1.strSquirrelDeck[squirrelIdx][0] != null) {
                            // Create the card for animation
                            String name = p1.strSquirrelDeck[squirrelIdx][0];
                            int cost = Integer.parseInt(p1.strSquirrelDeck[squirrelIdx][1]);
                            int hp = Integer.parseInt(p1.strSquirrelDeck[squirrelIdx][2]);
                            int attack = Integer.parseInt(p1.strSquirrelDeck[squirrelIdx][3]);
                            String sigil = p1.strSquirrelDeck[squirrelIdx][4];
                            CardClass cardToDraw = new CardClass(name, null, new int[]{hp, attack, cost}, sigil);
                            
                            // Now draw through game method (which sets hasDrawnThisTurn)
                            if (game.playerDrawSquirrel(1)) {
                                startDrawAnimation(cardToDraw, false); // false = squirrel slot
                                repaint();
                            }
                        }
                    }
                } else {
                    System.out.println("Can only draw during Drawing Phase");
                }
                return;
            }
            
            // Check bottom deck slot
            if (x >= BOTTOM_RIGHT_DECK_X - 60 && x <= BOTTOM_RIGHT_DECK_X + 60 &&
                y >= BOTTOM_RIGHT_SLOT_Y - 75 && y <= BOTTOM_RIGHT_SLOT_Y + 75) {
                System.out.println("Clicked on bottom deck slot at (" + x + ", " + y + ")");
                
                // Draw a card from deck for Player 1
                if (game.getCurrentPhase().equals("DrawingPhase")) {
                    // Can't draw during initialization phase
                    if (game.isInitializationPhase) {
                        System.out.println("Cannot draw cards during the first Drawing Phase!");
                        return;
                    }
                    p1 = game.getP1();
                    if (p1 != null && !p1.hasDrawnThisTurn) {
                        // Get the card we'll draw for animation
                        int deckIdx = p1.getDeckIndex();
                        if (deckIdx < p1.strDeck.length && p1.strDeck[deckIdx][0] != null) {
                            // Create the card for animation
                            String name = p1.strDeck[deckIdx][0];
                            int cost = Integer.parseInt(p1.strDeck[deckIdx][1]);
                            int hp = Integer.parseInt(p1.strDeck[deckIdx][2]);
                            int attack = Integer.parseInt(p1.strDeck[deckIdx][3]);
                            String sigil = p1.strDeck[deckIdx][4];
                            BufferedImage cardImage = getCardImage(name);
                            CardClass cardToDraw = new CardClass(name, cardImage, new int[]{hp, attack, cost}, sigil);
                            
                            // Now draw through game method (which sets hasDrawnThisTurn)
                            if (game.playerDrawCard(1)) {
                                startDrawAnimation(cardToDraw, true); // true = deck slot
                                repaint();
                            }
                        }
                    }
                } else {
                    System.out.println("Can only draw during Drawing Phase");
                }
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
