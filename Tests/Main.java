package Tests;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Main implements ActionListener, KeyListener{
    // Properties
    JFrame theMainFrame = new JFrame("Inscyption");

    JAnimation theAnimationPanel = new JAnimation();   
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

    public Main(){
        theAnimationPanel.setPreferredSize(new Dimension(1280, 720));
        theAnimationPanel.setLayout(null);

        theMainFrame.setContentPane(theAnimationPanel);
        theMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theMainFrame.pack();
        theMainFrame.setResizable(false);
        theMainFrame.setVisible(true);
    }

    // Constructor
    public static void main(String[] args) {
    
    }
}
