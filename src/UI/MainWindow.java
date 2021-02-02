package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeListener;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import Networking.Messages.ActionPromptMessage;
import Networking.Networker;
import UI.Components.TableComponent;
import UI.Listeners.MainWindowListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainWindow implements ActionListener {

    // constants
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 725;

    private boolean isGameStarted = false;

    // components
    private JFrame mainFrame;
    private JPanel mainPanel;
    private TableComponent table;
    private JButton checkButton, callButton, foldButton, raiseButton, resetButton, testButton, test2Button, runHandButton;
    private JSlider betSlider;
    private JTextField raiseTextBox;

    public MainWindow() {
        createWindow();

        drawTable();

        createControls();

        // show zee vindow
        mainFrame.setVisible(true);//making the frame visible
    }

    public boolean getIsGameStarted(){
        return isGameStarted;
    }

    public void setIsGameStarted(boolean isGameStarted){
        this.isGameStarted = isGameStarted;
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

        // create a bet slider and have it affect the raise text box
        if(Game.getPlayers() != null){
            betSlider = new JSlider(0, Game.getPlayers().get(Game.getCurrentActionIndex()).getStack());
        }else{
            betSlider = new JSlider(0, 0);
        }

        ChangeListener e = e1 -> setTextValue(); //idk wtf this means but it was cleaner than the other thing
        betSlider.addChangeListener(e);

        gameButtonsPanel.add(foldButton);
        gameButtonsPanel.add(checkButton);
        gameButtonsPanel.add(callButton);
        gameButtonsPanel.add(raiseButton);
        gameButtonsPanel.add(raiseTextBox);
        gameButtonsPanel.add(betSlider);

        JPanel testButtonsPanel = new JPanel();

        resetButton = new JButton("Reset");
        resetButton.setActionCommand("Reset");
        resetButton.addActionListener(this);
        resetButton.setEnabled(true);

        testButton = new JButton("Test");
        testButton.setActionCommand("Test");
        testButton.addActionListener(this);
        testButton.setEnabled(true);

        test2Button = new JButton("0");
        test2Button.setActionCommand("Test2");
        test2Button.addActionListener(this);
        test2Button.setEnabled(true);

        runHandButton = new JButton("Run Hand");
        runHandButton.setActionCommand("Run");
        runHandButton.addActionListener(this);
        runHandButton.setEnabled(true);

        testButtonsPanel.add(runHandButton, BorderLayout.LINE_END);
        testButtonsPanel.add(test2Button, BorderLayout.LINE_END);
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
        //mainFrame.setDefaultCloseOperation(JFrame.);
        // TODO: this doesn't close the application
        mainFrame.addWindowListener(new MainWindowListener());
        mainFrame.setResizable(false);

        String title = "ULTRA POKER!";
        if(Networker.getInstance() != null && Networker.getInstance().getIsServer()){
            title += " - Server";
        }else if (Networker.getInstance() != null && !Networker.getInstance().getIsServer()){
            title += " - Client";
        }
        mainFrame.setTitle(title);

        mainFrame.setBounds(450 + (Game.getLocalPlayerNum() * 100),  + (Game.getLocalPlayerNum() * 100), WINDOW_WIDTH, WINDOW_HEIGHT);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        mainFrame.add(mainPanel);
    }

    public void setTableCards(Card[] cards){
        table.setTableCards(cards);
    }

    public void updateButtons(){
        // server does this a bit different
        if(Networker.getInstance() != null){
            // disable all the buttons
            checkButton.setEnabled(false);
            foldButton.setEnabled(false);
            callButton.setEnabled(false);
            raiseButton.setEnabled(false);
            testButton.setEnabled(false);

            // check to see if the new action is to a client
            if(Game.getCurrentActionIndex() > 0 && Networker.getInstance().getIsServer()){
                // send an action prompt message which tells the current client what actions it can take
                Networker.getInstance().sendActionPrompt();

                return;
            }

            // if this is a client, just back out.
            if(!Networker.getInstance().getIsServer()){
                return;
            }
        }

        checkButton.setEnabled(Game.checkCheckAllowed());
        foldButton.setEnabled(Game.checkFoldAllowed());
        callButton.setEnabled(Game.checkCallAllowed());
        raiseButton.setEnabled(Game.checkRaiseAllowed());
        testButton.setEnabled(Game.checkRebuyAllowed());
        updateBetSlider();
    }

    public void updateButtons(int actionsAllowed, int minBet, int maxBet){
        // enable the buttons based off the bit mask
        checkButton.setEnabled((actionsAllowed & ActionPromptMessage.CHECK_BIT) == ActionPromptMessage.CHECK_BIT ? true : false);
        foldButton.setEnabled((actionsAllowed & ActionPromptMessage.FOLD_BIT) == ActionPromptMessage.FOLD_BIT ? true : false);
        callButton.setEnabled((actionsAllowed & ActionPromptMessage.CALL_BIT) == ActionPromptMessage.CALL_BIT ? true : false);
        raiseButton.setEnabled((actionsAllowed & ActionPromptMessage.RAISE_BIT) == ActionPromptMessage.RAISE_BIT ? true : false);

        // update the bet slider
        updateBetSlider(maxBet, minBet);
    }

    public void updateBetSlider(){
        int actionIndex = Game.getCurrentActionIndex();
        ArrayList<Player> players = Game.getPlayers();

        int maxBet = players.get(actionIndex).getStack() +
                Game.getCurrentPot().getBets()[players.get(actionIndex).getPlayerNum()];

        betSlider.setMaximum(maxBet);
        if (Game.getStreet() != 0 && Game.getHighestBet() == 0) {
            betSlider.setValue(Game.getMinBet());
        } else {
            betSlider.setValue(Game.getLastRaiseSize() + Game.getHighestBet());
        }

        // update the textbox value while we're here
        setTextValue();
    }

    public void updateBetSlider(int maxBet, int minBet){
        betSlider.setMaximum(maxBet);
        betSlider.setMinimum(minBet);
        betSlider.setValue(minBet);

        // update the textbox value while we're here
        setTextValue();
    }

    public String getRaiseText(){
        return raiseTextBox.getText();
    }

    public void setTextValue() {
        raiseTextBox.setText(Integer.toString(betSlider.getValue()));
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
        }else if(e.getActionCommand().equals("Reset")) {
            resetButtonAction();
        } else if(e.getActionCommand().equals("Run")) {
            runHandButtonAction();
        }else if(e.getActionCommand().equals("Test")){
            testButtonAction();
        }else if(e.getActionCommand().equals("Test2")){
            test2ButtonAction();
        }else if(e.getActionCommand().equals("RaiseText")){
            int x = 0;

            x++;
        }
    }

    public void callButtonAction() {
        if(Networker.getInstance() != null && !Networker.getInstance().getIsServer()){
            // client does things a bit different
            Networker.getInstance().sendClientAction(ActionPromptMessage.CALL_BIT);
        }else {
            Game.getPlayers().get(Game.getCurrentActionIndex()).call(Game.getCurrentPot());

            if (Game.checkFolds()) {
                Game.endHand();
            } else if (Game.checkBettingRoundCompleted()) {
                Game.nextStreet();
            } else {
                Game.updateCurrentAction();
            }

            Game.printPlayersAndPot();
        }
        updateButtons();
    }

    public void foldButtonAction() {
        if(Networker.getInstance() != null && !Networker.getInstance().getIsServer()){
            // client does things a bit different
            Networker.getInstance().sendClientAction(ActionPromptMessage.FOLD_BIT);
        }else {
            int actionIndex = Game.getCurrentActionIndex();

            Game.getPlayers().get(actionIndex).fold(Game.getPots());

            getTable().foldPlayer(actionIndex);

            if (Game.checkFolds()) {
                Game.endHand();
            } else if (Game.checkBettingRoundCompleted()) {
                Game.nextStreet();
            } else {
                Game.updateCurrentAction();
            }

            Game.printPlayersAndPot();
        }
        updateButtons();
    }

    public void raiseButtonAction() {
        if(Networker.getInstance() != null && !Networker.getInstance().getIsServer()){
            // client does things a bit different
            Networker.getInstance().sendClientAction(ActionPromptMessage.RAISE_BIT);
        }else {
            int holder = Game.getHighestBet();
            int betValue = Game.formatBetValue();

            Game.setLastRaiseSize(betValue - holder);

            Game.getPlayers().get(Game.getCurrentActionIndex()).raise(betValue, Game.getCurrentPot());

            if (Game.checkFolds()) {
                Game.endHand();
            } else if (Game.checkBettingRoundCompleted()) {
                Game.nextStreet();
            } else {
                Game.updateCurrentAction();
            }

            Game.printPlayersAndPot();
        }
        updateButtons();
    }

    public void checkButtonAction() {
        if(Networker.getInstance() != null && !Networker.getInstance().getIsServer()){
            // client does things a bit different
            Networker.getInstance().sendClientAction(ActionPromptMessage.CHECK_BIT);
        }else {
            Game.getPlayers().get(Game.getCurrentActionIndex()).check(Game.getCurrentPot());

            if (Game.checkFolds()) {
                Game.endHand();
            } else if (Game.checkBettingRoundCompleted()) {
                Game.nextStreet();
            } else {
                Game.updateCurrentAction();
            }

            Game.printPlayersAndPot();
        }
        updateButtons();
    }

    public void resetButtonAction() {
        Game.resetStacks();
        Game.resetHand();
        System.out.println("Reset Pressed");
    }

    public void testButtonAction(){
        Game.rebuy();
    }

    public void runHandButtonAction() {
        Game.runHand();
    }

    public void test2ButtonAction(){

    }
}