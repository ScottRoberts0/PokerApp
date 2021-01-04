package UI.Animation;

public class LinearEasingFunction implements EasingFunction{

    @Override
    public double getProgress(int currentTime, int totaltime) {
        return (double)currentTime / (double) totaltime;
    }
}
