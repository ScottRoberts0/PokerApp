package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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

    public TableComponent getTable(){
        return table;
    }

    private void drawTable(){
        Point p;

        // create the table
        table = new TableComponent(players);
        table.setBorder(new LineBorder(Color.BLACK, 5));

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
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBorder(new LineBorder(Color.BLACK, 1));

        JPanel gameButtonsPanel = new JPanel();

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

        raiseButton = new JButton("Raise");
        raiseButton.setActionCommand("Raise");
        raiseButton.addActionListener(this);

        // create raise value textbox
        raiseTextBox = new JTextField();
        raiseTextBox.setColumns(8);

        gameButtonsPanel.add(foldButton);
        gameButtonsPanel.add(checkButton);
        gameButtonsPanel.add(callButton);
        gameButtonsPanel.add(raiseButton);
        gameButtonsPanel.add(raiseTextBox);

        JPanel testButtonsPanel = new JPanel();

        resetButton = new JButton("Reset");
        resetButton.setActionCommand("Reset");
        resetButton.addActionListener(this);

        testButton = new JButton("Test");
        testButton.setActionCommand("Test");
        testButton.addActionListener(this);

        testButtonsPanel.add(testButton, BorderLayout.LINE_END);
        testButtonsPanel.add(resetButton, BorderLayout.LINE_END);

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0;
        controlsPanel.add(gameButtonsPanel, c);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(new JPanel(), c);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        controlsPanel.add(testButtonsPanel, c);


        c = new GridBagConstraints();
        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
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
        }else if(e.getActionCommand().equals("RaiseText")){
            int x = 0;

            x++;
        }
    }
}