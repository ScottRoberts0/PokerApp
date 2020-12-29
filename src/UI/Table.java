package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import UI.Components.TableComponent;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Table {

    private JFrame mainFrame;
    private JPanel mainPanel;
    private TableComponent table;

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 800;

    private JButton checkButton, callButton, foldButton, raiseButton;

    private Dimension windowCenter;

    public Table() {
        createWindow();

        drawTable();

        drawButtons();

        showWindow();
    }

    private void drawTable(){
        Point p;

        // create the table
        table = new TableComponent();

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 5;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;

        // add components to the table to the window
        mainPanel.add(table, c);

    }

    private void drawButtons(){
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        foldButton = new JButton("Fold");
        //foldButton.setLocation(10, WINDOW_HEIGHT - foldButton.getHeight() - 10);
        foldButton.setLocation(100, 100);
        foldButton.setVisible(true);

        buttonPanel.add(foldButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;

        mainPanel.add(buttonPanel, c);
    }

    private void createWindow(){
        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mainFrame.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        mainFrame.add(mainPanel);
    }

    private void showWindow(){
        mainFrame.setVisible(true);//making the frame visible
    }
}
