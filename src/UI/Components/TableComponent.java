package UI.Components;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import UI.Animation.Animatable;
import UI.Animation.AnimationThread;
import UI.Animation.InOutQuintEasingFunction;
import UI.Animation.OutSineEasingFunction;
import UI.GraphicalHelpers;
import UI.Main;

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
    private static final int CARD_RADIUS_BUFFER = 85;
    private static final int POT_RADIUS_BUFFER = 45;
    private static final int CARD_Y_BUFFER = 0;
    private static final int CARD_X_BUFFER = 0;
    private static final int CARD_Y_STAGGER = 10;
    private static final int CARD_X_STAGGER = 25;
    private static final int STACK_BUFFER = 15;
    private static final int TABLE_CARD_SPACER = 5;
    private static final int POT_LABEL_SPACER = 15;
    private static final int PLAYER_NAME_SPACER = 15;

    public static final int CARD_WIDTH = 52;
    public static final int CARD_HEIGHT = 76;

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
        int panelWidth = this.getWidth();
        int x = ((panelWidth - TABLE_WIDTH) / 2) + ins.left;
        g.drawOval(x, ((this.getHeight() - TABLE_HEIGHT) / 2) + ins.top, TABLE_WIDTH - ins.left - ins.right,
                TABLE_HEIGHT - ins.top - ins.bottom);

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

        angleRadius[1] -= POT_RADIUS_BUFFER / 2;

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
            int pot = Main.getPot();
            String name = players[i].getPlayerName();

            int potStringWidth = g.getFontMetrics().stringWidth(pot + "");

            // stack below cards
            g.drawString(stack + "",
                    panelCenter.x + playerPositions[i].x,
                    panelCenter.y + playerPositions[i].y + (CARD_HEIGHT / 2) + STACK_BUFFER);

            // pot in table
            double[] angleRadius = getPlayerAngleRadius(i);

            angleRadius[1] -= POT_RADIUS_BUFFER;

            double x = (Math.sin(angleRadius[0]) * angleRadius[1]) * -1;
            double y = (Math.cos(angleRadius[0]) * angleRadius[1]);

            g.drawString(bet + "",
                    panelCenter.x  + (int)x,
                    panelCenter.y + (int)y);

            // draw the pot
            g.drawString("POT: " + pot,
                    panelCenter.x - (potStringWidth / 2),
                    panelCenter.y - (CARD_HEIGHT / 2) - POT_LABEL_SPACER
                    );

            // draw the player name
            g.drawString(name,
                    panelCenter.x + playerPositions[i].x,
                    panelCenter.y + playerPositions[i].y - (CARD_HEIGHT / 2) - PLAYER_NAME_SPACER);
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

    public void createPlayerCards(boolean animateNow){
        createPlayerCards();
        if(animateNow){
            animatePlayerCards();
        }
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

                // TODO: Make cards come from the dealer
                int dealerPosition = Game.getDealerIndex();
                Point dealerPoint = GraphicalHelpers.addPoints(playerPositions[dealerPosition], panelCenter);
                playerCards[player][cardNum] = new CardComponent(dealerPoint.x, dealerPoint.y, players[player].getHand()[cardNum]);
                playerCards[player][cardNum].moveTo(cardLoc.x, cardLoc.y, 500, false);

                AnimationThread.getInstance().addAnimatableObject(playerCards[player][cardNum]);
            }
        }
    }

    public void deletePlayerCards(){
        AnimationThread.getInstance().removeAnimatableObjects();
    }

    /*public void drawPlayerCards(Graphics g) {
        for (int player = 0; player < numPlayers; player++) {
            if(players[player].getHand()[0] == null){
                // this player has no cards. Draw nothing.
                continue;
            }
            for (int cardNum = 0; cardNum < 2; cardNum++) {
                // grab a local copy of the player position
                Point p = playerPositions[player];

                // grab the center of this panel
                Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

                // grab the card image based off of suit value, or the cardback if folded
                BufferedImage cardImage;
                if(!players[player].getHasFolded()) {
                    cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                            (CARD_WIDTH * (players[player].getHand()[cardNum].getValue() - 2)),
                            (CARD_HEIGHT * players[player].getHand()[cardNum].getSuitValue()),
                            CARD_WIDTH, CARD_HEIGHT);

                } else {
                    cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                            0, CARD_HEIGHT * 4, CARD_WIDTH, CARD_HEIGHT);
                }

                // Sum the panel center and the player position
                Point cardLocation = GraphicalHelpers.addPoints(p, panelCenter);
                cardLocation.y -= (CARD_HEIGHT / 2);

                // offset the first card
                if (cardNum == 0) {
                    cardLocation.x -= CARD_X_STAGGER;
                    cardLocation.y -= CARD_Y_STAGGER;
                }

                // draw it
                g.drawImage(cardImage, cardLocation.x, cardLocation.y, null);
            }
        }
    }*/

    public void drawTableCards(Graphics g) {
        int totalCards = 0;
        Point p;

        // grab the center of this panel
        Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

        // grab the card image
        for(int i = 0; i < 5; i ++) {
            if(tableCards[i] != null) {
                BufferedImage cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                        (CARD_WIDTH * (tableCards[i].getValue() - 2)),
                        (CARD_HEIGHT * tableCards[i].getSuitValue()),
                        CARD_WIDTH, CARD_HEIGHT);

                p = new Point((CARD_WIDTH * i) - (5 * CARD_WIDTH / 2) + (TABLE_CARD_SPACER * i), 0);

                Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);

                g.drawImage(cardImage, cardLoc.x, cardLoc.y, null);
            }
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

    public void animatePlayerCards(){
        List<Animatable> cards = AnimationThread.getInstance().getAnimationObjects();

        for(int i = 0; i < cards.size(); i++ ){
            cards.get(i).setAnimating(true);
        }
    }

    public void foldPlayer(int playerNum){
        playerCards[playerNum][0].setHasFolded(true);
        playerCards[playerNum][1].setHasFolded(true);
    }
}

