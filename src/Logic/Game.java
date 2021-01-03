package Logic;

import UI.Main;
import UI.Table;

import java.util.Arrays;
import java.util.Scanner;

public class Game {
    private static int dealerIndex;
    private static int smallBlindIndex;
    private static int bigBlindIndex;
    private static int currentActionIndex;

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
            dealFlop(board, deck);
            dealTurn(board, deck);
            dealRiver(board, deck);

            for (Player player : players) {
                player.drawHand();
                player.makeMadeHand(board);
            }
            printBoard(board);
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
            dealFlop(board, deck);
            dealTurn(board, deck);
            dealRiver(board, deck);

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
                printBoard(board);
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

    public static void pickRandomDealer(Player[] players) {
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

    public static void nextDealer(Player[] players) {
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

    public static void flop(Player[] players, Card[] board, Deck deck, boolean[] playersInHand, Table gameTable) {
        Game.setStartingActionIndex(players, playersInHand, 1);
        Game.dealFlop(board, deck);
        gameTable.setTableCards(board);
    }

    public static void turn(Player[] players, Card[] board, Deck deck, boolean[] playersInHand, Table gameTable) {
        Game.setStartingActionIndex(players, playersInHand, 2);
        Game.dealTurn(board, deck);
        gameTable.setTableCards(board);
    }

    public static void river(Player[] players, Card[] board, Deck deck, boolean[] playersInHand, Table gameTable) {
        Game.setStartingActionIndex(players, playersInHand, 3);
        Game.dealRiver(board, deck);
        gameTable.setTableCards(board);
    }

    public static void getWinners(Player[] players, Card[] board, boolean[] playersInHand, int pot) {
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

    public static boolean checkBettingRoundCompleted(Player[] players, int[] bets, boolean[] playersInHand, boolean[] playerHasActed) {
        //check if all but one has folded
        if (checkFolds(players, playersInHand)) {
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

    public static boolean checkFolds(Player[] players, boolean[] playersInHand) {
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

    public static boolean checkCheckAllowed(int[] bets) {
        if (bets[currentActionIndex] < getHighestBet(bets)) {
            return false;
        }

        return true;
    }

    public static boolean checkRaiseAllowed(Player[] players, int[] bets, int betSize) {
        int highestBet = getHighestBet(bets);
        int raiseSize = highestBet - bets[currentActionIndex] + betSize;
        if (players[currentActionIndex].getStack() - raiseSize < 0) {
            return false;
        }

        return true;
    }

    public static boolean checkCallAllowed(int[] bets) {
        if (bets[currentActionIndex] == getHighestBet(bets)) {
            return false;
        }

        return true;
    }

    public static boolean checkFoldAllowed(int[] bets) {
        if (bets[currentActionIndex] == getHighestBet(bets)) {
            return false;
        }

        return true;
    }


    public static int getHighestBet(int[] bets) {
        int highestBet = -1;
        for (int bet : bets) {
            if (bet > highestBet) {
                highestBet = bet;
            }
        }
        return highestBet;
    }

    public static void resetPlayerAttributes(Player[] players, int[] bets) {
        for(Player player : players) {
            player.resetMoneyInPot();
            player.resetFolded();
        }

        for(int i = 0; i < bets.length; i++) {
            bets[i] = 0;
        }
    }


    public static void setStartingActionIndex(Player[] players, boolean[] playersInHand, int street) {
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

    public static void updateCurrentAction(Player[] players, boolean[] playersInHand) {
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

    public static void printPlayers(Player[] players, int[] bets, boolean[] playersInHand) {
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
        System.out.println("POT: " + Main.getPot());
        System.out.println();
    }

    public static void printHands(Player[] players, boolean[] playersInHand) {
        for (int i = 0; i < players.length; i++) {
            if (playersInHand[i]) {
                players[i].printHand();
            }
        }
    }


    public static void dealHands(Player[] players) {
        for (Player player : players) {
            player.drawHand();
        }
    }

    public static void dealFlop(Card[] board, Deck deck) {
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

    public static void dealTurn(Card[] board, Deck deck) {
        board[3] = deck.drawCard();
    }

    private static void dealTurn(Card[] board, Deck deck, String value1, String suit1) {
        board[3] = deck.drawCard(value1, suit1);
    }

    public static void dealRiver(Card[] board, Deck deck) {
        board[4] = deck.drawCard();
    }

    private static void dealRiver(Card[] board, Deck deck, String value1, String suit1) {
        board[4] = deck.drawCard(value1, suit1);
    }

    public static void printBoard(Card[] board) {
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
            dealFlop(board, deck);
            dealTurn(board, deck);
            dealRiver(board, deck);

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
}

