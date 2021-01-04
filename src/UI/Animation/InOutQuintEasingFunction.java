package UI.Animation;

public class InOutQuintEasingFunction implements EasingFunction{

    @Override
    public double getProgress(int currentTime, int totaltime) {
        double x = (double)currentTime / (double)totaltime;
        return x < 0.5 ? 16.0 * x * x * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 5.0) / 2.0;
    }
}
