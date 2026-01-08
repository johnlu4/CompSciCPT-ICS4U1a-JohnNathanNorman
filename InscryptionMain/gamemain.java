package InscryptionMain;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;

public class gamemain{
    // Properties
    JFrame gameframe = new JFrame("Inscryption");
    gamepanel gamepanel = new gamepanel();

    // Methods


    // Constructor
    public gamemain(){
        gamepanel.setPreferredSize(new Dimension(1280, 720));
        gamepanel.setLayout(null);

        gameframe.setContentPane(gamepanel);
        gameframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameframe.pack();
        gameframe.setResizable(false);
        gameframe.setVisible(true);
    }

    // Main
    public static void main(String[] args){
        new gamemain();
    }
}