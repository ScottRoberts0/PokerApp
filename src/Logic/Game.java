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

    private static int[] bets;
    private static boolean[] playerHasActed;
    private static boolean[] playersInHand;
    private static boolean[] playersSittingOut;
    private static boolean[] playersAllIn;
    private static int pot;
    private static ArrayList<Integer> pots;
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

    public static int[] getBets() {
        return bets;
    }

    public static boolean[] getPlayerHasActed() {
        return playerHasActed;
    }

    public static boolean[] getPlayersInHand() {
        return playersInHand;
    }

    public static boolean[] getPlayersSittingOut() {
        return playersSittingOut;
    }

    public static int getLastRaiseSize() {
        return lastRaiseSize;
    }

    public static int getStartingStackSize() {
        return startingStackSize;
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

    public static void addToPot(int betSize) {
        pot += betSize;
        pots.set(0, pots.get(0) + betSize);
    }

    public static int getPot(){
        return pot;
    }

    public static void nextStreet() {
        //i'd like to do something better with this method maybe
        Arrays.fill(playerHasActed, false);
        resetBets();
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

    public static void startGame() {
        // init starting variables
        deck = new Deck();
        board = new Card[5];
        street = 0;
        sb = 500;
        bb = 1000;
        pot = 0;
        pots = new ArrayList<Integer>();
        pots.add(0);
        startingStackSize = 100000;
        lastRaiseSize = bb;
        minBet = bb;

        //players = createPlayers(5, deck, startingStackSize);
        players = new Player[6];
        players[0] = new Player(0, deck, startingStackSize, "Reid");
        players[1] = new Player(1, deck, startingStackSize, "Tyler");
        players[2] = new Player(2, deck, startingStackSize);
        players[3] = new Player(3, deck, startingStackSize);
        players[4] = new Player(4, deck, startingStackSize, "Dan");
        players[5] = new Player(5, deck, startingStackSize, "Cody");

        // init arrays
        bets = new int[players.length];
        playerHasActed = new boolean[players.length];
        playersInHand = new boolean[players.length];
        playersSittingOut = new boolean[players.length];
        playersAllIn = new boolean[players.length];

        Arrays.fill(board, null);
        Arrays.fill(playersInHand, true);
        resetBets();

        pickRandomDealer();

        dealHands();

        setStartingActionIndex();
        players[getSmallBlindIndex()].postBlind(sb, bets);
        players[getBigBlindIndex()].postBlind(bb, bets);

        printPlayers();
    }

    public static void endHand() {
        if (checkFolds()) {
            for (int i = 0; i < playersInHand.length; i++) {
                if (playersInHand[i]) {
                    players[i].win(pot);
                    break;
                }
            }
        }

        resetHand();
    }

    public static void resetHand() {
        printHands();
        printBoard();

        pot = 0;
        pots.clear();
        pots.add(0);
        street = 0;
        deck.shuffle();

        Arrays.fill(playerHasActed, false);
        Arrays.fill(playersInHand, true);
        Arrays.fill(playersAllIn, false);
        Arrays.fill(board, null);

        resetFolds();
        resetBets();
        resetStacks();
        nextDealer();
        setStartingActionIndex();
        dealHands();

        players[getSmallBlindIndex()].postBlind(sb, bets);
        players[getBigBlindIndex()].postBlind(bb, bets);
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

    /**
     * Tests hands over a given number of games.
     *
     * @param numLoops   Number of games to test over
     * @param numPlayers Number of players to test
     */
    public static void testHands(int numLoops, int numPlayers, boolean[] playersInHand) {
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

            boolean[] winners = Evaluator.findWinner(players, board, playersInHand);
            int winnerCount = 0;

            for (boolean winner : winners) {
                if (winner) {
                    winnerCount++;
                }
            }

            if (winnerCount == 1) {
                for (int i = 0; i < winners.length; i++) {
                    if (winners[i]) {
                        System.out.println("Player " + players[i].getPlayerNum() + " wins with a " + players[i].getMadeHandName() + "!");
                    }
                }
            } else {
                System.out.println("Split pot between:");
                for (int i = 0; i < winners.length; i++) {
                    if (winners[i]) {
                        System.out.println("Player " + players[i].getPlayerNum());
                    }
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
    public static void testHands(String handToTest, int numLoops, int numPlayers, int ties, boolean[] playersInHand) {
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

                boolean[] winners = Evaluator.findWinner(players, board, playersInHand);
                int winnerCount = 0;

                for (boolean winner : winners) {
                    if (winner) {
                        winnerCount++;
                    }
                }

                if (winnerCount == 1) {
                    for (int i = 0; i < winners.length; i++) {
                        if (winners[i]) {
                            System.out.println("Player " + players[i].getPlayerNum() + " wins with a " + players[i].getMadeHandName() + "!");
                        }
                    }
                } else {
                    System.out.println("Split pot between:");
                    for (int i = 0; i < winners.length; i++) {
                        if (winners[i]) {
                            System.out.println("Player " + players[i].getPlayerNum());
                        }
                    }
                }

                System.out.println("================================");
                System.out.println();
            }

            deck.shuffle();
            a++;
        }
    }


    //debugging. put street logic into a method and call it from each street
    //check stack math
    //all in situations
    //not allowing stacks to go below 0
    //look at post flop set current action 3 players left
    public static Player[] createPlayers(int numPlayers, Deck deck, int stackSize) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i, deck, stackSize);
        }
        return players;
    }

    public static void pickRandomDealer() {
        dealerIndex = (int) (Math.random() * players.length);

        smallBlindIndex = dealerIndex + 1;
        if (smallBlindIndex > players.length - 1) {
            smallBlindIndex = 0;
        }

        bigBlindIndex = smallBlindIndex + 1;
        if (bigBlindIndex > players.length - 1) {
            bigBlindIndex = 0;
        }
    }

    public static void nextDealer() {
        dealerIndex++;
        if (dealerIndex > players.length - 1) {
            dealerIndex = 0;
        }

        smallBlindIndex = dealerIndex + 1;
        if (smallBlindIndex > players.length - 1) {
            smallBlindIndex = 0;
        }

        bigBlindIndex = smallBlindIndex + 1;
        if (bigBlindIndex > players.length - 1) {
            bigBlindIndex = 0;
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
        boolean[] winners = Evaluator.findWinner(players, board, playersInHand);

        int winnerCount = 0;
        for (boolean winner : winners) {
            if (winner) {
                winnerCount++;
            }
        }

        for (int i = 0; i < winners.length; i++) {
            if (winners[i]) {
                players[i].win(pot / winnerCount);
            }
        }
    }


    /*public static void hand(Player[] players, Card[] board, Deck deck, int sb, int bb) {
        pot = 0;
        Arrays.fill(board, null);

        boolean[] playersInHand = new boolean[players.length];
        Arrays.fill(playersInHand, true);

        boolean[] playerHasActed = new boolean[players.length];

        int[] bets = new int[players.length];

        preFlop(players, bets, playersInHand, playerHasActed, sb, bb);
        flop(players, board, deck, bets, playersInHand, playerHasActed);
        turn(players, board, deck, bets, playersInHand, playerHasActed);
        river(players, board, deck, bets, playersInHand, playerHasActed);
        nextDealer(players);
    }*/

    /*public static void preFlop(Player[] players, int[] bets, boolean[] playersInHand, boolean[] playerHasActed, int sb, int bb) {
        System.out.println(">>>>>>>>>>>>>> PREFLOP <<<<<<<<<<<<<");
        dealHands(players);
        printHands(players, playersInHand);

        //setStartingActionIndex(players, playersInHand, 0);

        players[smallBlindIndex].postBlind(sb, bets);
        players[bigBlindIndex].postBlind(bb, bets);

        printPlayers(players, bets, playersInHand);

        bettingRound(players, bets, playersInHand, playerHasActed);
    }

    public static void flop(Player[] players, Card[] board, Deck deck, int[] bets, boolean[] playersInHand, boolean[] playerHasActed) {
        Arrays.fill(bets, 0);
        Arrays.fill(playerHasActed, false);

        System.out.println(">>>>>>>>>>>>>> FLOP <<<<<<<<<<<<<<<");
        dealFlop(board, deck);
        printBoard(board);

        //setStartingActionIndex(players, playersInHand, 1);

        printPlayers(players, bets, playersInHand);

        bettingRound(players, bets, playersInHand, playerHasActed);
    }

    public static void turn(Player[] players, Card[] board, Deck deck, int[] bets, boolean[] playersInHand, boolean[] playerHasActed) {
        Arrays.fill(bets, 0);
        Arrays.fill(playerHasActed, false);

        System.out.println(">>>>>>>>>>>>>> TURN <<<<<<<<<<<<<<<<");
        dealTurn(board, deck);
        printBoard(board);

        //setStartingActionIndex(players, playersInHand, 2);

        printPlayers(players, bets, playersInHand);

        bettingRound(players, bets, playersInHand, playerHasActed);
    }

    public static void river(Player[] players, Card[] board, Deck deck, int[] bets, boolean[] playersInHand, boolean[] playerHasActed) {
        Arrays.fill(bets, 0);
        Arrays.fill(playerHasActed, false);

        System.out.println(">>>>>>>>>>>>>> RIVER <<<<<<<<<<<<<<<");
        dealRiver(board, deck);
        printBoard(board);

        //setStartingActionIndex(players, playersInHand, 3);

        printPlayers(players, bets, playersInHand);

        bettingRound(players, bets, playersInHand, playerHasActed);
    }

    public static void bettingRound(Player[] players, int[] bets, boolean[] playersInHand, boolean[] playerHasActed) {
        while (!checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
            playerAction(players, bets, playersInHand, playerHasActed);
            printHands(players, playersInHand);
            updateCurrentAction(players, playersInHand);
            printPlayers(players, bets, playersInHand);
        }
    }*/

    public static boolean checkBettingRoundCompleted() {
        //check if all but one has folded
        if (checkFolds()) {
            return true;
        }

        int foldCount = 0;
        for (boolean player : playersInHand) {
            if (!player) {
                foldCount++;
            }
        }

        //check that all players left in the hand match the highest bet
        int highestBet = -1;
        for (int bet : bets) {
            if (bet > highestBet) highestBet = bet;
        }
        int matchCount = 0;
        for (int i = 0; i < bets.length; i++) {
            if (playersInHand[i] && bets[i] == highestBet) {
                matchCount++;
            }
        }

        //check that all players have acted
        int actedCount = 0;
        for (boolean player : playerHasActed) {
            if (player) {
                actedCount++;
            }
        }

        if (actedCount >= players.length - foldCount && matchCount == players.length - foldCount) {
            return true;
        }

        return false;
    }

    public static int getNumPlayersInHand() {
        int count = 0;
        for(boolean player : playersInHand) {
            if(player) {
                count++;
            }
        }
        return count;
    }

    public static boolean checkHandFinished() {
        int[] playerStacks = new int[players.length];
        for(int i = 0; i < playerStacks.length; i++) {
            playerStacks[i] = players[i].getStack();
        }

        int emptyStackCount = 0;
        for(int i = 0; i < playerStacks.length; i++) {
            if(playerStacks[i] == 0 && playersInHand[i]) {
                emptyStackCount++;
            }

            if(emptyStackCount == getNumPlayersInHand()) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkFolds() {
        //check if all but one has folded
        int foldCount = 0;
        for (boolean player : playersInHand) {
            if (!player) {
                foldCount++;
            }
        }
        if (foldCount == players.length - 1) {
            return true;
        }
        return false;
    }

    public static int getSmallBlindIndex() {
        return smallBlindIndex;
    }

    public static int getBigBlindIndex() {
        return bigBlindIndex;
    }

    public static boolean[] getPlayersAllIn() {
        return Arrays.copyOf(playersAllIn, playersAllIn.length);
    }

    public static int getBetValue() {
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

        if(betValue < getHighestBet() + lastRaiseSize && getHighestBet() != 0) {
            betValue = getHighestBet() + lastRaiseSize;
        }

        if(players[currentActionIndex].getStack() - betValue < 0) {
            betValue = players[currentActionIndex].getStack();
        }

        return betValue;
    }

    public static boolean checkCheckAllowed() {
        if (bets[currentActionIndex] < getHighestBet()) {
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
        if (bets[currentActionIndex] == getHighestBet()) {
            return false;
        } else if(players[currentActionIndex].getStack() == 0) {
            return false;
        }

        return true;
    }

    public static boolean checkFoldAllowed() {
        if (bets[currentActionIndex] == getHighestBet()) {
            return false;
        }

        return true;
    }


    public static int getHighestBet() {
        int highestBet = -1;
        for (int bet : bets) {
            if (bet > highestBet) {
                highestBet = bet;
            }
        }
        return highestBet;
    }

    public static void resetBets() {
        for(Player player : players) {
            player.resetMoneyInPot();
        }

        for(int i = 0; i < bets.length; i++) {
            bets[i] = 0;
        }
    }

    public static void resetFolds() {
        for(Player player : players) {
            player.resetFolded();
        }
    }


    public static void setStartingActionIndex() {
        if (street == 0) {
            currentActionIndex = bigBlindIndex + 1;
            if (currentActionIndex > players.length - 1) {
                currentActionIndex = 0;
            }
        } else {
            currentActionIndex = smallBlindIndex;
        }

        while (!playersInHand[currentActionIndex]) {
            currentActionIndex++;
            if (currentActionIndex > players.length - 1) {
                currentActionIndex = 0;
            }
        }
    }

    public static void updateCurrentAction() {
        currentActionIndex++;
        if (currentActionIndex > players.length - 1) {
            currentActionIndex = 0;
        }

        while (!playersInHand[currentActionIndex]) {
            currentActionIndex++;
            if (currentActionIndex > players.length - 1) {
                currentActionIndex = 0;
            }
        }
    }

    public static int getCurrentActionIndex() {
        return currentActionIndex;
    }

    public static int getDealerIndex() {
        return dealerIndex;
    }

    public static void printPlayers() {
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

            System.out.println(playersInHand[i] + " " + bets[i] + " " + players[i].getPlayerName() + " STACK: "
                    + players[i].getStack() + " " + info);
        }
        System.out.println();
        System.out.println("POT: " + pot);
        System.out.println("POT (LIST): " + pots.get(0));
        System.out.println();
    }

    public static void printHands() {
        for (int i = 0; i < players.length; i++) {
            if (playersInHand[i]) {
                players[i].printHand();
            }
        }
    }


    public static void dealHands() {
        for (Player player : players) {
            player.drawHand();
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
        return players;
    }

    public static void setLastRaiseSize(int lastRaiseSize) {
        Game.lastRaiseSize = lastRaiseSize;
    }
}