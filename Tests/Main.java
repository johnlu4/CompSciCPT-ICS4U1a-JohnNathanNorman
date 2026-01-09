package Tests;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import javax.swing.Timer;

public class Main implements ActionListener, KeyListener, FocusListener{
    // Properties
    JFrame theMainFrame = new JFrame("Inscyption");

    int port = 0;

    Timer Maintimer = new Timer(16, this); // Approximately 60 FPS
    // Main menu properties
    JPanel MainMenuPanel = new JPanel();
    JButton HostButton = new JButton("Host Game");
    JButton JoinButton = new JButton("Join Game");
    JTextField IPAddressField = new JTextField("Enter IP Address");
    JTextField PortField = new JTextField("Enter Port Number");
    JButton StartGameButton = new JButton("Start Game");
    JLabel TitleLabel = new JLabel("Inscyption");
    JLabel StatusLabel = new JLabel("Status: Not connected");
    

    
    // Animation panel
    JAnimation theAnimationPanel = new JAnimation();   
    SuperSocketMaster ssm = null;

    // Methods
    public void StartGame(){
        theMainFrame.setContentPane(theAnimationPanel);
        theMainFrame.revalidate();
        theMainFrame.repaint();
        theMainFrame.pack();
        Maintimer.start();
    }

    // ActionListener methods
    public void actionPerformed(ActionEvent event) {
        // Action handling code
        if (event.getSource() == HostButton) {
            // Host game button clicked
            try{
                port = Integer.parseInt(PortField.getText());
            }catch(NumberFormatException e){
                System.err.println("Port number must be an integer.");
                return;
            }catch(Exception e){
                System.err.println("Invalid port number.");
                return;
            }
            ssm = new SuperSocketMaster(port, this);
            ssm.connect();
            System.err.println("Hosting game on port " + PortField.getText());
            HostButton.setVisible(false);
            JoinButton.setVisible(false);
            StatusLabel.setVisible(true);
            IPAddressField.setVisible(false);
            PortField.setVisible(false);
            StatusLabel.setText("Status: Waiting for a player to join... Port: " + port);
        } else if (event.getSource() == JoinButton) {
            // Join game button clicked
            try{
                port = Integer.parseInt(PortField.getText());
            }catch(NumberFormatException e){
                System.err.println("Port number must be an integer.");
                return;
            }catch(Exception e){
                System.err.println("Invalid port number.");
                return;
            }
            ssm = new SuperSocketMaster(IPAddressField.getText(), port, this);
            boolean connected = ssm.connect();
            System.err.println("Attempting to join game at " + IPAddressField.getText() + ":" + PortField.getText());
            if(connected){
                System.err.println("Successfully joined game session.");
                JoinButton.setVisible(false);
                HostButton.setVisible(false);
                StatusLabel.setVisible(true);
                IPAddressField.setVisible(false);
                PortField.setVisible(false);
                StatusLabel.setText("Status: Connected, waiting for host to start the game...");
                ssm.sendText("PLAYER_JOINED");
            }else {
                System.err.println("Failed to join game session.");

            }
        } else if (event.getSource() == IPAddressField) {
            // IP address field action
        } else if (event.getSource() == PortField) {
            // Port field action
        } else if (event.getSource() == ssm) {
            // SuperSocketMaster event
            String strLine = ssm.readText();
            System.err.println("Received: " + strLine);

            if(strLine.equals("START_GAME")){
                StartGame();
            }else if (strLine.equals("PLAYER_JOINED")){
                StatusLabel.setText("Status: Player joined. You can start the game...");
                StartGameButton.setVisible(true);
            }


        } else if (event.getSource() == StartGameButton) {
            // Start game button clicked
            System.err.println("Starting game...");
            StartGame();
            ssm.sendText("START_GAME");
        }
    }

    // KeyListener methods

    public void keyTyped(KeyEvent event) {
        // Key typed handling code
    }

    public void keyPressed(KeyEvent event) {
        // Key pressed handling code
    }

    public void keyReleased(KeyEvent event) {
        // Key released handling code
    }

    // FocusListener methods
    public void focusGained(FocusEvent event) {
        // Focus gained handling code
        if (event.getSource() == IPAddressField) {
            IPAddressField.setText("");
        } else if (event.getSource() == PortField) {
            PortField.setText("");
        }
    }
    public void focusLost(FocusEvent event) {
        // Focus lost handling code
    }



    public Main(){
        MainMenuPanel.setPreferredSize(new Dimension(1280, 720));
        MainMenuPanel.setLayout(null);

        theAnimationPanel.setPreferredSize(new Dimension(1280, 720));
        theAnimationPanel.setLayout(null);

        HostButton.setBounds(540, 200, 200, 50);
        JoinButton.setBounds(540, 300, 200, 50);
        IPAddressField.setBounds(490, 400, 300, 40);
        PortField.setBounds(490, 460, 300, 40);
        StartGameButton.setBounds(540, 500, 200, 50);
        TitleLabel.setBounds(580, 100, 200, 50);
        StatusLabel.setBounds(10, 680, 300, 20);

        MainMenuPanel.add(TitleLabel);
        MainMenuPanel.add(StartGameButton);
        MainMenuPanel.add(HostButton);
        MainMenuPanel.add(JoinButton);
        MainMenuPanel.add(IPAddressField);
        MainMenuPanel.add(PortField);
        MainMenuPanel.add(StatusLabel);

        HostButton.addActionListener(this);
        JoinButton.addActionListener(this);
        IPAddressField.addActionListener(this);
        PortField.addActionListener(this);
        StartGameButton.addActionListener(this);

        IPAddressField.addFocusListener(this);
        PortField.addFocusListener(this);

        StartGameButton.setVisible(false);
        StatusLabel.setVisible(false);


        theMainFrame.setContentPane(MainMenuPanel);
        theMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theMainFrame.pack();
        theMainFrame.setResizable(false);
        theMainFrame.setVisible(true);
    }

    // Constructor
    public static void main(String[] args) {
        new Main();
    }
}
