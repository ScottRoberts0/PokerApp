package UI.Animation;

import java.awt.Graphics;

public abstract class Animatable {

    // the original position
    protected int originX;
    protected int originY;

    // the current position
    protected int currentX;
    protected int currentY;

    // the destination position
    protected int destinationX;
    protected int destinationY;

    protected boolean animating;

    // the total amount of time this object is to complete it's animation
    protected int totalDuration;

    // the current time in it's animation cycle, always 0-based so there doesn't need an initial time
    protected int currentTime;

    protected abstract void paint(Graphics g);

    protected void nextStep() {
        // add the frame time to the current time
        currentTime += AnimationThread.FRAME_TIME;
        // clamp it
        if(currentTime > totalDuration)
            currentTime = totalDuration;

        double stepPercent = (double)currentTime / (double)totalDuration;

        this.currentX = (int)((double)this.originX + (((double)this.destinationX - (double)this.originX) * stepPercent));
        this.currentY = (int)((double)this.originY + (((double)this.destinationY - (double)this.originY) * stepPercent));

        // check to see if the animation is complete
        if(currentTime == totalDuration){
            // stop animating
            this.animating = false;
            // reset the time
            this.currentTime = 0;

            // TODO: Create an animation complete callback using runnables.
            //       Make a class that implements runnable that takes an index as a constructor parameter
            //       Set the animating flag of that index to true
        }
    }

    /**
     * Move this object to some linear destination over a given time period
     *
     * @param toX Destination x
     * @param toY Destination y
     * @param duration Duration in milliseconds
     * @param animating Whether to start animation immedately or not
     */
    public void moveTo(int toX, int toY, int duration, boolean animating){
        this.animating = animating;

        this.originX = this.currentX;
        this.originY = this.currentY;

        this.destinationX = toX;
        this.destinationY = toY;

        this.totalDuration = duration;
    }

    public void setAnimating(boolean animating){
        this.animating = animating;
    }
}
