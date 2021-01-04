package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import UI.Components.TableComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Table implements ActionListener {

    // constants
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 725;

    // components
    private JFrame mainFrame;
    private JPanel mainPanel;
    private TableComponent table;
    private JButton checkButton, callButton, foldButton, raiseButton, resetButton, testButton;
    private JTextField raiseTextBox;
    private Player[] players;

    // misc vars
    private int numPlayers;

    public Table(Player[] players) {
        this.players = players;
        numPlayers = players.length;

        createWindow();

        drawTable();

        drawControls();

        // show zee vindow
        mainFrame.setVisible(true);//making the frame visible

        // create some cards
        table.createPlayerCards();
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

    private void drawControls(){
        // create panel
        JPanel controlsPanel = new JPanel(new GridBagLayout());

        // create buttons
        foldButton = new JButton("Fold");
        foldButton.setActionCommand("Fold");
        foldButton.addActionListener(this);

        checkButton = new JButton("Check");
        checkButton.setActionCommand("Check");
        checkButton.addActionListener(this);

        callButton = new JButton("Call");
        callButton.setActionCommand("Call");
        callButton.addActionListener(this);

        resetButton = new JButton("Reset");
        resetButton.setActionCommand("Reset");
        resetButton.addActionListener(this);

        testButton = new JButton("Test");
        testButton.setActionCommand("Test");
        testButton.addActionListener(this);

        raiseButton = new JButton("Raise");
        raiseButton.setActionCommand("Raise");
        raiseButton.addActionListener(this);

        // create raise value textbox
        raiseTextBox = new JTextField();
        raiseTextBox.setColumns(8);


        controlsPanel.add(foldButton);
        controlsPanel.add(checkButton);
        controlsPanel.add(callButton);
        controlsPanel.add(testButton);
        controlsPanel.add(resetButton);
        controlsPanel.add(raiseButton);
        controlsPanel.add(raiseTextBox);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;

        mainPanel.add(controlsPanel, c);
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

    public void updateButtons(Player[] players, int[] bets){
        checkButton.setEnabled(Game.checkCheckAllowed(bets));
        foldButton.setEnabled(Game.checkFoldAllowed(bets));
        callButton.setEnabled(Game.checkCallAllowed(players, bets));
        raiseButton.setEnabled(Game.checkRaiseAllowed(players));
        raiseTextBox.setText("");

        table.repaint();
    }

    public void updateTable(){
        table.repaint();
    }

    public String getRaiseText(){
        return raiseTextBox.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Fold")){
            Main.foldButtonAction();
        }else if(e.getActionCommand().equals("Check")){
            Main.checkButtonAction();
        }else if(e.getActionCommand().equals("Raise")){
            Main.raiseButtonAction();
        }else if(e.getActionCommand().equals("Call")){
            Main.callButtonAction();
        }else if(e.getActionCommand().equals("Reset")){
            Main.resetButtonAction();
        }else if(e.getActionCommand().equals("Test")){
            Main.testButtonAction();

            table.testAnimation();
        }else if(e.getActionCommand().equals("RaiseText")){
            int x = 0;

            x++;
        }
    }
}
