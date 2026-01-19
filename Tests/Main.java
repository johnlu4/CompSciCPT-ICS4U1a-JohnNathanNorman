package Tests;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.*; 
import javax.imageio.ImageIO;

public class Main implements ActionListener, KeyListener, FocusListener{
    // Properties
    int intPort = 0;
    int intHelpMenupage = 1;
    Timer Maintimer = new Timer(16, this); // Approximately 60 FPS

    String strP1Name = "Player 1";
    String strP2Name = "Player 2";
    Game game = null;

    // Main menu properties
    JFrame theMainFrame = new JFrame("Inscyption");
    JPanel MainMenuPanel = new JPanel();
    BufferedImage MainMenuImage;
    JButton HostButton = new JButton("Host Game");
    JButton JoinButton = new JButton("Join Game");
    JTextField nameField = new JTextField("Player Name");
    JTextField IPAddressField = new JTextField("Enter IP Address");
    JTextField PortField = new JTextField("Enter Port Number");
    JButton StartGameButton = new JButton("Start Game");
    JLabel StatusLabel = new JLabel("Status: Not connected");

    JButton returnMenuButton = new JButton("Return");

    // Help menu properties
    JFrame helpFrame = new JFrame("Help Menu");
    JPanel helpPanel = new JPanel();

    JButton helpButton = new JButton("Help");
    JButton LeftHelpButton = new JButton("<");
    JButton RightHelpButton = new JButton(">");

    BufferedImage helpImage;
    JLabel helpImageLabel;

    // About menu properties
    JPanel aboutPanel = new JPanel();
    JFrame aboutFrame = new JFrame("About Menu");

    BufferedImage aboutImage;
    JButton aboutButton = new JButton("About");
    
    // Round menu properties
    JTextArea theChatArea = new JTextArea();
    JTextField theChatText = new JTextField();

    
    // Animation panel
    JAnimation theAnimationPanel = new JAnimation();   
    SuperSocketMaster ssm = null;

    // Methods
    public void SwitchTabs(JPanel thePanel){

        theMainFrame.setContentPane(thePanel);
        theMainFrame.repaint();
        theMainFrame.pack();
    }

    public void SendSystemMessage(String message) {
        if (ssm != null && game != null && game.blnStarted) {
            theChatArea.append("[SYSTEM]: " + message + "\n");
            ssm.sendText("SYSTEM: " + message);
        }
    }

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

    public void StartGame(){
        PlayerClass p1 = new PlayerClass(strP1Name);
        PlayerClass p2 = new PlayerClass(strP2Name);
        game = new Game(p1, p2, theAnimationPanel);
        game.startGame();

        theMainFrame.setContentPane(theAnimationPanel);
        theMainFrame.repaint();
        theMainFrame.pack();
        Maintimer.start();

        System.out.println("Game Started!");
        System.out.println("P1: " + p1.strPlayerName);
        System.out.println("P2: " + p2.strPlayerName);

        SendSystemMessage("Game Started!");
        
        SendSystemMessage("~~ Drawing Phase ~~");
    }

