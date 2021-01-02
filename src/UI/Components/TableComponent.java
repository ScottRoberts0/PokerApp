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
    private int pot;
    private Point[] playerPositions;

    public TableComponent(Player[] players) {
        super(null);
        this.setOpaque(false);

        // store a reference to the players
        this.players = players;
        numPlayers = players.length;

        // initialize arrays
        tableCards = new Card[5];
        pot = 0;

        // grab the positions of the players and store them so we don't have to recalculate every frame
        playerPositions = new Point[numPlayers];
        for(int i = 0; i < numPlayers; i ++){
            playerPositions[i] = getPlayerPosition(i);
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

        // update labels
        drawText(g);
    }

    private void drawText(Graphics g){
        // get the table center to determine which side of the board the player is on
        Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);
        // draw player stacks and bets
        for(int i = 0; i < numPlayers; i ++){
            int stack = players[i].getStack();
            int bet = 0;
            // grab the string width
            int stackStringWidth = g.getFontMetrics().stringWidth(stack + "");
            int betStringWidth = g.getFontMetrics().stringWidth(bet + "");
            int stringHeight = g.getFont().getSize();

            // TODO: Stack above/below cards. Put bets on the table using the radius maths.
            // draw player stack and bet on outside
            if(playerPositions[i].x > 0){
                // player is on the right side of the board, put the stack on the right and bet to the left
                g.drawString(stack + "",
                        (this.getWidth() / 2) + playerPositions[i].x + CARD_WIDTH,
                        (this.getHeight() / 2) + playerPositions[i].y - (stringHeight / 2));

                g.drawString(bet + "",
                        (this.getWidth() / 2) + playerPositions[i].x - CARD_WIDTH - betStringWidth,
                        (this.getHeight() / 2) + playerPositions[i].y + (stringHeight / 2));
            }else{
                // player is on the left side of the board, put the stack on the left and bet to the right
                g.drawString(stack + "",
                        (this.getWidth() / 2) + playerPositions[i].x - CARD_WIDTH - stackStringWidth,
                        (this.getHeight() / 2) + playerPositions[i].y + (stringHeight / 2));

                g.drawString(bet + "",
                        (this.getWidth() / 2) + playerPositions[i].x + CARD_WIDTH,
                        (this.getHeight() / 2) + playerPositions[i].y - (stringHeight / 2));
            }
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
                        (Math.pow((double)TABLE_HEIGHT / 2, 2) * Math.pow(Math.sin(angle), 2) +
                         (Math.pow((double)TABLE_WIDTH / 2, 2) * Math.pow(Math.cos(angle), 2)))
                );

        radius += CARD_RADIUS_INCREASE;

        double x = ((Math.sin(angle) * radius) - CARD_X_BUFFER) * -1;
        double y = (Math.cos(angle) * radius) - CARD_Y_BUFFER;

        return new Point((int) x, (int) y);
    }

    public void drawPlayerCards(Graphics g) {
        for (int player = 0; player < numPlayers; player++) {
            if(players[player].getHand()[0] == null){
                // this player has no cards. Draw nothing.
                continue;
            }
            for (int cardNum = 0; cardNum < 2; cardNum++) {
                Point p = playerPositions[player];

                // grab the center of this panel
                Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

                // grab the card image
                BufferedImage cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                        (CARD_WIDTH * (players[player].getHand()[cardNum].getValue() - 2)),
                        (CARD_HEIGHT * players[player].getHand()[cardNum].getSuitValue()),
                        CARD_WIDTH, CARD_HEIGHT);

                Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                cardLoc.y -= (CARD_HEIGHT / 2);

                if(cardNum == 0 && p.x > 0){
                    cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                    cardLoc.x -= 15;
                    cardLoc.y -= 5;
                }else if (cardNum == 1 && p.x < 0){
                    cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                    cardLoc.x -= 15;
                }else if (cardNum == 0 && p.x < 0){
                    cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                    cardLoc.x -= CARD_WIDTH;
                    cardLoc.y -= 5;
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

    public void updatePlayers(Player[] players){
        this.players = players;
    }
}

