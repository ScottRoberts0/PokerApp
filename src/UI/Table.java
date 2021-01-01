package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Logic.Card;
import Logic.Player;
import UI.Components.TableComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Table implements ActionListener {

    // constants
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 650;

    // components
    private JFrame mainFrame;
    private JPanel mainPanel;
    private TableComponent table;
    private JButton checkButton, callButton, foldButton, raiseButton;
    private Player[] players;

    // misc vars
    private int numPlayers;

    public Table(Player[] players) {
        this.players = players;
        numPlayers = players.length;

        createWindow();

        drawTable();

        drawButtons();

        // show zee vindow
        mainFrame.setVisible(true);//making the frame visible
    }

    private void drawTable(){
        Point p;

        // create the table
        table = new TableComponent(players);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 5;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;

        // add components to the table to the window
        mainPanel.add(table, c);
    }

    private void drawButtons(){
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        foldButton = new JButton("Fold");
        foldButton.setActionCommand("Fold");
        foldButton.addActionListener(this);

        checkButton = new JButton("Check");
        checkButton.setActionCommand("Check");
        checkButton.addActionListener(this);

        raiseButton = new JButton("Raise");
        raiseButton.setActionCommand("Raise");
        raiseButton.addActionListener(this);

        callButton = new JButton("Call");
        callButton.setActionCommand("Call");
        callButton.addActionListener(this);


        buttonPanel.add(foldButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(raiseButton);
        buttonPanel.add(callButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;

        mainPanel.add(buttonPanel, c);
    }

    private void createWindow(){
        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        mainFrame.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        mainFrame.add(mainPanel);
    }

    public void setTableCards(Card[] cards){
        table.setTableCards(cards);
    }

    public void updatePlayer(Player[] players){
        this.players = players;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Fold")){
            Main.foldAction();
        }else if(e.getActionCommand().equals("Check")){
            Main.checkAction();
        }else if(e.getActionCommand().equals("Raise")){
            Main.raiseAction();
        }else if(e.getActionCommand().equals("Call")){
            Main.callAction();
        }
    }
}
