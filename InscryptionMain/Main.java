package InscryptionMain;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;

public class Main implements ActionListener, KeyListener {
    // Properties
    JFrame gameframe = new JFrame("Inscryption");
    JAnimation gamepanel = new JAnimation();
    SuperSocketMaster ssm = null;

    // Methods
    public void actionPerformed(ActionEvent event) {
        // Action handling code
    }

    public void keyTyped(KeyEvent event) {
        // Key typed handling code
    }

    public void keyPressed(KeyEvent event) {
        // Key pressed handling code
    }

    public void keyReleased(KeyEvent event) {
        // Key released handling code
    }

    // Constructor
    public Main(){
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
        new Main();
    }
}