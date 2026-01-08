package Tests;

import java.awt.image.BufferedImage;

public abstract class CardClass {
    // Properties
    public String strName;
    public BufferedImage imgCardImage; 

    // Methods

    // Constructor
    public CardClass(String strName, BufferedImage imgCardImage, int[] arrStats) {
        // Initialization code
        this.strName = strName;
        this.imgCardImage = imgCardImage;
    }    
}
