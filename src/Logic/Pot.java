package Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Pot {
    private int potValue;
    private final int potNum;
    private ArrayList<Player> playersInPot;
    private int[] bets;
    private boolean[] playerHasActed;

    /**
     * @param potNum Integer value corresponding to the side pot number. Should start at 2 as main pot will always be pot name 1.
     */
    public Pot(int potNum) {
        this(potNum, 0);
    }

    public Pot(int potNum, int potValue){
        this.potNum = potNum;
        this.potValue = potValue;
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

    public void addPlayerToPot(Player player) {
        this.playersInPot.add(player);
    }

    public void removePlayerFromPot(Player player) {
        this.playersInPot.remove(player);
        this.bets[player.getPlayerNum()] = 0;
    }

    public void refundBets() {
        //refunds players their bets if they have overbet everyone on left in the hand... note that this should only ever happen to one player, the player
        //with the most money on the table
        if (!Game.checkHandCompleted()) {
            ArrayList<Integer> sortedBets = sortBets();
            ArrayList<Player> sortedPlayers = sortPlayersByBets();
            int difference = sortedBets.get(sortedBets.size() - 1) - sortedBets.get(sortedBets.size() - 2);

            sortedPlayers.get(sortedPlayers.size() - 1).refundBet(difference, Game.getCurrentPot());
            bets[sortedPlayers.get(sortedPlayers.size() - 1).getPlayerNum()] -= difference;
        }
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

        //sort bets in ascending order
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

    public ArrayList<Player> sortPlayersByStack() {
        //sorts players in ascending order according to the size of their remaining stacks
        ArrayList<Player> sortedPlayers = new ArrayList<>();

        sortedPlayers.addAll(playersInPot);

        Collections.sort(sortedPlayers);

        return sortedPlayers;
    }

    public ArrayList<Player> sortPlayersByBets() {
        //sorts players in ascending order according to the size of their current bet
        ArrayList<Player> sortedPlayers = new ArrayList<>();
        ArrayList<Integer> sortedBets = sortBets();

        for(int i = 0; i < sortedBets.size(); i++) {
            for(int j = 0; j < playersInPot.size(); j++) {
                if(playersInPot.get(j).getCurrentBet() == sortedBets.get(i)) {
                    sortedPlayers.add(playersInPot.get(j));
                }
            }
        }
        return sortedPlayers;
    }

    //TODO: continue to debug and test
    public ArrayList<Pot> createSidePots() {
        //create side pots based on gaps in bets, must be called after betting round is complete!
        //creates a list of pots and returns this, which is then appended to the list of pots in the Game class
        ArrayList<Pot> pots = new ArrayList<>();
        ArrayList<Integer> sortedBets = sortBets();
        ArrayList<Player> sortedPlayers = sortPlayersByStack();


        for(int bet : sortedBets) {
            System.out.println(bet);
        }

        for(Player player : sortedPlayers) {
            System.out.println(player);
        }

        int potCount = Game.getPots().size() + 1;
        for(int i = 0; i < sortedBets.size() - 1; i++) {
            //condition for creating a side pot: there is a player all in, or there is a difference in bet sizes after all bets have finished
            //last statement checks to make sure we aren't creating extra side pots by comparing and ensuring the bets are not equal
            if(sortedBets.get(i) < sortedBets.get(i + 1) || sortedPlayers.get(i).getStack() == 0) {
                Pot sidePot = new Pot(potCount);
                potCount++;

                //add players to the side pot
                for(int j = i + 1; j < sortedPlayers.size(); j++) {
                    if(bets[sortedPlayers.get(j).getPlayerNum()] >= sortedBets.get(i + 1)) {
                        sidePot.addPlayerToPot(sortedPlayers.get(j));
                    }
                }

                //add money to the side pot, remove money from the main pot
                int difference = sortedBets.get(i + 1) - sortedBets.get(i);
                for(int j = 0; j < sidePot.playersInPot.size(); j++) {
                    if(bets[sidePot.playersInPot.get(j).getPlayerNum()] >= sortedBets.get(i + 1)) {
                        Game.getCurrentPot().removeFromPot(difference);
                        sidePot.addToPot(difference);
                    }
                }

                if(sidePot.getNumPlayersInPot() > 1) {
                    pots.add(sidePot);
                }
            }
        }

        return pots;
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



    public boolean containsPlayer(Player player) {
        return this.playersInPot.contains(player);
    }

    public int getPotValue() {
        return this.potValue;
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
        System.out.println("POT " + potNum + " (VALUE: " + this.potValue + ")" + " PLAYERS IN POT: ");
        for(Player player : this.playersInPot) {
            System.out.println(player);
        }
        System.out.println();
    }

    public String toString() {
        if(potNum == 0) {
            return "MAIN POT: " + potValue + "§";
        } else {
            return "POT " + potNum + ": " + potValue + "§";
        }
    }
}
