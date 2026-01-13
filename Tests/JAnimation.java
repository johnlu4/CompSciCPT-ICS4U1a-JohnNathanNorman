package Tests;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Dimension;

public class JAnimation extends JPanel {
    // Properties
    private PlayerClass p1;
    private PlayerClass p2;

    // Methods
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
