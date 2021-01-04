package UI.Animation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationThread extends Thread {

    public static final int FRAME_TIME = 16;

    private static AnimationThread instance;

    private boolean startAnimationThread;
    private List<Animatable> animationObjects;

    private Component parentComponent;

    public AnimationThread(Component parentComponent){
        super();

        if(instance == null){
            instance = this;
        }

        this.parentComponent = parentComponent;

        animationObjects = new ArrayList<>();
    }

    public void addAnimatableObject(Animatable obj){
        animationObjects.add(obj);
    }

    public void removeAnimatableObject(Animatable obj){
        animationObjects.remove(obj);
    }

    public void removeAnimatableObjects(){
        animationObjects.clear();
    }

    public List<Animatable> getAnimationObjects(){
        return animationObjects;
    }

    @Override
    public void run() {
        super.run();

        while(this.isAlive()) {
            parentComponent.repaint();

            try {
                // pause between frames. 16ms = ~60fps
                this.sleep(FRAME_TIME);
            } catch (InterruptedException e) {
                System.out.println("Animation wait interrupted");
            }
        }
    }

    public void paint(Graphics g){
        // paint each animating component
        for(int i = 0; i < animationObjects.size(); i ++){
            animationObjects.get(i).paint(g);
        }
    }

    public static AnimationThread getInstance(){
        return instance;
    }
}
