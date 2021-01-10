package UI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.ResourceBundle;

public class GraphicalHelpers {

    private static BufferedImage cardsImage = null;

    public static Point getCenter(Component component){
        Point p = new Point();

        p.x = component.getX() + (component.getWidth() / 2);
        p.y = component.getY() + (component.getHeight() / 2);

        return p;
    }

    /**
     * Given a desired center point, this calculates the top-left coordinates that Java uses.
     * @param component Component to be placed by a given center position
     * @param centerPoint Desired center position of the given component
     * @return The top-left referenced position of the given component
     */
    public static Point getTopLeftFromCenter(Component component, Point centerPoint){
        Point p = new Point();

        p.x = centerPoint.x - component.getWidth() / 2;
        p.y = centerPoint.y - component.getHeight() / 2;

        return p;
    }

    public static BufferedImage getCardsImage(){
        // if the image isn't loaded yet, bring it on in
        if(GraphicalHelpers.cardsImage == null) {
            try {
                File f = new File(System.getProperty("user.dir") + "\\resources\\card deck.png");
                GraphicalHelpers.cardsImage = ImageIO.read(f);
            }catch (Exception e){
                // catch some bad path or whateva
                System.out.println("FUICSDFS");
            }
        }

        return cardsImage;
    }

    public static Point addPoints(Point a, Point b){
        return new Point(a.x + b.x, a.y + b.y);
    }
}
