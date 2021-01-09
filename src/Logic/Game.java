package Logic;

import UI.Main;
import UI.MainWindow;

import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    private static int dealerIndex;
    private static int smallBlindIndex;
    private static int bigBlindIndex;
    private static int currentActionIndex;
    private static int street;

    private static Deck deck;
    private static Card[] board;
    private static Player[] players;
    private static Pot mainPot;
    private static Pot currentPot;

    //TODO: Figure out which one of these i'm actually using.
    private static ArrayList<Pot> pots;

    private static int lastRaiseSize;
    private static int startingStackSize;
    private static int sb;
    private static int bb;
    private static int minBet;

    public static int getStreet() {
        return street;
    }

    public static Deck getDeck() {
        return deck;
    }

    public static Card[] getBoard() {
        return board;
    }

    public static int getLastRaiseSize() {
        return lastRaiseSize;
    }

    public static int getStartingStackSize() {
        return startingStackSize;
    }

    public static int getNumPlayers() {
        return players.length;
    }

    public static int getSb() {
        return sb;
    }

    public static int getBb() {
        return bb;
    }

    public static int getMinBet() {
        return minBet;
    }

    public static ArrayList<Pot> getPots() {
        return pots;
    }

    public static Pot getCurrentPot() {
        return currentPot;
    }

    public static boolean checkSidePotPresent() {
        return false;
    }

    public static void nextStreet() {
        //TODO: i'd like to do something better with this method maybe

        currentPot.refundBets();
        currentPot.resetPlayerHasActed();

        //check if side pot is required, and create said side pot
        if(currentPot.getNumPlayersInPot() > 2) {
            ArrayList<Pot> newPots = currentPot.createSidePots();
            pots.addAll(newPots);
            currentPot = pots.get(pots.size() - 1);
        }

        for(Pot pot : pots) {
            pot.resetBets();
        }

        if(checkHandCompleted()) {
            runHand();
        } else {
            street++;
            if (street == 1) {
                flop(Main.getGameWindow());
            } else if (street == 2) {
                turn(Main.getGameWindow());
            } else if (street == 3) {
                river(Main.getGameWindow());
            } else if (street >= 4) {
                getWinners();
                endHand();
            }
        }
    }

    public static void startGame() {
        // init starting variables
        deck = new Deck();
        board = new Card[5];
        Arrays.fill(board, null);
        street = 0;
        sb = 500;
        bb = 1000;
        lastRaiseSize = bb;
        minBet = bb;
        startingStackSize = 100000;

        players = Game.createPlayers(6, deck, startingStackSize);
/*        players = new Player[6];
        players[0] = new Player(0, deck, startingStackSize, "Reid");
        players[1] = new Player(1, deck, 75000, "Tyler");
        players[2] = new Player(2, deck, 50000, "Dan");
        players[3] = new Player(3, deck, 125000, "Scott");
        players[4] = new Player(4, deck, 110000, "Pat");
        players[5] = new Player(5, deck, 0, "Denis");*/

        //init pot
        mainPot = new Pot(1);
        currentPot = mainPot;
        pots = new ArrayList<>();
        pots.add(mainPot);

        pickRandomDealer();

        dealHands();

        setStartingActionIndex();
        players[getSmallBlindIndex()].postBlind(sb, mainPot);
        players[getBigBlindIndex()].postBlind(bb, mainPot);

        printPlayersAndPot();
    }

    public static void endHand() {
        //if every has folded, award the pot to the last player in the hand
        if (checkFolds()) {
            currentPot.getPlayersInPot().get(0).win(currentPot.getPotValue());
        }

        resetHand();
    }

    public static boolean checkHandCompleted() {
        return currentPot.getNumPlayersInPot() - currentPot.getNumPlayersAllIn() == 1;
    }

    public static void runHand() {
        if(street == 0) {
            flop(Main.getGameWindow());
            turn(Main.getGameWindow());
            river(Main.getGameWindow());
            getWinners();
        } else if(street == 1) {
            turn(Main.getGameWindow());
            river(Main.getGameWindow());
            getWinners();
        } else if(street == 2) {
            river(Main.getGameWindow());
            getWinners();
        } else if(street == 3) {
            getWinners();
        }

        resetHand();
    }

    public static void resetHand() {
        printHands();
        printBoard();

        resetPots();
        street = 0;
        deck.shuffle();
        Arrays.fill(board, null);

        resetHands();
        dealHands();
        resetFolds();
        nextDealer();
        setStartingActionIndex();

        players[getSmallBlindIndex()].postBlind(sb, mainPot);
        players[getBigBlindIndex()].postBlind(bb, mainPot);
        lastRaiseSize = bb;

        Main.getGameWindow().updateButtons();
        Main.getGameWindow().getTable().deletePlayerCards();
        Main.getGameWindow().getTable().createPlayerCards(true);
    }

    public static void resetStacks() {
        for(Player player : players) {
            player.resetStack();
        }
    }

    public static void resetPots() {
        for(Player player : players) {
            player.resetMoneyInPot();
        }
        mainPot.resetPot();
        pots.clear();
        pots.add(mainPot);
        currentPot = mainPot;
    }

    /**
     * Tests hands over a given number of games.
     *
     * @param numLoops   Number of games to test over
     * @param numPlayers Number of players to test
     */
    public static void testHands(int numLoops, int numPlayers) {
        int a = 1;

        Deck deck = new Deck();
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i + 1, deck);
        }

        Card[] board = new Card[5];

        while (a < numLoops) {
            dealFlop();
            dealTurn();
            dealRiver();

            for (Player player : players) {
                player.drawHand();
                player.makeMadeHand(board);
            }

            printBoard();
            for (Player player : players) {
                player.printHand();
            }
            for (Player player : players) {
                player.printMadeHand();
            }

            ArrayList<Player> winners = Evaluator.findWinner(board, mainPot);
            int winnerCount = winners.size();


            if (winnerCount == 1) {
                System.out.println(winners.get(0).getPlayerName() + " wins with a " + winners.get(0).getMadeHandName() + "!");
            } else {
                System.out.println("Split pot between:");
                for (Player winner : winners) {
                    System.out.println(winner.getPlayerName());
                }
            }

            System.out.println("================================");
            System.out.println();

            deck.shuffle();
            a++;
        }
    }

    /**
     * Tests hands over a given number of games.
     *
     * @param handToTest String value, all caps, of what hand you want to be looking at. Eg. "PAIR" or "FOUR OF A KIND"
     * @param numLoops   Number of games to test over
     * @param numPlayers Number of players to test
     * @param ties       The number of ties you want to test. Eg. if you have 3 players and you want to look at cases where all three players
     *                   have the same hand, you will call testHands(handToTest, numLoops, 3, 3);
     */
    public static void testHands(String handToTest, int numLoops, int numPlayers, int ties) {
        int a = 1;

        if (ties > numPlayers) {
            ties = numPlayers;
        }

        Deck deck = new Deck();
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i + 1, deck);
        }

        Card[] board = new Card[5];

        while (a < numLoops) {
            dealFlop();
            dealTurn();
            dealRiver();

            for (Player player : players) {
                player.drawHand();
                player.makeMadeHand(board);
            }

            int handCount = 0;
            for (Player player : players) {
                if (player.getMadeHandName().equals(handToTest)) {
                    handCount++;
                }
            }

            boolean display = false;
            if (handCount >= ties) {
                display = true;
            }

            if (display == true) {
                printBoard();
                for (Player player : players) {
                    player.printHand();
                }
                for (Player player : players) {
                    player.printMadeHand();
                }

                ArrayList<Player> winners = Evaluator.findWinner(board, mainPot);
                int winnerCount = winners.size();

                if (winnerCount == 1) {
                    System.out.println(winners.get(0).getPlayerName() + " wins with a " + winners.get(0).getMadeHandName() + "!");
                } else {
                    System.out.println("Split pot between:");
                    for (int i = 0; i < winners.size(); i++) {
                        System.out.println(winners.get(i).getPlayerName());
                    }
                }

                System.out.println("================================");
                System.out.println();
            }

            deck.shuffle();
            a++;
        }
    }

    public static Player[] createPlayers(int numPlayers, Deck deck, int stackSize) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i, deck, stackSize);
        }
        return players;
    }

    public static void pickRandomDealer() {
        dealerIndex = (int) (Math.random() * players.length);

        setSmallBlindIndex();
        setBigBlindIndex();
    }

    public static void nextDealer() {
        dealerIndex++;
        if (dealerIndex > players.length - 1) {
            dealerIndex = 0;
        }

        setSmallBlindIndex();
        setBigBlindIndex();
    }

    public static void setSmallBlindIndex() {
        smallBlindIndex = dealerIndex + 1;
        if (smallBlindIndex > players.length - 1) {
            smallBlindIndex = 0;
        }

        while(players[smallBlindIndex].getStack() == 0) {
            smallBlindIndex++;
            if (smallBlindIndex > players.length - 1) {
                smallBlindIndex = 0;
            }
        }
    }

    public static void setBigBlindIndex() {
        bigBlindIndex = smallBlindIndex + 1;
        if (bigBlindIndex > players.length - 1) {
            bigBlindIndex = 0;
        }

        while(players[bigBlindIndex].getStack() == 0) {
            bigBlindIndex++;
            if (bigBlindIndex > players.length - 1) {
                bigBlindIndex = 0;
            }
        }
    }

    public static void resetHands() {
        for(Player player : players) {
            player.resetHand();
        }
    }

    public static void flop(MainWindow gameWindow) {
        System.out.println(">>>>>>>>>> FLOP <<<<<<<<<<<<<<<<");
        setStartingActionIndex();
        dealFlop();
        gameWindow.setTableCards(board);
    }

    public static void turn(MainWindow gameWindow) {
        System.out.println(">>>>>>>>>> TURN <<<<<<<<<<<<<<<<");
        setStartingActionIndex();
        dealTurn();
        gameWindow.setTableCards(board);
    }

    public static void river(MainWindow gameWindow) {
        System.out.println(">>>>>>>>>> RIVER <<<<<<<<<<<<<<<<");
        setStartingActionIndex();
        dealRiver();
        gameWindow.setTableCards(board);
    }

    public static void getWinners() {
        //legacy:
 /*       ArrayList<Player> winners = Evaluator.findWinner(board, mainPot);

        for (int i = 0; i < winners.size(); i++) {
            winners.get(i).win(mainPot.getPotValue() / winners.size());
        }*/
        for(Pot pot : pots) {
            ArrayList<Player> winners = Evaluator.findWinner(board, pot);
            for(int i = 0; i < winners.size(); i++) {
                winners.get(i).win(pot.getPotValue() / winners.size());
            }
        }
    }

    //TODO: move this method into the pot class
    public static boolean checkBettingRoundCompleted() {
        //check if all but one has folded
        if (checkFolds()) {
            return true;
        }

        //check that all players left in the hand match the highest bet OR they are all in
        int matchCount = 0;
        for(int i = 0; i < currentPot.getBets().length; i++) {
            if((currentPot.getBets()[i] == currentPot.getHighestBet() || players[i].getStack() == 0) && currentPot.containsPlayer(players[i])) {
                matchCount++;
            }
        }

        //check that all players left in the hand have acted
        int actedCount = 0;
        for(int i = 0; i < currentPot.getPlayerHasActed().length; i++) {
            if(currentPot.getPlayerHasActed()[i] && currentPot.containsPlayer(players[i])) {
                actedCount++;
            }
        }

        if(actedCount == currentPot.getNumPlayersInPot() && matchCount == currentPot.getNumPlayersInPot()) {
            return true;
        }

        return false;
    }

    public static boolean checkFolds() {
        //check if all but one has folded
        return mainPot.getNumPlayersInPot() == 1;
    }

    public static int getSmallBlindIndex() {
        return smallBlindIndex;
    }

    public static int getBigBlindIndex() {
        return bigBlindIndex;
    }

    //handles formatting bet sizes so that they conform to the rules
    public static int formatBetValue() {
        int betValue;

        try {
            betValue = Integer.parseInt(Main.getGameWindow().getRaiseText());
        } catch (NumberFormatException e) {
            if(street != 0 && getHighestBet() == 0) {
                betValue = minBet;
            } else {
                betValue = lastRaiseSize + getHighestBet();
            }
        }

        //if a player raises less than the min raise size, sets their bet value to the min raise size
        if(betValue < getHighestBet() + lastRaiseSize && getHighestBet() != 0) {
            betValue = getHighestBet() + lastRaiseSize;
        }

        //if a player's leading bet is less than the min bet, sets their bet value to the min bet
        if(checkBetsAllZero() && betValue < minBet) {
            betValue = minBet;
        }

        //puts a player all in if their bet is greater than the value of their stack
        if(players[currentActionIndex].getStack() - betValue < 0) {
            betValue = players[currentActionIndex].getStack();
        }

        return betValue;
    }

    public static boolean checkBetsAllZero() {
        int zeroCount = 0;
        for(int bet : currentPot.getBets()) {
            if (bet == 0) {
                zeroCount++;
            }
        }
        return zeroCount == currentPot.getBets().length;
    }

    public static boolean checkCheckAllowed() {
        if (currentPot.getBets()[currentActionIndex] < getHighestBet()) {
            return false;
        }

        return true;
    }

    public static boolean checkRaiseAllowed() {
        if (players[currentActionIndex].getStack() == 0) {
            return false;
        }

        return true;
    }

    public static boolean checkCallAllowed() {
        if (currentPot.getBets()[currentActionIndex] == getHighestBet()) {
            return false;
        } else if(players[currentActionIndex].getStack() == 0) {
            return false;
        }

        return true;
    }

    public static boolean checkFoldAllowed() {
        if (currentPot.getBets()[currentActionIndex] == getHighestBet()) {
            return false;
        }

        return true;
    }


    public static int getHighestBet() {
        int highestBet = -1;
        for (int bet : currentPot.getBets()) {
            if (bet > highestBet) {
                highestBet = bet;
            }
        }
        return highestBet;
    }

    public static void resetFolds() {
        for(Player player : players) {
            player.resetFolded();
        }
    }


    public static void setStartingActionIndex() {
        if (street == 0) {
            currentActionIndex = bigBlindIndex + 1;
            wrapCurrentActionIndex();
        } else {
            currentActionIndex = smallBlindIndex;
        }

        checkValidCurrentActionIndex();
    }

    public static void updateCurrentAction() {
        currentActionIndex++;
        wrapCurrentActionIndex();

        checkValidCurrentActionIndex();
    }

    public static void checkValidCurrentActionIndex() {
        if(street != 0) {
            //postflop, if the pot does not contain the player, or the player is all in, move the action forward until we find a valid player
            while (!currentPot.containsPlayer(players[currentActionIndex]) || players[currentActionIndex].checkHasHand()) {
                currentActionIndex++;
                wrapCurrentActionIndex();
            }
        } else {
            //preflop, if the pot does not contain the player, or the player is all in, move the action forward until we find a valid player
            while(players[currentActionIndex].checkHasHand()) {
                currentActionIndex++;
                wrapCurrentActionIndex();
            }
        }
    }

    public static void wrapCurrentActionIndex() {
        if (currentActionIndex > players.length - 1) {
            currentActionIndex = 0;
        }
    }

    public static int getCurrentActionIndex() {
        return currentActionIndex;
    }

    public static int getDealerIndex() {
        return dealerIndex;
    }

    public static void printPlayersAndPot() {
        for (int i = 0; i < players.length; i++) {
            String info = "";

            if (i == currentActionIndex) {
                info = " <o>";
            } else if (i == dealerIndex) {
                info = " >> D";
            } else if (i == smallBlindIndex) {
                info = " >> SB";
            } else if (i == bigBlindIndex) {
                info = " >> BB";
            }

            System.out.println(players[i].getPlayerName() + " " + currentPot.getBets()[i] + " " + players[i].getPlayerName() + " STACK: "
                    + players[i].getStack() + " " + info);
        }
        System.out.println();
        for(Pot pot : pots) {
            pot.printPlayersInPot();
            System.out.println(pot.getPotValue());
        }
        System.out.println();
    }

    public static void printHands() {
        for (int i = 0; i < players.length; i++) {
            if (mainPot.containsPlayer(players[i])) {
                players[i].printHand();
            }
        }
    }


    public static void dealHands() {
        for (Player player : players) {
            if(player.getStack() > 0) {
                player.drawHand();
            }
        }
    }

    public static void rebuy() {

    }

    public static void sitOut() {

    }

    public static void dealFlop() {
        for (int i = 0; i < 3; i++) {
            board[i] = deck.drawCard();
        }
    }

    private static void dealFlop(Card[] board, Deck deck, String value1, String suit1, String value2, String
            suit2, String value3, String suit3) {
        board[0] = deck.drawCard(value1, suit1);
        board[1] = deck.drawCard(value2, suit2);
        board[2] = deck.drawCard(value3, suit3);
    }

    public static void dealTurn() {
        board[3] = deck.drawCard();
    }

    private static void dealTurn(Card[] board, Deck deck, String value1, String suit1) {
        board[3] = deck.drawCard(value1, suit1);
    }

    public static void dealRiver() {
        board[4] = deck.drawCard();
    }

    private static void dealRiver(Card[] board, Deck deck, String value1, String suit1) {
        board[4] = deck.drawCard(value1, suit1);
    }

    public static void printBoard() {
        System.out.println("Board:");
        for (Card card : board) {
            if (card != null) {
                System.out.println(card);
            }
        }
        System.out.println();
    }


    public static void equityCalculator(Player player1, String value1, String suit1, String value2, String
            suit2, Player player2, Card[] board, Deck deck) {
        double player1Wins = 0;
        double player2Wins = 0;
        double ties = 0;
        for (int i = 0; i < 1000000; i++) {
            player1.drawHand(value1, suit1, value2, suit2);
            player2.drawHand();
            dealFlop();
            dealTurn();
            dealRiver();

            player1.makeMadeHand(board);
            player2.makeMadeHand(board);

            /*if(player1.compareHands(player2) == 1) {
                player1Wins++;
            } else if(player1.compareHands(player2) == -1) {
                player2Wins++;
            } else {
                ties++;
            }
*/
            deck.shuffle();
        }

        double p1Equity = player1Wins / (player1Wins + player2Wins + ties);
        double p2Equity = player2Wins / (player1Wins + player2Wins + ties);

        System.out.println("Player one wins: " + player1Wins);
        System.out.println("Plyaer two wins: " + player2Wins);
        System.out.println("Hands ending in a tie: " + ties);
        System.out.println();
        System.out.println("Player one equity: " + p1Equity + "%");
        System.out.println("Plyaer two equity: " + p2Equity + "%");
    }

    public static Player[] getPlayers() {
        if(players != null) {
            return Arrays.copyOf(players, players.length);
        }
        return null;
    }

    public static void setLastRaiseSize(int lastRaiseSize) {
        Game.lastRaiseSize = lastRaiseSize;
    }
}