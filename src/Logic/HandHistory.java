package Logic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class HandHistory extends File {
    private final Date date;

    /**
     * Must specify file location and file name
     * @param fileName
     */
    public HandHistory(String fileName) {
        super(fileName);
        this.date = new Date();
    }

    /**
     * Enter a manual date or use a new date (new Date())
     * @param date
     */
    public HandHistory(Date date) {
        super("Histories/Hand History " + date.toString().replaceAll(":", " "));
        this.date = date;

        File histories = new File("Histories");
        if (!histories.exists()) {
            histories.mkdir();
        }
    }

    public void writeHandStart(ArrayList<Player> players) throws IOException {
        //create a copy of the file
        StringBuilder s = createCopy();

        //appends the new data to the copy
        s.append("======== Hand at: ").append(new Date()).append(" ========\n");
        for (Player player : players) {
            s.append(player.getPlayerName()).append(" - stack: ").append(player.getStack()).append(" - cards: ")
                    .append(player.getHand()[0].getShortName()).append(" ")
                    .append(player.getHand()[1].getShortName()).append("\n");
        }

        write(s);
    }

    public void writeAction(String action) throws IOException {
        //create a copy of the file
        StringBuilder s = createCopy();

        //append the newest action to the StringBuilder
        s.append(action);

        write(s);
    }

    public void write(StringBuilder s) throws IOException {
        PrintWriter output = new PrintWriter(this);
        //write the new file
        output.println(s);
        output.close();
    }

    //creates a copy of the current hand history file and returns it as a StringBuilder object
    private StringBuilder createCopy() throws IOException {
        Scanner input;
        StringBuilder s = new StringBuilder();

        //only happens if the file already exists
        if (this.exists()) {
            input = new Scanner(this);

            //copy the whole file
            while (input.hasNext()) {
                s.append(input.nextLine()).append("\n");
            }
        }

        return s;
    }

    public Date getDate() {
        return date;
    }
}