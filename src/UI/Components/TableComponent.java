package UI.Components;

import Logic.Card;
import UI.GraphicalHelpers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TableComponent extends JPanel {

    private static final int TABLE_WIDTH = 650;
    private static final int TABLE_HEIGHT = 300;
    private static final int CARD_RADIUS_INCREASE = 75;
    private static final int CARD_Y_BUFFER = 15;
    private static final int CARD_X_BUFFER = 10;

    private static final int CARD_WIDTH = 52;
    private static final int CARD_HEIGHT = 76;

    Card[][] playerCards;

    public TableComponent() {
        super(null);

        this.setOpaque(false);

        playerCards = new Card[9][2];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 2; j++) {
                playerCards[i][j] = new Card(0, 0);
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

        // draw the table itself
        Insets ins = getInsets();
        g.drawOval(((this.getWidth() - TABLE_WIDTH) / 2) + ins.left, ((this.getHeight() - TABLE_HEIGHT) / 2) + ins.top, TABLE_WIDTH - ins.left - ins.right - 5,
                TABLE_HEIGHT - ins.top - ins.bottom - 5);

        // draw cards
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 2; j++) {
                drawCards(g, i, j);
            }
        }
    }

    public Point getPlayerPosition(int playerNum) {
        double angle = (Math.PI * 2 / 9) * (double) playerNum;

        double radius = (TABLE_WIDTH / 2 * TABLE_HEIGHT / 2) /
                Math.sqrt(
                        (Math.pow(TABLE_HEIGHT / 2, 2) * Math.pow(Math.sin(angle), 2) + (Math.pow(TABLE_WIDTH / 2, 2) * Math.pow(Math.cos(angle), 2)))
                );

        radius += CARD_RADIUS_INCREASE;

        double x = (Math.sin(angle) * radius) - CARD_X_BUFFER;
        double y = (Math.cos(angle) * radius) - CARD_Y_BUFFER;

        return new Point((int) x, (int) y);
    }

    public void drawCards(Graphics g, int player, int cardNum) {
        Point p = getPlayerPosition(player);

        // grab the center of this panel
        Point panelCenter = new Point(this.getWidth() / 2, this.getHeight() / 2);

        // grab the card image
        BufferedImage cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                (CARD_WIDTH * (playerCards[player][cardNum].getValue() - 2)),
                (CARD_HEIGHT * playerCards[player][cardNum].getSuitValue()),
                CARD_WIDTH, CARD_HEIGHT);

        Point CardLocation = GraphicalHelpers.addPoints(p, panelCenter);

        g.drawImage(cardImage, CardLocation.x, CardLocation.y, null);
    }
}

