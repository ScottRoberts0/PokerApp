package Logic;

import java.util.ArrayList;
import java.util.Arrays;

public class Pot {
    private int potValue;
    private final int name;
    private ArrayList<Player> playersInPot;
    private int[] bets;
    private boolean[] playerHasActed;

    /**
     * @param name Integer value corresponding to the side pot number. Should start at 2 as main pot will always be pot name 1.
     */
    public Pot(int name) {
        this.name = name;
        this.potValue = 0;
        this.playersInPot = new ArrayList<>();
        this.bets = new int[Game.getNumPlayers()];
        this.playerHasActed = new boolean[Game.getNumPlayers()];
        resetBets();
    }

    public void addToPot(int bet, int playerNum) {
        this.potValue += bet;
        this.bets[playerNum] += bet;
    }

    public void removeFromPot(int bet) {
        this.potValue -= bet;
    }

    public void resetPot() {
        this.potValue = 0;
        this.playersInPot.clear();
        resetPlayerHasActed();
        resetBets();
    }

    public void resetBets() {
        Arrays.fill(bets, 0);
        for(Player player : playersInPot) {
            player.resetMoneyInPot();
        }
    }

    public void resetPlayerHasActed() {
        Arrays.fill(playerHasActed, false);
    }

    public int getPotValue() {
        return this.potValue;
    }

    public void addPlayerToPot(Player player, int bet) {
        this.playersInPot.add(player);
    }

    public void removePlayerFromPot(Player player) {
        this.playersInPot.remove(player);
        this.bets[player.getPlayerNum()] = 0;
    }

    //returns true if the player is involved in the pot
    public boolean containsPlayer(Player player) {
        return this.playersInPot.contains(player);
    }

    public int getNumPlayersInPot() {
        return this.playersInPot.size();
    }

    public void setPlayerActed(int playerNum, boolean value) {
        playerHasActed[playerNum] = value;
    }

    public ArrayList<Player> getPlayersInPot() {
        ArrayList<Player> copy = this.playersInPot;
        return copy;
    }

    public int[] getBets() {
        return Arrays.copyOf(this.bets, this.bets.length);
    }

    public boolean[] getPlayerHasActed() {
        return Arrays.copyOf(this.playerHasActed, playerHasActed.length);
    }

    public int getHighestBet() {
        int highestBet = -1;
        for(int bet : bets) {
            if(bet > highestBet) {
                highestBet = bet;
            }
        }
        return highestBet;
    }

    public void printPlayersInPot() {
        System.out.println("POT " + name + " PLAYERS IN POT: ");
        for(Player player : this.playersInPot) {
            System.out.println(player);
        }
        System.out.println();
    }

    public String toString() {
        if(name == 1) {
            return "MAIN POT: " + potValue + "§";
        } else {
            return "SIDE POT " + name + ": " + potValue + "§";
        }
    }
}