    // ActionListener methods
    @Override
    public void actionPerformed(ActionEvent event) {
        // Action handling code
        if (event.getSource() == HostButton) {
            // Host game button clicked
            try{
                intPort = Integer.parseInt(PortField.getText());
            }catch(NumberFormatException e){
                System.out.println("Port number must be an integer.");
                return;
            }catch(Exception e){
                System.out.println("Invalid port number.");
                return;
            }
            ssm = new SuperSocketMaster(intPort, this);
            try{
                ssm.connect();
            }catch(Exception e){
                System.out.println("Failed to host game session.");
                return;
            }
            System.out.println("Hosting game on port " + PortField.getText());
            HostButton.setVisible(false);
            JoinButton.setVisible(false);
            IPAddressField.setVisible(false);
            PortField.setVisible(false);
            StatusLabel.setVisible(true);

            StatusLabel.setText("Status: Waiting for a player to join... Port: " + intPort);
        } else if (event.getSource() == JoinButton) {
            // Join game button clicked
            try{
                intPort = Integer.parseInt(PortField.getText());
            }catch(NumberFormatException e){
                System.out.println("Port number must be an integer.");
                return;
            }catch(Exception e){
                System.out.println("Invalid port number.");
                return;
            }
            ssm = new SuperSocketMaster(IPAddressField.getText(), intPort, this);
            boolean connected = ssm.connect();
            System.out.println("Attempting to join game at " + IPAddressField.getText() + ":" + PortField.getText());
            StatusLabel.setVisible(true);
            if(connected){
                System.out.println("Successfully joined game session.");
                JoinButton.setVisible(false);
                HostButton.setVisible(false);
                IPAddressField.setVisible(false);
                PortField.setVisible(false);
                nameField.setVisible(true);
                StatusLabel.setText("Status: Connected, waiting for host to start the game...");
                
                ssm.sendText("PLAYER_JOINED");
            }else {
                System.out.println("Failed to join game session.");
                StatusLabel.setText("Status: Failed to connect to host.");
                JoinButton.setVisible(true);
                HostButton.setVisible(true);
                IPAddressField.setVisible(true);
                PortField.setVisible(true);
            }
        } else if (event.getSource() == ssm) {
            // SuperSocketMaster event
            String strLine = ssm.readText();
            System.out.println("Received: " + strLine);


            // Main Menu events
            if(strLine.equals("START_GAME")){
                StartGame();
            }else if (strLine.equals("PLAYER_JOINED")){
                StatusLabel.setText("Status: Player joined. You can start the game...");
                StartGameButton.setVisible(true);
                nameField.setVisible(true);
            } else if (strLine.startsWith("PLAYER_NAME: ")) {
                strP2Name = strLine.substring(13);
                System.out.println("Player 2 Name: " + strP2Name);
            } else if (game != null && game.blnStarted && strLine.startsWith("CHAT: ")) {
                String chatMessage = strLine.substring(6);
                theChatArea.append(strP2Name + ": " + chatMessage + "\n");
            } else if (game != null && game.blnStarted && strLine.equals("SYSTEM: ")) {
                String systemMessage = strLine.substring(8);
                theChatArea.append("[SYSTEM]: " + systemMessage + "\n");
            }else if (game != null && game.blnStarted && strLine.equals("NEXT_PHASE")) {
                game.nextPhase();
            }

            // In round events
            if(strLine.equals("")) {
            }

        } else if (event.getSource() == StartGameButton) {
            // Start game button clicked
            System.out.println("Starting game...");
            StartGame();
            ssm.sendText("START_GAME");
        } else if (event.getSource() == Maintimer) {
            theAnimationPanel.repaint();
        } else if (event.getSource() == helpButton){
            SwitchTabs(helpPanel);
            helpPanel.add(returnMenuButton, BorderLayout.NORTH);
            returnMenuButton.setVisible(true);
        } else if (event.getSource() == aboutButton){
            SwitchTabs(aboutPanel);
            aboutPanel.add(returnMenuButton, BorderLayout.NORTH);
            returnMenuButton.setVisible(true);
        } else if (event.getSource() == returnMenuButton){
            SwitchTabs(MainMenuPanel);
        } else if (event.getSource() == nameField){
            // Name field action
            strP1Name = nameField.getText();
            nameField.setText("Player Name: " + strP1Name);
            ssm.sendText("PLAYER_NAME: " + strP1Name);
        } else if (event.getSource() == LeftHelpButton){
            intHelpMenupage -= 1;
            helpImage = getImage("HelpMenu" + intHelpMenupage + ".png");

            if (helpImage != null) {


                if (helpImageLabel != null) {
                    helpPanel.remove(helpImageLabel);
                }
                helpImageLabel = new JLabel(new ImageIcon(helpImage));
                helpPanel.add(helpImageLabel, BorderLayout.CENTER);
                helpPanel.revalidate();
                helpPanel.repaint();
            } else {
                intHelpMenupage += 1; 
                System.out.println("Warning: HelpMenu.png not found on classpath (Tests/Main.java) for page " + intHelpMenupage);
            }

        } else if (event.getSource() == RightHelpButton){
            intHelpMenupage += 1;
            helpImage = getImage("HelpMenu" + intHelpMenupage + ".png");

            if (helpImage != null) {
                if (helpImageLabel != null) {
                    helpPanel.remove(helpImageLabel);
                }
                helpImageLabel = new JLabel(new ImageIcon(helpImage));
                helpPanel.add(helpImageLabel, BorderLayout.CENTER);
                helpPanel.revalidate();
                helpPanel.repaint();
                
            } else {
                intHelpMenupage -= 1; 
                System.out.println("Warning: HelpMenu.png not found on classpath (Tests/Main.java) for page " + intHelpMenupage);
            }
        } else if (event.getSource() == theChatText) {
            String chatMessage = theChatText.getText();
            if (!chatMessage.trim().isEmpty() && game != null && game.blnStarted) {
                theChatArea.append(strP1Name + ": " + chatMessage + "\n");
                ssm.sendText("CHAT: " + chatMessage);
                theChatText.setText("");
            }
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
        if (event.getSource() == IPAddressField && IPAddressField.getText().equals("Enter IP Address")) {
            IPAddressField.setText("");
        } else if (event.getSource() == PortField && PortField.getText().equals("Enter Port Number")) {
            PortField.setText("");
        }
    }
    public void focusLost(FocusEvent event) {

    }



    public Main(){
        // Main menu setup
        theMainFrame.setPreferredSize(new Dimension(1280, 720));

        MainMenuImage = getImage("MainMenu.png");

        // Use a panel that paints the background image so components don't get overlapped
        MainMenuPanel = new BackgroundPanel(MainMenuImage);
        MainMenuPanel.setPreferredSize(new Dimension(1280, 720));
        MainMenuPanel.setLayout(null);

        if (MainMenuImage == null) {
            System.out.println("Warning: MainMenu.png not found on classpath (Tests/Main.java)");
        }

        HostButton.setBounds(540, 300, 200, 50);
        JoinButton.setBounds(540, 360, 200, 50);
        IPAddressField.setBounds(490, 540, 300, 40);
        PortField.setBounds(490, 580, 300, 40);
        nameField.setBounds(490, 350, 300, 40);
        StartGameButton.setBounds(540, 500, 200, 50);
        StatusLabel.setBounds(500, 600, 400, 30);
        helpButton.setBounds(10, 650, 100, 30);
        aboutButton.setBounds(120, 650, 100, 30);


        MainMenuPanel.add(nameField);
        MainMenuPanel.add(StartGameButton);
        MainMenuPanel.add(HostButton);
        MainMenuPanel.add(JoinButton);
        MainMenuPanel.add(IPAddressField);
        MainMenuPanel.add(PortField);
        MainMenuPanel.add(StatusLabel);
        MainMenuPanel.add(helpButton);
        MainMenuPanel.add(aboutButton);

        StatusLabel.setForeground(Color.WHITE);

        HostButton.addActionListener(this);
        JoinButton.addActionListener(this);
        IPAddressField.addActionListener(this);
        PortField.addActionListener(this);
        StartGameButton.addActionListener(this);
        nameField.addActionListener(this);
        helpButton.addActionListener(this);
        aboutButton.addActionListener(this);

        IPAddressField.addFocusListener(this);
        PortField.addFocusListener(this);
        nameField.addFocusListener(this);

        StartGameButton.setVisible(false);
        StatusLabel.setVisible(false);
        nameField.setVisible(false);

        // Help menu setup
        helpPanel.setPreferredSize(new Dimension(1280, 720));
        helpPanel.setLayout(new BorderLayout());


        helpPanel.add(LeftHelpButton);
        helpPanel.add(RightHelpButton);

        LeftHelpButton.setBounds(10, 650, 100, 30);
        RightHelpButton.setBounds(1150, 650, 100, 30);

        LeftHelpButton.addActionListener(this);
        RightHelpButton.addActionListener(this);

        helpImage = getImage("HelpMenu1.png");
        
        if (helpImage != null) {
            helpImageLabel = new JLabel(new ImageIcon(helpImage));
            helpPanel.add(helpImageLabel, BorderLayout.CENTER);
        } else {
            System.out.println("Warning: HelpMenu.png not found on classpath (Tests/Main.java)");
        }


        // About menu setup
        aboutPanel.setPreferredSize(new Dimension(1280, 720));
        aboutPanel.setLayout(new BorderLayout());
       
        aboutImage = getImage("AboutMenu.png");

        if (aboutImage != null) {
            aboutPanel.add(new JLabel(new ImageIcon(aboutImage)), BorderLayout.CENTER);
        } else {
            JLabel missing = new JLabel("About image not found", SwingConstants.CENTER);
            aboutPanel.add(missing, BorderLayout.CENTER);
            System.out.println("Warning: AboutMenu.png not found on classpath (Tests/Main.java)");
        }

        returnMenuButton.setBounds(10, 10, 150, 30);
        returnMenuButton.addActionListener(this);

        returnMenuButton.setVisible(false);

        // JAnimation Panel setup
        theAnimationPanel.setLayout(null);

        theChatArea.setEditable(false);
        theChatArea.setLineWrap(true);

        theChatArea.setBackground(new Color(65, 48, 21));
        theChatText.setBackground(new Color(65, 48, 21));
        theChatArea.setForeground(Color.WHITE);
        theChatText.setForeground(Color.WHITE);

        theChatArea.setBounds(0, 0, 250, 630);
        theChatText.setBounds(0, 650, 250, 30);

        theAnimationPanel.add(theChatArea);
        theAnimationPanel.add(theChatText);
        
        theChatText.addActionListener(this);



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
    
    // small helper panel that paints a background image behind child components
    static class BackgroundPanel extends JPanel {
        private BufferedImage bg;
        BackgroundPanel(BufferedImage img) {
            this.bg = img;
            setOpaque(true);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
