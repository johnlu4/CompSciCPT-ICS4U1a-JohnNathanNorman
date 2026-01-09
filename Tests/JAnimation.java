package Tests;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class JAnimation extends JPanel {
    // Properties

    // Methods
    public void PaintComponent(Graphics paint){
        super.paintComponent(paint);
        paint.setColor(new Color(95, 78, 51));

        paint.fillRect(0,0, 1280, 720);
    }

    

    // Constructor
    public JAnimation() {
        super();

        // Initializate properties

    }
}
