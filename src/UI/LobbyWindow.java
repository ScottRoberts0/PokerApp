package UI;

import Networking.Networker;
import UI.Listeners.MainWindowListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class LobbyWindow extends JFrame implements ActionListener {

    private static final String ACTION_HOST = "host";
    private static final String ACTION_JOIN = "join";
    private static final String ACTION_START = "start";

    private JList playerListBox;
    private JTextField nameInputField, ipInputField;
    private JButton hostButton, joinButton, startGameButton;

    private String playerNameDefault;

    private static LobbyWindow instance;

    public LobbyWindow(String playerNameDefault){
        instance = this;

        this.playerNameDefault = playerNameDefault;

        this.setBounds(600, 200, 300, 525);
        this.setTitle("Poker Lobby");
        this.setLayout(new GridBagLayout());
        this.addWindowListener(new MainWindowListener());
        this.setResizable(false);

        createControls();

        this.setVisible(true);
    }

    public static LobbyWindow getInstance(){
        return instance;
    }

    private void createControls(){
        GridBagConstraints c = new GridBagConstraints();

        // set up the title
        JPanel titlePanel = new JPanel();
        //titlePanel.setBorder(new LineBorder(Color.BLACK, 1));

        JLabel titleLabel = new JLabel("POKER!!!!  (I hardly know her)");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(titleLabel, BorderLayout.CENTER);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.ipady = 25;
        c.ipadx = 25;
        c.anchor = GridBagConstraints.WEST;
        this.add(titlePanel, c);

        // set up the header area (player name, host/join buttons)
        JPanel playerNamePanel = new JPanel();

        JLabel nameInputLabel = new JLabel("Player Name:");

        nameInputField = new JTextField(10);
        nameInputField.setText(this.playerNameDefault);

        playerNamePanel.add(nameInputLabel);
        playerNamePanel.add(nameInputField);

        c.fill = GridBagConstraints.NONE;
        c.gridy = 1;
        this.add(playerNamePanel, c);

        // Set up the host/join/ip section
        JPanel serverButtonsPanel = new JPanel();

        hostButton = new JButton("Host Game");
        hostButton.setActionCommand(ACTION_HOST);
        hostButton.addActionListener(this);

        joinButton = new JButton("Join Game");
        joinButton.setActionCommand(ACTION_JOIN);
        joinButton.addActionListener(this);

        serverButtonsPanel.add(hostButton);
        serverButtonsPanel.add(joinButton);

        c.ipady = 0;
        c.gridy = 2;
        this.add(serverButtonsPanel, c);

        JPanel ipPanel = new JPanel();

        JLabel ipInputLabel = new JLabel("IP:");

        ipInputField = new JTextField(16);

        ipPanel.add(ipInputLabel);
        ipPanel.add(ipInputField);

        c.gridy = 3;
        c.ipady = 25;
        this.add(ipPanel, c);

        JPanel playerListPanel = new JPanel(new GridBagLayout());

        JLabel playerListLabel = new JLabel("Players Connected:");

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.anchor = GridBagConstraints.NORTHWEST;
        playerListPanel.add(playerListLabel, c1);

        playerListBox = new JList();
        playerListBox.setBorder(new BevelBorder(BevelBorder.LOWERED));
        playerListBox.setPreferredSize(new Dimension(this.getWidth() / 2 + 50, 200));
        playerListBox.setModel(new DefaultListModel());
        c1.gridy = 1;
        c1.anchor = GridBagConstraints.NORTHWEST;
        playerListPanel.add(playerListBox, c1);

        c.gridy = 4;
        c.ipady = 25;
        this.add(playerListPanel, c);

        JPanel startPanel = new JPanel();

        startGameButton = new JButton("Start Game");
        startGameButton.setActionCommand(ACTION_START);
        startGameButton.setEnabled(false);
        startGameButton.addActionListener(this);

        startPanel.add(startGameButton);

        c.gridy = 5;
        c.weighty = 1;
        c.ipady = 0;
        c.fill = GridBagConstraints.VERTICAL;
        this.add(startPanel, c);
    }

    private void joinButtonPressed(){
        // confirm IP is valid
        // create a new networker
        new Networker("127.0.0.1", nameInputField.getText());

        if(Networker.getInstance().getClientConnected()) {
            joinButton.setEnabled(false);
            hostButton.setEnabled(false);
            ipInputField.setEnabled(false);
            startGameButton.setEnabled(false);
        }

        // wait until players have joined
    }

    private void hostButtonPressed(){
        InetAddress address;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            address = socket.getInetAddress();
            System.out.println(address);
        }catch (Exception e){
            System.out.println("Connect to google.com to get IP failed.");
            address = null;
        }

        if(address != null){
            Networker net = new Networker();

            ipInputField.setText(address + "");
            ipInputField.setEnabled(false);

            // add yourself as a player
            net.addPlayerToLobby(nameInputField.getText());

            hostButton.setEnabled(false);
            joinButton.setEnabled(false);
            startGameButton.setEnabled(true);
        }
    }

    private void startGameButtonPressed(){
        if(Networker.getInstance().getPlayersInLobby().size() > 2){

        }else{
            JOptionPane.showMessageDialog(this, "Not enough players");
        }
    }

    public void updatePlayerList(ArrayList<String> playerList){
        DefaultListModel players = new DefaultListModel();

        for(int i = 0; i < playerList.size(); i++){
            players.addElement(playerList.get(i));
        }

        playerListBox.setModel(players);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ACTION_JOIN)){
            joinButtonPressed();
        }else if(e.getActionCommand().equals(ACTION_HOST)){
            hostButtonPressed();
        }else if(e.getActionCommand().equals(ACTION_START)){
            startGameButtonPressed();
        }
    }
}
