package Tests;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class JAnimation extends JPanel {
    // Properties

    // Methods
    public void PaintComponent(Graphics paint){
        super.paintComponent(paint);
        paint.setColor(new Color(95, 78, 51));

        paint.fillRect(0,0, 1280, 720);
        System.out.println("PRINT BRUH PLEASE");
    }

    

    // Constructor
    public JAnimation() {
        super();

        // Initializate properties

    }
}
