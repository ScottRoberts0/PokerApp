package UI.Components;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import UI.Animation.Animatable;
import UI.Animation.AnimationThread;
import UI.GraphicalHelpers;

import javax.swing.JPanel;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.List;

public class TableComponent extends JPanel {

    private static final int TABLE_WIDTH = 650;
    private static final int TABLE_HEIGHT = 300;
    private static final int CARD_RADIUS_BUFFER = 75;
    private static final int POT_RADIUS_BUFFER = 45;
    private static final int CARD_Y_BUFFER = 0;
    private static final int CARD_X_BUFFER = 0;
    private static final int CARD_Y_STAGGER = 10;
    private static final int CARD_X_STAGGER = 25;
    private static final int STACK_BUFFER = 15;
    private static final int TABLE_CARD_SPACER = 5;

    private static final int CARD_WIDTH = 52;
    private static final int CARD_HEIGHT = 76;

    private int numPlayers;
    private Player[] players;
    private Card[] tableCards;
    private int pot;
    private Point[] playerPositions;
    private CardComponent[][] playerCards;

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

        // test out animation system
        // create and start the animation thread
        new AnimationThread(this);
        AnimationThread.getInstance().start();
    }

    //TODO ask tyler: this method never seems to be called, so how does it draw all the shit?
    public void paint(Graphics g) {
        super.paint(g);

        // paint the animation components
        AnimationThread.getInstance().paint(g);

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

        // draw action indicator
        drawActionIndicators(g);
    }

    /**
     * Draws dealer button, action index, and blind indicators
     *
     * @param g
     */
    private void drawActionIndicators(Graphics g){
        // store the default colour to switch back at the end of this function
        Color currentColour = g.getColor();

        // draw action indicator (red)
        g.setColor(Color.RED);
        Point playerPosition = getPlayerPosition(Game.getCurrentActionIndex());
        g.fillOval((this.getWidth() / 2) + playerPosition.x - CARD_WIDTH - 25, (this.getHeight() / 2) + playerPosition.y, 25, 25);

        // draw dealer button (transparent with a D in the middle)
        g.setColor(Color.BLACK);
        int index = Game.getSmallBlindIndex();
        if(index == 0){
            index = players.length - 1;
        }else{
            index --;
        }
        double[] angleRadius = getPlayerAngleRadius(index);

        angleRadius[1] -= POT_RADIUS_BUFFER + 20;

        double x = (Math.sin(angleRadius[0]) * angleRadius[1]) * -1;
        double y = (Math.cos(angleRadius[0]) * angleRadius[1]);

        g.drawString("D",
                (this.getWidth() / 2) + (int)x,
                (this.getHeight() / 2) + (int)y);
        g.drawOval((this.getWidth() / 2) + (int)x - 5, (this.getHeight() / 2) + (int) y - 15, 20, 20);

        g.setColor(currentColour);
    }

    private void drawText(Graphics g){
        // get the table center to determine which side of the board the player is on
        Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);
        // draw player stacks and bets
        for(int i = 0; i < numPlayers; i ++){
            int stack = players[i].getStack();
            int bet = players[i].getMoneyInPot();
            // grab the string width
            int stackStringWidth = g.getFontMetrics().stringWidth(stack + "");
            int betStringWidth = g.getFontMetrics().stringWidth(bet + "");
            int stringHeight = g.getFont().getSize();

            // stack below cards
            g.drawString(stack + "",
                    (this.getWidth() / 2) + playerPositions[i].x,
                    (this.getHeight() / 2) + playerPositions[i].y + (CARD_HEIGHT / 2) + STACK_BUFFER);

            // pot in table
            double[] angleRadius = getPlayerAngleRadius(i);

            angleRadius[1] -= POT_RADIUS_BUFFER;

            double x = (Math.sin(angleRadius[0]) * angleRadius[1]) * -1;
            double y = (Math.cos(angleRadius[0]) * angleRadius[1]);

            g.drawString(bet + "",
                    (this.getWidth() / 2) + (int)x,
                    (this.getHeight() / 2) + (int)y);
        }
    }

    public double[] getPlayerAngleRadius(int playerNum){
        double angle = (Math.PI * 2 / numPlayers) * (double) playerNum;

        double radius = ((double)TABLE_WIDTH / 2 * TABLE_HEIGHT / 2) /
                Math.sqrt(
                        (Math.pow((double)TABLE_HEIGHT / 2, 2) * Math.pow(Math.sin(angle), 2) +
                                (Math.pow((double)TABLE_WIDTH / 2, 2) * Math.pow(Math.cos(angle), 2)))
                );
        return new double[] {angle, radius};
    }

    public Point getPlayerPosition(int playerNum){
        if(playerNum >= this.numPlayers){
            //throw new InvalidPlayerNumException("Player number requested is more than total players in the game");
            System.out.println("Player number requested is more than total players in the game");
            return new Point(0,0);
        }

        double[] angleRadius = getPlayerAngleRadius(playerNum);

        angleRadius[1] += CARD_RADIUS_BUFFER;

        double x = ((Math.sin(angleRadius[0]) * angleRadius[1]) - CARD_X_BUFFER) * -1;
        double y = (Math.cos(angleRadius[0]) * angleRadius[1]) - CARD_Y_BUFFER;

        return new Point((int) x, (int) y);
    }

    public void createPlayerCards(){
        playerCards = new CardComponent[numPlayers][2];

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
                // TODO: Draw card backs instead of card value if the player has folded
                //       After next git pull, there should be a players[player].getHasFolded() function
                BufferedImage cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                        (CARD_WIDTH * (players[player].getHand()[cardNum].getValue() - 2)),
                        (CARD_HEIGHT * players[player].getHand()[cardNum].getSuitValue()),
                        CARD_WIDTH, CARD_HEIGHT);

                Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                cardLoc.y -= (CARD_HEIGHT / 2);

                if(cardNum == 0) {
                    cardLoc.x -= CARD_X_STAGGER;
                    cardLoc.y -= CARD_Y_STAGGER;
                }

                playerCards[player][cardNum] = new CardComponent(-CARD_WIDTH, -CARD_HEIGHT, players[player].getHand());
                playerCards[player][cardNum].moveTo(cardLoc.x, cardLoc.y, 500, false);

                AnimationThread.getInstance().addAnimatableObject(playerCards[player][cardNum]);
            }
        }
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
                // TODO: Draw card backs instead of card value if the player has folded
                //       After next git pull, there should be a players[player].getHasFolded() function
                BufferedImage cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                        (CARD_WIDTH * (players[player].getHand()[cardNum].getValue() - 2)),
                        (CARD_HEIGHT * players[player].getHand()[cardNum].getSuitValue()),
                        CARD_WIDTH, CARD_HEIGHT);
                // TODO: After next git pull, there should be a players[player].getHasFolded() function
                if(!players[player].getHasFolded()) {
                    cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                            (CARD_WIDTH * (players[player].getHand()[cardNum].getValue() - 2)),
                            (CARD_HEIGHT * players[player].getHand()[cardNum].getSuitValue()),
                            CARD_WIDTH, CARD_HEIGHT);


                    Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                    cardLoc.y -= (CARD_HEIGHT / 2);

                    if (cardNum == 0) {
                        cardLoc.x -= CARD_X_STAGGER;
                        cardLoc.y -= CARD_Y_STAGGER;
                    }

                    g.drawImage(cardImage, cardLoc.x, cardLoc.y, null);
                } else {
                    cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                            0, CARD_HEIGHT * 4, CARD_WIDTH, CARD_HEIGHT);


                    Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                    cardLoc.y -= (CARD_HEIGHT / 2);

                    if (cardNum == 0) {
                        cardLoc.x -= CARD_X_STAGGER;
                        cardLoc.y -= CARD_Y_STAGGER;
                    }

                    g.drawImage(cardImage, cardLoc.x, cardLoc.y, null);
                }
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

    private int count = 0;
    CardComponent animCard;

    public void testAnimation(){
        List<Animatable> cards = AnimationThread.getInstance().getAnimationObjects();

        for(int i = 0; i < cards.size(); i++ ){
            cards.get(i).setAnimating(true);
        }
    }
}

