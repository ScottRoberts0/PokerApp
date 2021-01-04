package UI.Animation;

public class InSineEasingFunction implements EasingFunction{

    @Override
    public double getProgress(int currentTime, int totaltime) {
        double percent = (double)currentTime / (double)totaltime;
        return 1.0 - Math.sin((percent * Math.PI) / 2.0);
    }
}
