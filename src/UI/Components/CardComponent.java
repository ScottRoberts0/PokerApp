package UI.Components;

import UI.GraphicalHelpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Queue;
import javax.swing.*;

public class CardComponent extends JComponent {

    // card image spacing
    // card size = 76x108
    // space = 6

    private final int CARD_WIDTH = 76;
    private final int CARD_HEIGHT = 108;
    private final int CARD_SPACING = 6;

    private int suit, value;
    private BufferedImage cardImage;

    public CardComponent(int suit, int value){
        this.suit = suit;
        this.value = value;

        setOpaque(false);
        setSize(CARD_WIDTH, CARD_HEIGHT);
    }

    public void paint(Graphics g) {
        Insets ins = this.getInsets();
        //g.drawRect(ins.left, ins.top, this.getWidth() - 3, this.getHeight() - 3);

        grabCardImage();

        g.drawImage(cardImage, 0,0,null);
    }

    private void grabCardImage(){
        cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                (CARD_WIDTH * (value - 2)) + (CARD_SPACING * (value - 2)),
                (CARD_HEIGHT * suit) + (CARD_SPACING * suit),
                CARD_WIDTH, CARD_HEIGHT);
    }
}


