package UI.Components;

import javax.swing.*;
import java.awt.*;

public class TableComponent extends JComponent {

    private final int TABLE_WIDTH = 650;
    private final int TABLE_HEIGHT = 350;

    public TableComponent(){
        this.setSize(TABLE_WIDTH, TABLE_HEIGHT);
    }

    public void paint(Graphics g) {
        Insets ins = getInsets();
        g.drawOval (ins.left, ins.top, this.getWidth() - ins.left - ins.right - 5,
                this.getHeight() - ins.top - ins.bottom - 5);
    }

    public Point getPlayerPosition(int playerNum){
        double angle = (Math.PI * 2 / 9) * (double)playerNum;

        double radius = (TABLE_WIDTH/2 * TABLE_HEIGHT/2) /
                Math.sqrt(
                        (Math.pow(TABLE_HEIGHT/2, 2) * Math.pow(Math.sin(angle), 2) + (Math.pow(TABLE_WIDTH/2, 2) * Math.pow(Math.cos(angle), 2)))
                );

        radius += 100;

        double x = Math.sin(angle) * radius;
        double y = Math.cos(angle) * radius;

        return new Point((int)x, (int)y);
    }
}
