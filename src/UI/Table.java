package UI;

import javax.swing.*;

import UI.Components.CardComponent;
import UI.Components.TableComponent;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Table {

    private JFrame mainFrame;
    private TableComponent table;

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 800;

    CardComponent[][] playerCards;

    private Dimension windowCenter;

    public Table() {
        createWindow();

        drawTable();

        Image im = GraphicalHelpers.getCardsImage();

        showWindow();
    }

    private void drawTable(){
        // create the table
        table = new TableComponent();

        // center it in the window
        table.setLocation(GraphicalHelpers.getTopLeftFromCenter(table, GraphicalHelpers.getCenter(mainFrame)));

        Point p;

        playerCards = new CardComponent[9][9];
        // Create some cards
        for(int i = 0; i < 9; i++){
            playerCards[0][i] = new CardComponent(0, 15);
            p = GraphicalHelpers.getTopLeftFromCenter(playerCards[0][i], GraphicalHelpers.addPoints(GraphicalHelpers.getCenter(table), table.getPlayerPosition(i)));
            playerCards[0][i].setLocation(p);
        }


        // add components to the table to the window
        mainFrame.add(table);
        for(int i = 0; i < 9; i++){
            mainFrame.add(playerCards[0][i]);
        }
    }

    private void createWindow(){
        mainFrame = new JFrame();//creating instance of JFrame
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLayout(null);

        mainFrame.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);//400 width and 500 height
    }

    private void showWindow(){
        mainFrame.setVisible(true);//making the frame visible
    }
}
