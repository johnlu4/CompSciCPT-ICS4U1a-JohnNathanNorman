package Tests;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.*; 
import javax.imageio.ImageIO;

public class Main implements ActionListener, FocusListener{
    // Properties
    int intPort = 0;
    int intHelpMenupage = 1;
    Timer Maintimer = new Timer(16, this); // Approximately 60 FPS

    String strP1Name = "Player 1";
    String strP2Name = "Player 2";
    Game game = null;
    boolean blnIsHost = false; // Track if this client is the host

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
    
    // Game end properties
    JPanel gameEndPanel = new JPanel();
    BufferedImage gameEndWinImage;
    BufferedImage gameEndLoseImage;
    
    // Round menu properties
    JTextArea theChatArea = new JTextArea();
    JTextField theChatText = new JTextField();
    JScrollPane theScroll = new JScrollPane(theChatArea);
    
    // Animation panel
    JAnimation theAnimationPanel = new JAnimation();   
    SuperSocketMaster ssm = null;

    // Utility Methods
    // small helper panel that paints a background image behind child components
    static class BackgroundPanel extends JPanel{
        private BufferedImage bg;
        BackgroundPanel(BufferedImage img){
            this.bg = img;
            setOpaque(true);
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if (bg != null){
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // load images from resources
    public BufferedImage getImage(String strImagePath){
        BufferedImage Image = null;
        String resourcePath = strImagePath.startsWith("/") ? strImagePath : "/Tests/" + strImagePath;
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null){
            System.out.println("Resource not found: " + resourcePath);
            return null;
        }
        try{
            Image = ImageIO.read(is);
        } catch (Exception e){
            System.out.println("Failed to read image: " + resourcePath + " -> " + e.getMessage());
            Image = null;
        }
        return Image;
    }
    
    // Methods

    // method to switch between different panels
    public void SwitchTabs(JPanel thePanel){

        theMainFrame.setContentPane(thePanel);
        theMainFrame.repaint();
        theMainFrame.pack();
    }

    // method to send system messages to chat area and remote client
    public void SendSystemMessage(String message) {
        if (ssm != null && game != null && game.blnStarted) {
            theChatArea.append("[SYSTEM]: " + message + "\n");
            ssm.sendText("SYSTEM: " + message);
        }
    }
    
    // show game end screen (win or lose)
    public void showGameEnd(boolean didWin) {
        Maintimer.stop();
        
        // Clear and set up game end panel with appropriate image
        gameEndPanel.removeAll();
        gameEndPanel.setLayout(new BorderLayout());
        gameEndPanel.setPreferredSize(new Dimension(1280, 720));
        
        BufferedImage endImage = didWin ? gameEndWinImage : gameEndLoseImage;
        if (endImage != null) {
            JLabel imageLabel = new JLabel(new ImageIcon(endImage));
            gameEndPanel.add(imageLabel, BorderLayout.CENTER);
        } else {
            String message = didWin ? "Victory!" : "Defeat...";
            JLabel textLabel = new JLabel(message, SwingConstants.CENTER);
            textLabel.setFont(new Font("Arial", Font.BOLD, 48));
            gameEndPanel.add(textLabel, BorderLayout.CENTER);
            System.out.println("Warning: Game end image not found");
        }
        
        SwitchTabs(gameEndPanel);
        System.out.println(didWin ? "Game Over - You Win!" : "Game Over - You Lose!");
    }


    // start game method
    public void StartGame(){
        PlayerClass p1 = new PlayerClass(strP1Name);
        PlayerClass p2 = new PlayerClass(strP2Name);
        game = new Game(p1, p2, theAnimationPanel, ssm, this, blnIsHost);
        theAnimationPanel.setSSM(ssm);
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
    public void actionPerformed(ActionEvent event){
        // Action handling code
        if (event.getSource() == HostButton){
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
            blnIsHost = true; // Mark this client as host

            StatusLabel.setText("Status: Waiting for a player to join... Port: " + intPort);
        } else if (event.getSource() == JoinButton){
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
            }else{
                System.out.println("Failed to join game session.");
                StatusLabel.setText("Status: Failed to connect to host.");
                JoinButton.setVisible(true);
                HostButton.setVisible(true);
                IPAddressField.setVisible(true);
                PortField.setVisible(true);
            }
        } else if (event.getSource() == ssm){
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
            } else if (strLine.startsWith("PLAYER_NAME: ")){
                strP2Name = strLine.substring(13);
                System.out.println("Player 2 Name: " + strP2Name);
            } 

            // In-game events
            if (game != null && game.blnStarted && strLine.startsWith("CHAT: ")){
                String chatMessage = strLine.substring(6);
                theChatArea.append(strP2Name + ": " + chatMessage + "\n");
            } else if (game != null && game.blnStarted && strLine.equals("SYSTEM: ")){
                String systemMessage = strLine.substring(8);
                theChatArea.append("[SYSTEM]: " + systemMessage + "\n");
            } else if (game != null && game.blnStarted && strLine.equals("NEXT_PHASE")){
                game.nextPhase();
            } else if (game != null && game.blnStarted && strLine.equals("RETURN_TO_DRAWING")){
                // Other client completed attack phase, sync back to drawing phase
                System.out.println("Received RETURN_TO_DRAWING from remote");
                game.syncReturnToDrawing();
                theAnimationPanel.repaint();
            } else if (game != null && game.blnStarted && strLine.startsWith("ATTACK_ANIM:")){
                // Handle attack animation from host
                // Format: ATTACK_ANIM:slotIndex:isBottomAttacking
                String[] parts = strLine.split(":");
                if (parts.length == 3){
                    try{
                        int intSlotIndex = Integer.parseInt(parts[1]);
                        boolean isBottomAttacking = Boolean.parseBoolean(parts[2]);
                        
                        // Get the attacking card
                        CardClass attackingCard = null;
                        if (isBottomAttacking){
                            attackingCard = game.getP2().placedSlots[intSlotIndex]; // Client sees host as P2
                        } else{
                            attackingCard = game.getP1().placedSlots[intSlotIndex]; // Client sees self as P1
                        }
                        
                        if (attackingCard != null){
                            theAnimationPanel.startAttackAnimation(attackingCard, intSlotIndex, !isBottomAttacking); // Flip perspective
                            System.out.println("Received attack animation: slot " + intSlotIndex + ", isBottomAttacking=" + !isBottomAttacking);
                        }
                    } catch (NumberFormatException e){
                        System.out.println("Error parsing ATTACK_ANIM: " + e.getMessage());
                    }
                }
            } else if (game != null && game.blnStarted && strLine.startsWith("DAMAGE_RESULT:")){
                // Handle damage result from host
                // Format: DAMAGE_RESULT:slotIndex:isBottomAttacking:newHP_or_destroyed:defenderBlood
                String[] parts = strLine.split(":");
                if (parts.length == 5){
                    try{
                        int intSlotIndex = Integer.parseInt(parts[1]);
                        boolean isBottomAttacking = Boolean.parseBoolean(parts[2]);
                        String hpResult = parts[3];
                        int intDefenderBlood = Integer.parseInt(parts[4]);
                        
                        // Get defender (opposite of attacker)
                        PlayerClass defender = isBottomAttacking ? game.getP1() : game.getP2(); // Flip perspective
                        
                        if (hpResult.equals("destroyed")){
                            // Card was destroyed
                            defender.placedSlots[intSlotIndex] = null;
                            defender.intBlood = intDefenderBlood;
                            System.out.println("Card at slot " + intSlotIndex + " destroyed. Defender blood: " + intDefenderBlood);
                        } else{
                            // Card took damage but survived
                            int intNewHP = Integer.parseInt(hpResult);
                            if (defender.placedSlots[intSlotIndex] != null){
                                defender.placedSlots[intSlotIndex].intHealth = intNewHP;
                                defender.intBlood = intDefenderBlood;
                                System.out.println("Card at slot " + intSlotIndex + " HP: " + intNewHP + ". Defender blood: " + intDefenderBlood);
                            }
                        }
                        theAnimationPanel.repaint();
                    } catch (NumberFormatException e){
                        System.out.println("Error parsing DAMAGE_RESULT: " + e.getMessage());
                    }
                }
            } else if (game != null && game.blnStarted && strLine.startsWith("SCALE_UPDATE:")){
                // Handle scale update from host
                // Format: SCALE_UPDATE:p1Scale:p2Scale
                String[] parts = strLine.split(":");
                if (parts.length == 3){
                    try{
                        int intP1Scale = Integer.parseInt(parts[1]);
                        int intP2Scale = Integer.parseInt(parts[2]);
                        
                        // Swap because client sees host as P2
                        game.getP1().intScale = intP2Scale;
                        game.getP2().intScale = intP1Scale;
                        
                        System.out.println("Scale updated - P1: " + game.getP1().intScale + " | P2: " + game.getP2().intScale);
                        theAnimationPanel.repaint();
                    } catch (NumberFormatException e){
                        System.out.println("Error parsing SCALE_UPDATE: " + e.getMessage());
                    }
                }
            } else if (game != null && game.blnStarted && strLine.startsWith("LIFE_UPDATE:")){
                // Handle life update from host
                // Format: LIFE_UPDATE:p1Lives:p2Lives
                String[] parts = strLine.split(":");
                if (parts.length == 3){
                    try{
                        int intP1Lives = Integer.parseInt(parts[1]);
                        int intP2Lives = Integer.parseInt(parts[2]);
                        
                        // Swap because client sees host as P2
                        game.getP1().intLives = intP2Lives;
                        game.getP2().intLives = intP1Lives;
                        
                        System.out.println("Lives updated - P1: " + game.getP1().intLives + " | P2: " + game.getP2().intLives);
                        theAnimationPanel.repaint();
                    } catch (NumberFormatException e){
                        System.out.println("Error parsing LIFE_UPDATE: " + e.getMessage());
                    }
                }
            } else if (game != null && game.blnStarted && strLine.equals("PLAYER_READY")){
                // Sync player 2's ready status when receiving message from remote player
                game.getP2().isReady = true;
                theChatArea.append("[SYSTEM]: " + strP2Name + " is ready!\n");
                System.out.println(strP2Name + " is ready!");
                
                // Check if both players are ready to advance phase
                if (game.getP1().isReady && game.getP2().isReady){
                    game.nextPhase();
                } else{
                    System.out.println("Waiting for other player...");
                }
                theAnimationPanel.repaint();
            } else if (game != null && game.blnStarted && strLine.startsWith("PLACE_CARD:")){
                // Handle card placement from remote player
                // Format: PLACE_CARD:slotIndex:cardName:cost:hp:attack:sigil:sacrificeSlots
                String[] parts = strLine.split(":", 8);
                if (parts.length >= 7){
                    try{
                        int intSlotIndex = Integer.parseInt(parts[1]);
                        String cardName = parts[2];
                        int intCost = Integer.parseInt(parts[3]);
                        int intHp = Integer.parseInt(parts[4]);
                        int intAttack = Integer.parseInt(parts[5]);
                        String sigil = parts[6];
                        String sacrificeSlots = parts.length == 8 ? parts[7] : "none";
                        
                        PlayerClass p2 = game.getP2();
                        
                        // Handle sacrifice slots first
                        if (!sacrificeSlots.equals("none") && !sacrificeSlots.isEmpty()){
                            String[] sacrificeIndices = sacrificeSlots.split(",");
                            for (String sacrificeSlotStr : sacrificeIndices){
                                try{
                                    int intSacrificeSlot = Integer.parseInt(sacrificeSlotStr.trim());
                                    if (p2.placedSlots[intSacrificeSlot] != null){
                                        System.out.println(strP2Name + " sacrificed " + p2.placedSlots[intSacrificeSlot].strName + " in slot " + intSacrificeSlot);
                                        p2.placedSlots[intSacrificeSlot] = null;
                                    }
                                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                                    System.out.println("Invalid sacrifice slot: " + sacrificeSlotStr);
                                }
                            }
                        }
                        
                        // Create card with actual stats from the message
                        CardClass cardToPlace = new CardClass(cardName, null, new int[]{intHp, intAttack, intCost}, sigil);
                        
                        // Place the card directly in the slot
                        boolean wasOccupied = (p2.placedSlots[intSlotIndex] != null);
                        if (wasOccupied){
                            System.out.println(strP2Name + " replaced card in slot " + intSlotIndex);
                        }
                        p2.placedSlots[intSlotIndex] = cardToPlace;
                        
                        // Reset blood to 0 after placement (mirroring local behavior)
                        p2.intBlood = 0;
                        
                        System.out.println(strP2Name + " placed " + cardName + " in slot " + intSlotIndex + " (HP:" + intHp + ", ATK:" + intAttack + ")");
                        theAnimationPanel.repaint();
                    } catch (NumberFormatException e){
                        System.out.println("Invalid PLACE_CARD message format");
                    }
                }
            } else if (game != null && game.blnStarted && strLine.startsWith("WINNER: ")) {
                String winnerName = strLine.substring(8);
                System.out.println("Game Over! Winner: " + winnerName);
                
                // Check if local player won
                boolean didLocalPlayerWin = winnerName.equals(strP1Name);
                showGameEnd(didLocalPlayerWin);
            }

        } else if (event.getSource() == StartGameButton){
            // Start game button clicked
            System.out.println("Starting game...");
            StartGame();
            ssm.sendText("START_GAME");
        } else if (event.getSource() == Maintimer){
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

            if (helpImage != null){


                if (helpImageLabel != null){
                    helpPanel.remove(helpImageLabel);
                }
                helpImageLabel = new JLabel(new ImageIcon(helpImage));
                helpPanel.add(helpImageLabel, BorderLayout.CENTER);
                helpPanel.revalidate();
                helpPanel.repaint();
            } else{
                intHelpMenupage += 1; 
                System.out.println("Warning: HelpMenu.png not found on classpath (Tests/Main.java) for page " + intHelpMenupage);
            }

        } else if (event.getSource() == RightHelpButton){
            intHelpMenupage += 1;
            helpImage = getImage("HelpMenu" + intHelpMenupage + ".png");

            if (helpImage != null){
                if (helpImageLabel != null){
                    helpPanel.remove(helpImageLabel);
                }
                helpImageLabel = new JLabel(new ImageIcon(helpImage));
                helpPanel.add(helpImageLabel, BorderLayout.CENTER);
                helpPanel.revalidate();
                helpPanel.repaint();
                
            } else{
                intHelpMenupage -= 1; 
                System.out.println("Warning: HelpMenu.png not found on classpath (Tests/Main.java) for page " + intHelpMenupage);
            }
        } else if (event.getSource() == theChatText){
            String chatMessage = theChatText.getText();
            if (!chatMessage.trim().isEmpty() && game != null && game.blnStarted){
                theChatArea.append(strP1Name + ": " + chatMessage + "\n");
                ssm.sendText("CHAT: " + chatMessage);
                theChatText.setText("");
            }
        }

    }

