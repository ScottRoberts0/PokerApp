package UI.Components;

import Logic.Card;
import Logic.Player;
import UI.GraphicalHelpers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TableComponent extends JPanel {

    private static final int TABLE_WIDTH = 650;
    private static final int TABLE_HEIGHT = 300;
    private static final int CARD_RADIUS_INCREASE = 75;
    private static final int CARD_Y_BUFFER = 30;
    private static final int CARD_X_BUFFER = 10;
    private static final int TABLE_CARD_SPACER = 5;

    private static final int CARD_WIDTH = 52;
    private static final int CARD_HEIGHT = 76;

    private int numPlayers;
    private Player[] players;
    private Card[] tableCards;
    private int[] playerBets;
    private int[] playerStacks;
    private int pot;

    public TableComponent(Player[] players) {
        super(null);
        this.setOpaque(false);

        this.players = players;
        numPlayers = players.length;
        playerBets = new int[numPlayers];
        playerStacks = new int[numPlayers];
        pot = 0;

        tableCards = new Card[5];

        // create table cards
        for (int i = 0; i < 3; i++) {
            tableCards[i] = new Card(0, 0);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

        // draw the table itself
        Insets ins = getInsets();
        g.drawOval(((this.getWidth() - TABLE_WIDTH) / 2) + ins.left, ((this.getHeight() - TABLE_HEIGHT) / 2) + ins.top, TABLE_WIDTH - ins.left - ins.right - 5,
                TABLE_HEIGHT - ins.top - ins.bottom - 5);

        // draw player cards
        drawPlayerCards(g);


        // draw table cards
        drawTableCards(g);

        // draw text
        drawText(g);

    }

    private void drawText(Graphics g){
        // draw player stacks and bets
        for(int i = 0; i < numPlayers; i ++){

        }
    }

    public Point getPlayerPosition(int playerNum){
        if(playerNum >= this.numPlayers){
            //throw new InvalidPlayerNumException("Player number requested is more than total players in the game");
            System.out.println("Player number requested is more than total players in the game");
            return new Point(0,0);
        }

        double angle = (Math.PI * 2 / numPlayers) * (double) playerNum;

        double radius = ((double)TABLE_WIDTH / 2 * TABLE_HEIGHT / 2) /
                Math.sqrt(
                        (Math.pow((double)TABLE_HEIGHT / 2, 2) * Math.pow(Math.sin(angle), 2) + (Math.pow((double)TABLE_WIDTH / 2, 2) * Math.pow(Math.cos(angle), 2)))
                );d

        radius += CARD_RADIUS_INCREASE;

        double x = ((Math.sin(angle) * radius) - CARD_X_BUFFER) * -1;
        double y = (Math.cos(angle) * radius) - CARD_Y_BUFFER;

        return new Point((int) x, (int) y);
    }

    public void drawPlayerCards(Graphics g) {
        for (int player = 0; player < numPlayers; player++) {
            for (int cardNum = 0; cardNum < 2; cardNum++) {
                Point p = getPlayerPosition(player);

                // grab the center of this panel
                Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

                // grab the card image
                BufferedImage cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                        (CARD_WIDTH * (players[player].getHand()[cardNum].getValue() - 2)),
                        (CARD_HEIGHT * players[player].getHand()[cardNum].getSuitValue()),
                        CARD_WIDTH, CARD_HEIGHT);

                Point cardLoc;

                if(cardNum == 0){
                    cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                    cardLoc.x -= 15;
                    cardLoc.y -= 10;
                }else{
                    cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                }

                g.drawImage(cardImage, cardLoc.x, cardLoc.y, null);
            }
        }
    }

    public void drawTableCards(Graphics g) {
        int totalCards = 0;
        Point p;

        for(int i = 0; i < 5; i ++){
            if(tableCards[i] != null)
                totalCards ++;
            else
                break;
        }

        // grab the center of this panel
        Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

        // grab the card image
        for(int i = 0; i < totalCards; i ++) {
            BufferedImage cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                    (CARD_WIDTH * (tableCards[i].getValue() - 2)),
                    (CARD_HEIGHT * tableCards[i].getSuitValue()),
                    CARD_WIDTH, CARD_HEIGHT);

            p = new Point((CARD_WIDTH * i) - (totalCards * CARD_WIDTH / 2) + (TABLE_CARD_SPACER * i), 0);

            Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);

            g.drawImage(cardImage, cardLoc.x, cardLoc.y, null);
        }
    }

    public void setTableCards(Card[] cards){
        tableCards = cards;

        repaint();
    }

    public void setPot(int pot){
        this.pot = pot;
    }

    public void setStack(int playerNum, int stack){

    }

    public void updatePlayers(Player[] players){
        this.players = players;
    }
}

