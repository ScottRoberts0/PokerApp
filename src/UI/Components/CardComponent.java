package UI.Components;

import Logic.Card;
import UI.Animation.Animatable;
import UI.Animation.AnimationThread;

import java.awt.*;

public class CardComponent extends Animatable {

    private Card[] cards;

    public CardComponent(int x, int y, Card[] cards){
        this.currentX = x;
        this.currentY = y;

        this.cards = cards;

        this.currentTime = 0;
        this.totalDuration = 0;

        this.animating = false;
    }

    @Override
    protected void paint(Graphics g) {
        // only re-calculate positions on objects that are moving
        if(this.animating){
            nextStep();
        }

        // paint the object
        g.fillOval(currentX, currentY, 50, 50);
    }
}
