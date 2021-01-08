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

    //create side pots based on gaps in bets, must be called after betting round is complete!
    //TODO: continue to debug and test
    public ArrayList<Pot> createSidePots() {
        ArrayList<Pot> pots = new ArrayList<>();
        ArrayList<Integer> betsSorted = new ArrayList<>();
        ArrayList<Player> playersSorted = new ArrayList<>();

        for(int bet : bets) {
            if(bet > 0) {
                betsSorted.add(bet);
            }
        }

        //sort bets and players in ascending order, and so they remain one-to-one
        boolean sorted = false;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < betsSorted.size() - 1; i++) {
                if (betsSorted.get(i + 1) < betsSorted.get(i)) {
                    int temp = betsSorted.get(i);
                    betsSorted.set(i, betsSorted.get(i + 1));
                    betsSorted.set(i + 1, temp);

                    sorted = false;
                }
            }
        }

        for(int bet : betsSorted) {
            for(int j = 0; j < playersInPot.size(); j++) {
                if(playersInPot.get(j).getMoneyInPot() == bet && !playersSorted.contains(playersInPot.get(j))) {
                    playersSorted.add(playersInPot.get(j));
                }
            }
        }



        //testing output:
        for(int bet : betsSorted) {
            System.out.println(bet);
        }
        System.out.println();
        for(Player player : playersInPot) {
            System.out.println(player);
        }
        System.out.println();



        int potCount = Game.getPots().size() + 1;
        for(int i = 0; i < betsSorted.size() - 1; i++) {
            //condition for createing a side pot: there is a player all in, or there is a difference in bet sizes after all bets have finished
            if(betsSorted.get(i) < betsSorted.get(i + 1) || playersSorted.get(i).getStack() == 0) {
                int difference = betsSorted.get(i + 1) - betsSorted.get(i);
                Pot sidePot = new Pot(potCount);
                potCount++;

                //add players to the side pot
                for(int j = i + 1; j < playersSorted.size(); j++) {
                    sidePot.addPlayerToPot(playersSorted.get(j));
                }

                //add money to the side pot, remove money from the main pot
                for(int j = 0; j < sidePot.playersInPot.size(); j++) {
                    if(bets[sidePot.playersInPot.get(j).getPlayerNum()] == betsSorted.get(i + 1)) {
                        Game.getCurrentPot().removeFromPot(difference);
                        sidePot.addToPot(difference);
                    }
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

    public String toString() {
        if(name == 1) {
            return "MAIN POT: " + potValue + "§";
        } else {
            return "POT " + name + ": " + potValue + "§";
        }
    }
}
