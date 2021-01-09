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

    public void addToPot(int bet) {
        this.potValue += bet;
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

    public ArrayList<Integer> sortBets() {
        ArrayList<Integer> sortedBets = new ArrayList<>();

        for(int bet : bets) {
            if(bet > 0) {
                sortedBets.add(bet);
            }
        }

        if(sortedBets.size() == 0) {
            sortedBets.add(0);
            sortedBets.add(0);
        }

        //sort bets and players in ascending order, and so they remain one-to-one
        boolean sorted = false;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < sortedBets.size() - 1; i++) {
                if (sortedBets.get(i + 1) < sortedBets.get(i)) {
                    int temp = sortedBets.get(i);
                    sortedBets.set(i, sortedBets.get(i + 1));
                    sortedBets.set(i + 1, temp);

                    sorted = false;
                }
            }
        }

        return sortedBets;
    }

    public ArrayList<Player> sortPlayers(ArrayList<Integer> sortedBets) {
        ArrayList<Player> sortedPlayers = new ArrayList<>();
        for(int bet : sortedBets) {
            for(int j = 0; j < playersInPot.size(); j++) {
                if(playersInPot.get(j).getMoneyInPot() == bet && !sortedPlayers.contains(playersInPot.get(j))) {
                    sortedPlayers.add(playersInPot.get(j));
                }
            }
        }
        return sortedPlayers;
    }

    public void refundBets() {
/*      if(getNumPlayersInPot() == 2) {
            //refunds the difference between the two players to the higher player
            int difference = bets[playersInPot.get(0).getPlayerNum()] - bets[playersInPot.get(1).getPlayerNum()];
            if(bets[playersInPot.get(0).getPlayerNum()] > bets[playersInPot.get(1).getPlayerNum()]) {
                playersInPot.get(0).refundBet(difference, this);
            } else {
                playersInPot.get(1).refundBet(difference, this);
            }
        }*/
        if (!Game.checkHandCompleted()) {
            ArrayList<Integer> sortedBets = sortBets();
            ArrayList<Player> sortedPlayers = sortPlayers(sortedBets);
            int difference = sortedBets.get(sortedBets.size() - 1) - sortedBets.get(sortedBets.size() - 2);
            sortedPlayers.get(sortedPlayers.size() - 1).refundBet(difference, Game.getCurrentPot());
            bets[sortedPlayers.get(sortedPlayers.size() - 1).getPlayerNum()] -= difference;
        }
    }

    //create side pots based on gaps in bets, must be called after betting round is complete!
    //TODO: continue to debug and test
    public ArrayList<Pot> createSidePots() {
        ArrayList<Pot> pots = new ArrayList<>();
        ArrayList<Integer> sortedBets = sortBets();
        ArrayList<Player> playersSorted = sortPlayers(sortedBets);

        int potCount = Game.getPots().size() + 1;
        for(int i = 0; i < sortedBets.size() - 1; i++) {
            //condition for createing a side pot: there is a player all in, or there is a difference in bet sizes after all bets have finished
            if(sortedBets.get(i) < sortedBets.get(i + 1) || playersSorted.get(i).getStack() == 0) {
                Pot sidePot = new Pot(potCount);
                potCount++;

                //add players to the side pot
                for(int j = i + 1; j < playersSorted.size(); j++) {
                    sidePot.addPlayerToPot(playersSorted.get(j));
                }

                //add money to the side pot, remove money from the main pot
                int difference = sortedBets.get(i + 1) - sortedBets.get(i);
                for(int j = 0; j < sidePot.playersInPot.size(); j++) {
                    if(bets[sidePot.playersInPot.get(j).getPlayerNum()] >= sortedBets.get(i + 1)) {
                        Game.getCurrentPot().removeFromPot(difference);
                        sidePot.addToPot(difference);
                    }
                }

                pots.add(sidePot);
            }
        }

        return pots;
    }

    //returns true if the player is involved in the pot
    public boolean containsPlayer(Player player) {
        return this.playersInPot.contains(player);
    }

    public int getNumPlayersInPot() {
        return this.playersInPot.size();
    }

    public int getNumPlayersAllIn() {
        int count = 0;
        for(Player player : playersInPot) {
            if(player.checkPlayerAllIn()) {
                count++;
            }
        }
        return count;
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
            return "POT " + name + ": " + potValue + "§";
        }
    }
}
