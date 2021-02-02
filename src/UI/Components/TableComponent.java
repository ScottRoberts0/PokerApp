package UI.Components;

import Logic.Card;
import Logic.Game;
import Logic.Pot;
import Networking.Networker;
import UI.Animation.Animatable;
import UI.Animation.AnimationThread;
import UI.GraphicalHelpers;
import UI.Main;

import javax.swing.*;
import java.awt.*;
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

    private Card[] tableCards;
    private Point[] playerPositions;
    private CardComponent[][] playerCards;
    private CardComponent[] tableCardsAnimated;

    public TableComponent() {
        super(null);
        this.setOpaque(false);

        // initialize arrays
        tableCards = new Card[5];
        tableCardsAnimated = new CardComponent[5];

        // create and start the animation thread
        new AnimationThread(this);
        AnimationThread.getInstance().start();
    }

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

        // only try to paint players if there are some
        if (Main.getGameWindow().getIsGameStarted()) {
            // update labels
            drawText(g);

            // draw action indicator
            drawActionIndicators(g);
        }
    }

    public void initPlayers() {
        // grab the positions of the players and store them so we don't have to recalculate every frame
        playerPositions = new Point[Game.getPlayers().size()];
        for (int i = 0; i < Game.getPlayers().size(); i++) {
            playerPositions[i] = getPlayerPosition(i);
        }
    }

    /**
     * Draws dealer button, action index, and blind indicators
     *
     * @param g
     */
    private void drawActionIndicators(Graphics g) {
        // store the default colour to switch back at the end of this function
        Color currentColour = g.getColor();

        // draw action indicator (red)
        g.setColor(Color.RED);
        Point playerPosition = getPlayerPosition(Game.getCurrentActionIndex());
        g.fillOval((this.getWidth() / 2) + playerPosition.x - CARD_WIDTH - 25, (this.getHeight() / 2) + playerPosition.y, 25, 25);

        // draw dealer button (transparent with a D in the middle)
        g.setColor(Color.BLACK);
        int index = Game.getDealerIndex();
        double[] angleRadius = getPlayerAngleRadius(index);

        angleRadius[1] -= POT_RADIUS_BUFFER / 2;

        double x = (Math.sin(angleRadius[0]) * angleRadius[1]) * -1;
        double y = (Math.cos(angleRadius[0]) * angleRadius[1]);

        g.drawString("D",
                (this.getWidth() / 2) + (int) x,
                (this.getHeight() / 2) + (int) y);
        g.drawOval((this.getWidth() / 2) + (int) x - 5, (this.getHeight() / 2) + (int) y - 15, 20, 20);

        g.setColor(currentColour);
    }

    private void drawText(Graphics g) {
        // get the table center to determine which side of the board the player is on
        Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

        // draw player stacks and bets
        for (int i = 0; i < Game.getPlayers().size(); i++) {
            int stack = Game.getPlayers().get(i).getStack();

            int bet = Game.getPlayers().get(i).getCurrentBet();

            StringBuilder pots = new StringBuilder();

            for (Pot pot : Game.getPots()) {
                if(pot.getPotValue() > 0) {
                    pots.append(pot).append(" ");
                }
            }

            String name = Game.getPlayers().get(i).getPlayerName();

            int potStringWidth = g.getFontMetrics().stringWidth(pots + "");

            // stack below cards
            g.drawString(stack + "",
                    panelCenter.x + playerPositions[i].x,
                    panelCenter.y + playerPositions[i].y + (CARD_HEIGHT / 2) + STACK_BUFFER);

            double[] angleRadius = getPlayerAngleRadius(i);

            angleRadius[1] -= POT_RADIUS_BUFFER;

            double x = (Math.sin(angleRadius[0]) * angleRadius[1]) * -1;
            double y = (Math.cos(angleRadius[0]) * angleRadius[1]);

            if (bet != 0) {
                g.drawString(bet + "",
                        panelCenter.x + (int) x,
                        panelCenter.y + (int) y);
            }

            // draw the pots
            g.drawString(pots.toString(),
                    panelCenter.x - (potStringWidth / 2),
                    panelCenter.y - (CARD_HEIGHT / 2) - POT_LABEL_SPACER
            );

            // draw the player name
            g.drawString(name,
                    panelCenter.x + playerPositions[i].x,
                    panelCenter.y + playerPositions[i].y - (CARD_HEIGHT / 2) - PLAYER_NAME_SPACER);
        }
    }

    public double[] getPlayerAngleRadius(int playerNum) {
        double angle = (Math.PI * 2 / Game.getPlayers().size()) * (double) playerNum;

        double radius = ((double) TABLE_WIDTH / 2 * TABLE_HEIGHT / 2) /
                Math.sqrt(
                        (Math.pow((double) TABLE_HEIGHT / 2, 2) * Math.pow(Math.sin(angle), 2) +
                                (Math.pow((double) TABLE_WIDTH / 2, 2) * Math.pow(Math.cos(angle), 2)))
                );
        return new double[]{angle, radius};
    }

    public Point getPlayerPosition(int playerNum) {
        if (playerNum >= Game.getPlayers().size()) {
            //throw new InvalidPlayerNumException("Player number requested is more than total players in the game");
            System.out.println("Player number requested is more than total players in the game");
            return new Point(0, 0);
        }

        double[] angleRadius = getPlayerAngleRadius(playerNum);

        angleRadius[1] += CARD_RADIUS_BUFFER;

        double x = ((Math.sin(angleRadius[0]) * angleRadius[1]) - CARD_X_BUFFER) * -1;
        double y = (Math.cos(angleRadius[0]) * angleRadius[1]) - CARD_Y_BUFFER;

        return new Point((int) x, (int) y);
    }

    public void createPlayerCards(boolean animateNow) {
        createPlayerCards();
        if (animateNow) {
            animateCards();
        }
    }

    public void createPlayerCards() {
        if(playerPositions == null){
            initPlayers();
        }

        playerCards = new CardComponent[Game.getPlayers().size()][2];

        for (int player = 0; player < Game.getPlayers().size(); player++) {
            if (Game.getPlayers().get(player).getHand()[0] == null) {
                // this player has no cards. Create nothing.
                continue;
            }
            for (int cardNum = 0; cardNum < 2; cardNum++) {
                Point p = playerPositions[player];

                // grab the center of this panel
                Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

                // grab the player's card location
                Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);
                cardLoc.y -= (CARD_HEIGHT / 2);

                if (cardNum == 0) {
                    cardLoc.x -= CARD_X_STAGGER;
                    cardLoc.y -= CARD_Y_STAGGER;
                }

                // Make cards come from the dealer
                int dealerPosition = Game.getDealerIndex();
                Point dealerPoint = GraphicalHelpers.addPoints(playerPositions[dealerPosition], panelCenter);

                // create the card component
                playerCards[player][cardNum] = new CardComponent(dealerPoint.x, dealerPoint.y, Game.getPlayers().get(player).getHand()[cardNum]);
                playerCards[player][cardNum].moveTo(cardLoc.x, cardLoc.y, 500, false);

                // if this is not the local player's cards, hide it as if it was folded
                if(Networker.getInstance() != null && player != Game.getLocalPlayerNum()) {
                    playerCards[player][cardNum].setHasFolded(true);
                }

                AnimationThread.getInstance().addAnimatableObject(playerCards[player][cardNum]);
            }
        }
    }

    public void deletePlayerCards() {
        AnimationThread.getInstance().removeAnimatableObjects();
    }

    public void createTableCards() {
        Point p;

        // grab the center of this panel
        Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

        // grab the card image
        for (int i = 0; i < 5; i++) {
            if (tableCards[i] != null && tableCardsAnimated[i] == null) {
                p = new Point((CARD_WIDTH * i) - (5 * CARD_WIDTH / 2) + (TABLE_CARD_SPACER * i), 0);

                Point cardLoc = GraphicalHelpers.addPoints(p, panelCenter);

                int dealerPosition = Game.getDealerIndex();
                Point dealerPoint = GraphicalHelpers.addPoints(playerPositions[dealerPosition], panelCenter);

                tableCardsAnimated[i] = new CardComponent(dealerPoint.x, dealerPoint.y, new Card(tableCards[i].getValue(), tableCards[i].getSuitValue()));
                tableCardsAnimated[i].moveTo(cardLoc.x, cardLoc.y, 500, true);

                AnimationThread.getInstance().addAnimatableObject(tableCardsAnimated[i]);
            }
        }
    }

    public void setTableCards(Card[] cards) {
        tableCards = cards;
        createTableCards();
        createTableCards();
    }

    private int count = 0;
    CardComponent animCard;

    public void animateCards() {
        List<Animatable> cards = AnimationThread.getInstance().getAnimationObjects();

        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setAnimating(true);
        }
    }

    public void foldPlayer(int playerNum) {
        playerCards[playerNum][0].setHasFolded(true);
        playerCards[playerNum][1].setHasFolded(true);
    }

    public void resetTableCardsAnimated(){
        tableCardsAnimated = new CardComponent[5];
    }
}