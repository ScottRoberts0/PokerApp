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

    public void addPlayerToPot(Player player) {
        this.playersInPot.add(player);
    }

    public void removePlayerFromPot(Player player) {
        this.playersInPot.remove(player);
        this.bets[player.getPlayerNum()] = 0;
    }

    public void refundBets() {
        if(getNumPlayersInPot() == 2) {
            //refunds the difference between the two players to the higher player
            int difference = bets[playersInPot.get(0).getPlayerNum()] - bets[playersInPot.get(1).getPlayerNum()];
            if(bets[playersInPot.get(0).getPlayerNum()] > bets[playersInPot.get(1).getPlayerNum()]) {
                playersInPot.get(0).refundBet(difference, this);
            } else {
                playersInPot.get(1).refundBet(difference, this);
            }
        }
    }

    //TODO: check for multiple side pots in one street, IE one player has 25, another has 75, another has 100, another has 150, all go all in, requires 2 side pots
    //must be called after betting is complete
    public boolean checkSidePotRequirement() {
        if(getNumPlayersInPot() > 2) {
            for (Player player : playersInPot) {
                if (bets[player.getPlayerNum()] < getHighestBet()) {
                    return true;
                } else if(player.checkPlayerAllIn() && playersWithMoneyBehind() > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    //create side pots based on gaps in bets
    //TODO: sort playersinpot so that they correspond to their bets
    public ArrayList<Pot> createSidePots() {
        ArrayList<Pot> pots = new ArrayList<>();
        System.out.println(getNumPlayersInPot());
        //sort bets and players in ascending order, and so they remain one-to-one
        boolean sorted = false;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < bets.length - 1; i++) {
                if (bets[i + 1] < bets[i]) {
                    int temp = bets[i];
                    //Player temporary = playersInPot.get(i);

                    bets[i] = bets[i + 1];
                    //playersInPot.set(i, playersInPot.get(i + 1));

                    bets[i + 1] = temp;
                    //playersInPot.set(i + 1, temporary);

                    sorted = false;
                }
            }
        }
        printBets();

        for(int i = 0; i < bets.length - 1; i++) {
            if(bets[i] < bets[i + 1]) {
                Pot sidePot = new Pot(Game.getPots().size());
                for(int j = playersInPot.size(); j > i; j--) {
                    sidePot.addPlayerToPot(playersInPot.get(j));
                }
                pots.add(sidePot);
            }
        }

        return pots;
    }

    public int playersWithMoneyBehind() {
        int count = 0;
        for(Player player : playersInPot) {
            if(player.getStack() > 0) {
                count++;
            }
        }
        return count;
    }

    //must be called after betting is complete
    public boolean checkPlayerAllIn() {
        for(int i = 0; i < playersInPot.size(); i++) {
            if (playersInPot.get(i).getStack() == 0 && bets[playersInPot.get(i).getPlayerNum()] < getHighestBet()) return true;
            break;
        }
        return false;
    }

    //must be called after betting is complete
    public ArrayList<Player> findPlayersForSidePot() {
        ArrayList<Player> playersForSidePot = new ArrayList<>();
        for(int i = 0; i < playersInPot.size(); i++) {
            if(playersInPot.get(i).getStack() > 0) {
                playersForSidePot.add(playersInPot.get(i));
            }
        }
        return playersForSidePot;
    }

    public void testPrinter() {
        for(int i = 0; i < findPlayersForSidePot().size(); i++) {
            System.out.println(findPlayersForSidePot().get(i));
        }
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

    public int getLowestBet() {
        int lowestBet = 2147483646;
        for(int bet : bets) {
            if(bet < lowestBet && bet != 0) {
                lowestBet = bet;
            }
        }
        return lowestBet;
    }

    public void printPlayersInPot() {
        System.out.println("POT " + name + " PLAYERS IN POT: ");
        for(Player player : this.playersInPot) {
            System.out.println(player);
        }
        System.out.println();
    }

    public void printBets() {
        for(int bet : bets) {
            System.out.println(bet);
        }
    }

    public String toString() {
        if(name == 1) {
            return "MAIN POT: " + potValue + "§";
        } else {
            return "POT " + name + ": " + potValue + "§";
        }
    }
}
