package Tests;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.Color;
import java.awt.Dimension;

public class JAnimation extends JPanel {
    // Properties
    private PlayerClass p1;
    private PlayerClass p2;

    public int intScale ;

    public String strState ;


    // Methods

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
    
    public void initRound() {
        // Initialize round properties
        strState = "DrawingPhase";
        intScale = 0;
    }

    @Override
    public void paintComponent(Graphics paint){
        super.paintComponent(paint);
        paint.setColor(new Color(95, 78, 51));

        paint.fillRect(0,0, 1280, 720);

        // Draw players' names
        paint.setColor(Color.WHITE);
        if (p1 != null) {
            paint.drawString(p1.strPlayerName, 50, 50);
        }

        if (p2 != null) {
            paint.drawString(p2.strPlayerName, 1080, 50);
        }

    }

    public void setPlayers(PlayerClass p1, PlayerClass p2) {
        this.p1 = p1;
        this.p2 = p2;
        repaint();
    }

    // Constructor
    public JAnimation() {
        super();
        setPreferredSize(new Dimension(1280, 720));
        // Initialize properties

    }
}