    // FocusListener methods
    public void focusGained(FocusEvent event){
        // Focus gained handling code
        if (event.getSource() == IPAddressField && IPAddressField.getText().equals("Enter IP Address")){
            IPAddressField.setText("");
        } else if (event.getSource() == PortField && PortField.getText().equals("Enter Port Number")){
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

        if (MainMenuImage == null){
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
        
        if (helpImage != null){
            helpImageLabel = new JLabel(new ImageIcon(helpImage));
            helpPanel.add(helpImageLabel, BorderLayout.CENTER);
        } else{
            System.out.println("Warning: HelpMenu.png not found on classpath (Tests/Main.java)");
        }


        // About menu setup
        aboutPanel.setPreferredSize(new Dimension(1280, 720));
        aboutPanel.setLayout(new BorderLayout());
       
        aboutImage = getImage("AboutMenu.png");

        if (aboutImage != null){
            aboutPanel.add(new JLabel(new ImageIcon(aboutImage)), BorderLayout.CENTER);
        } else{
            JLabel missing = new JLabel("About image not found", SwingConstants.CENTER);
            aboutPanel.add(missing, BorderLayout.CENTER);
            System.out.println("Warning: AboutMenu.png not found on classpath (Tests/Main.java)");
        }

        returnMenuButton.setBounds(10, 10, 150, 30);
        returnMenuButton.addActionListener(this);

        returnMenuButton.setVisible(false);
        
        // Game end panel setup
        gameEndPanel.setPreferredSize(new Dimension(1280, 720));
        gameEndPanel.setLayout(new BorderLayout());
        
        gameEndWinImage = getImage("gameendwin.png");
        gameEndLoseImage = getImage("gameendlose.png");
        
        if (gameEndWinImage == null) {
            System.out.println("Warning: gameendwin.png not found on classpath (Tests/Main.java)");
        }
        if (gameEndLoseImage == null) {
            System.out.println("Warning: gameendlose.png not found on classpath (Tests/Main.java)");
        }

        // the animation setup
        theAnimationPanel.setLayout(null);

        theChatArea.setEditable(false);
        theChatArea.setLineWrap(true);

        theChatArea.setBackground(new Color(65, 48, 21));
        theChatText.setBackground(new Color(65, 48, 21));
        theChatArea.setForeground(Color.WHITE);
        theChatText.setForeground(Color.WHITE);

        theScroll.setBounds(0, 0, 250, 630);
        theChatText.setBounds(0, 650, 250, 30);

        theAnimationPanel.add(theScroll);
        theAnimationPanel.add(theChatText);
        
        theChatText.addActionListener(this);



        theMainFrame.setContentPane(MainMenuPanel);
        theMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theMainFrame.pack();
        theMainFrame.setResizable(false);
        theMainFrame.setVisible(true);
    }

    // Constructor
    public static void main(String[] args){
        new Main();
    }
}
