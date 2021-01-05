package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import Logic.Card;
import Logic.Game;
import UI.Components.TableComponent;
import UI.Listeners.MainWindowListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow implements ActionListener {

    // constants
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 725;

    // components
    private JFrame mainFrame;
    private JPanel mainPanel;
    private TableComponent table;
    private JButton checkButton, callButton, foldButton, raiseButton, resetButton, testButton;
    private JTextField raiseTextBox;

    public MainWindow() {

        createWindow();

        drawTable();

        createControls();

        // show zee vindow
        mainFrame.setVisible(true);//making the frame visible
    }

    public TableComponent getTable(){
        return table;
    }

    private void drawTable(){
        Point p;

        // create the table
        table = new TableComponent();
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

    private void createControls(){
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBorder(new MatteBorder(0, 5, 5, 5, Color.BLACK));

        JPanel gameButtonsPanel = new JPanel();

        // create buttons
        foldButton = new JButton("Fold");
        foldButton.setActionCommand("Fold");
        foldButton.addActionListener(this);
        foldButton.setEnabled(false);

        checkButton = new JButton("Check");
        checkButton.setActionCommand("Check");
        checkButton.addActionListener(this);
        checkButton.setEnabled(false);

        callButton = new JButton("Call");
        callButton.setActionCommand("Call");
        callButton.addActionListener(this);
        callButton.setEnabled(false);

        raiseButton = new JButton("Raise");
        raiseButton.setActionCommand("Raise");
        raiseButton.addActionListener(this);
        raiseButton.setEnabled(false);

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
        resetButton.setEnabled(false);

        testButton = new JButton("Test");
        testButton.setActionCommand("Test");
        testButton.addActionListener(this);
        testButton.setEnabled(false);

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
        // TODO: this doesn't close the application
        mainFrame.addWindowListener(new MainWindowListener());
        mainFrame.setResizable(false);

        String title = "ULTRA POKER!";
        if(Main.getNetworker() != null && Main.getNetworker().getIsServer()){
            title += " - Server";
        }else if (Main.getNetworker() != null && !Main.getNetworker().getIsServer()){
            title += " - Client";
        }
        mainFrame.setTitle(title);

        mainFrame.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        mainFrame.add(mainPanel);
    }

    public void setTableCards(Card[] cards){
        table.setTableCards(cards);
    }

    public void updateButtons(){
        checkButton.setEnabled(Game.checkCheckAllowed());
        foldButton.setEnabled(Game.checkFoldAllowed());
        callButton.setEnabled(Game.checkCallAllowed());
        raiseButton.setEnabled(Game.checkRaiseAllowed());
        raiseTextBox.setText("");
    }

    public String getRaiseText(){
        return raiseTextBox.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Fold")){
            foldButtonAction();
        }else if(e.getActionCommand().equals("Check")){
            checkButtonAction();
        }else if(e.getActionCommand().equals("Raise")){
            raiseButtonAction();
        }else if(e.getActionCommand().equals("Call")){
            callButtonAction();
        }else if(e.getActionCommand().equals("Reset")){
            resetButtonAction();
        }else if(e.getActionCommand().equals("Test")){
            testButtonAction();
        }else if(e.getActionCommand().equals("RaiseText")){
            int x = 0;

            x++;
        }
    }

    public void callButtonAction() {
        Game.getPlayers()[Game.getCurrentActionIndex()].call(Game.getBets(), Game.getPlayerHasActed(), Game.getPlayersAllIn());
        if (Game.checkFolds()) {
            Game.endHand();
        } else if (Game.checkBettingRoundCompleted()) {
            Game.nextStreet();
        } else {
            Game.updateCurrentAction();
        }

        System.out.println(Game.checkHandFinished());

        Game.printPlayers();

        updateButtons();
    }

    public void foldButtonAction() {
        int actionIndex = Game.getCurrentActionIndex();
        Game.getPlayers()[actionIndex].fold(Game.getBets(), Game.getPlayersInHand());
        getTable().foldPlayer(actionIndex);

        if (Game.checkFolds()) {
            Game.endHand();
        } else if (Game.checkBettingRoundCompleted()) {
            Game.nextStreet();
        } else {
            Game.updateCurrentAction();
        }

        System.out.println(Game.checkHandFinished());

        Game.printPlayers();

        updateButtons();
    }

    public void raiseButtonAction() {
        int holder = Game.getHighestBet();
        int betValue = Game.getBetValue();
        Game.setLastRaiseSize(betValue - holder);

        Game.getPlayers()[Game.getCurrentActionIndex()].raise(betValue, Game.getBets(), Game.getPlayerHasActed(), Game.getPlayersAllIn());
        if (Game.checkFolds()) {
            Game.endHand();
        } else if (Game.checkBettingRoundCompleted()) {
            Game.nextStreet();
        } else {
            Game.updateCurrentAction();
        }

        System.out.println(Game.checkHandFinished());

        Game.printPlayers();

        updateButtons();
    }

    public void checkButtonAction() {
        Game.getPlayers()[Game.getCurrentActionIndex()].check(Game.getPlayerHasActed());
        if (Game.checkFolds()) {
            Game.endHand();
        } else if (Game.checkBettingRoundCompleted()) {
            Game.nextStreet();
        } else {
            Game.updateCurrentAction();
        }

        System.out.println(Game.checkHandFinished());

        Game.printPlayers();

        updateButtons();
    }

    public void resetButtonAction() {
        Game.resetHand();
        System.out.println("Reset Pressed");
    }

    public void testButtonAction(){

    }
}