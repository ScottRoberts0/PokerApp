package Logic;

import java.util.ArrayList;

public class Pot {
    private int value;
    private int name;
    private ArrayList<Player> playersInPot;

    public Pot(int name) {
        this.name = name;
        this.value = 0;
    }

    public void addToPot(int bet) {
        this.value += bet;
    }

    public void resetPot() {
        value = 0;
    }

    public int getPotValue() {
        return value;
    }

    public void addPlayerToPot(Player player) {
        playersInPot.add(player);
    }

    public ArrayList<Player> getPlayersInPot() {
        ArrayList<Player> copy = playersInPot;
        return copy;
    }

    public String toString() {
        if(name == 1) {
            return "MAIN POT: " + value + "§";
        } else {
            return "SIDE POT " + name + ": " + value + "§";
        }
    }
}
