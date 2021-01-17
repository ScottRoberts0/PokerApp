package UI.Components;

import Logic.Card;
import UI.Animation.Animatable;
import UI.Animation.AnimationThread;
import UI.Animation.EasingFunction;
import UI.Animation.LinearEasingFunction;
import UI.GraphicalHelpers;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CardComponent extends Animatable {

    private Card card;
    private boolean hasFolded;

    public CardComponent(int x, int y, Card card, EasingFunction easingFunction){
        this.currentX = x;
        this.currentY = y;

        this.card = card;

        this.currentTime = 0;
        this.totalDuration = 0;

        this.animating = false;

        this.easingFunction = easingFunction;
    }

    public CardComponent(int x, int y, Card card){
        this(x, y, card, new LinearEasingFunction());
    }

    public String toString() {
        return card.getValue() + " " + card.getSuitValue();
    }

    @Override
    protected void paint(Graphics g) {
        // only re-calculate positions on objects that are moving
        if(this.animating){
            nextStep();
        }

        // paint the card
        BufferedImage cardImage;
        if(!hasFolded) {
            cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                    (TableComponent.CARD_WIDTH * (card.getValue() - 2)),
                    (TableComponent.CARD_HEIGHT * card.getSuitValue()),
                    TableComponent.CARD_WIDTH, TableComponent.CARD_HEIGHT);

        } else {
            //TODO why can we access TableComponent.CARD_HEIGHT? it it set to private in TableComponent
            cardImage = GraphicalHelpers.getCardsImage().getSubimage(
                    0, TableComponent.CARD_HEIGHT * 4, TableComponent.CARD_WIDTH, TableComponent.CARD_HEIGHT);
        }

        // draw it
        g.drawImage(cardImage, currentX, currentY, null);
    }

    public void setHasFolded(boolean hasFolded){
        this.hasFolded = hasFolded;
    }
}
