import java.util.Scanner;

public class Game {
    private static int pot;
    private static int dealerIndex;
    private static int smallBlindIndex;
    private static int bigBlindIndex;
    private static int startingActionIndex;
    private static int currentActionIndex;
    private static int amountToCall;

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

            boolean[] winners = Evaluator.findWinner(players, board);
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

                boolean[] winners = Evaluator.findWinner(players, board);
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

    public static Player[] createPlayers(int numPlayers, Deck deck, int stackSize) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i + 1, deck, stackSize);
        }
        return players;
    }

    //debugging. put street logic into a method and call it from each street
    //check stack math
    //all in situations
    //not allowing stacks to go below 0
    
    public static void preFlop(Player[] players, int smallBlind, int bigBlind) {
        pot = 0;

        //post blinds and deal hands
        dealHands(players);
        System.out.println("Player " + players[smallBlindIndex].getPlayerNum() + " posts small blind");
        players[smallBlindIndex].bet(smallBlind);
        System.out.println("Player " + players[bigBlindIndex].getPlayerNum() + " posts big blind");
        players[bigBlindIndex].bet(bigBlind);

        setStartingActionIndex(players, 0);
        currentActionIndex = startingActionIndex;
        amountToCall = bigBlind;

        int[] bets = new int[players.length];
        for (int i = 0; i < bets.length; i++) {
            bets[i] = players[i].getLastVPIP();
        }

        bettingRound(players, bets, 0);
    }

    public static void flop(Player[] players, Card[] board, Deck deck) {
        setStartingActionIndex(players, 1);
        currentActionIndex = startingActionIndex;
        amountToCall = 0;

        dealFlop(board, deck);
        printBoard(board);

        int[] bets = new int[players.length];

        bettingRound(players, bets, 1);
    }

    public static void turn(Player[] players, Card[] board, Deck deck) {
        setStartingActionIndex(players, 2);
        currentActionIndex = startingActionIndex;
        amountToCall = 0;

        dealTurn(board, deck);
        printBoard(board);

        int[] bets = new int[players.length];
        bettingRound(players, bets, 2);
    }

    public static void river(Player[] players, Card[] board, Deck deck) {
        setStartingActionIndex(players, 3);
        currentActionIndex = startingActionIndex;
        amountToCall = 0;

        dealRiver(board, deck);
        printBoard(board);

        int[] bets = new int[players.length];
        bettingRound(players, bets, 3);

        boolean[] winners = Evaluator.findWinner(players, board);

        int winnerCount = 0;
        for (boolean winner : winners) {
            if (winner) {
                winnerCount++;
            }
        }

        for (int i = 0; i < players.length; i++) {
            if (winners[i]) {
                players[i].win(pot / winnerCount);
                System.out.println("Player " + players[i].getPlayerNum() + " wins " + (pot / winnerCount) + " with a " + players[i].getMadeHandName());
            }
        }
    }

    private static void bettingRound(Player[] players, int[] bets, int street) {
        for(Player player : players) {
            if(street != 0) {
                player.resetVPIP();
            }
        }

        do {
            if(!players[currentActionIndex].hasFolded()) {
                printPlayers(players);
                playerAction(players, bets);
                printHands(players);

                //TESTING READOUT BETS:
                System.out.println(pot);
                for (int bet : bets) {
                    System.out.println(bet);
                }
            }
            updateCurrentActionIndex(players);
        } while (!checkStreetCompleted(players, bets, street));
    }

    private static boolean checkStreetCompleted(Player[] players, int[] bets, int street) {
        int count = 0;
        for (Player player : players) {
            if (player.hasFolded()) {
                count++;
            }

            if (count == players.length - 1) {
                return true;
            }
        }

        int highestBet = -1;
        for (int bet : bets) {
            if (bet > highestBet && bet != 0) {
                highestBet = bet;
            }
        }

        int calledCount = 0;
        for (int i = 0; i < bets.length; i++) {
            if (!players[i].hasFolded() && bets[i] == highestBet) {
                calledCount++;
            }
        }

        if (calledCount == players.length - count && currentActionIndex != bigBlindIndex && street == 0) {
            return true;
        } else if (calledCount == players.length - count && street != 0) {
            return true;
        }

        if(street != 0 && currentActionIndex == startingActionIndex) {
            return true;
        }

        return false;
    }


    private static void playerAction(Player[] players, int[] bets) {
        Scanner input = new Scanner(System.in);

        char action = players[currentActionIndex].askAction(input.next().charAt(0));
        if (action == 'c') {
            bets[currentActionIndex] += amountToCall(bets);
            players[currentActionIndex].call();
        } else if (action == 'b') {
            System.out.print("Bet size: ");
            int betSize = input.nextInt();
            bets[currentActionIndex] = betSize;
            players[currentActionIndex].bet(betSize);
        } else if (action == 'x') {
            players[currentActionIndex].check();
        } else {
            players[currentActionIndex].fold();
        }
    }

    private static boolean checkValidIndex(Player[] players, int numToCheck) {
        return numToCheck <= players.length - 1;
    }

    public static void addToPot(int betSize) {
        pot += betSize;
        System.out.println("POT: " + pot);
    }

    private static int amountToCall(int[] bets) {
        int highestBet = 0;
        for (int bet : bets) {
            if (bet > highestBet) {
                highestBet = bet;
            }
        }
        amountToCall = highestBet - bets[currentActionIndex];
        return highestBet - bets[currentActionIndex];
    }

    public static int getAmountToCall() {
        return amountToCall;
    }



    public static void pickRandomDealer(Player[] players) {
        if (players.length == 2) {
            dealerIndex = (int) (Math.random() * 2);
            smallBlindIndex = dealerIndex;
            if (smallBlindIndex == 0) {
                bigBlindIndex = 1;
            } else if (smallBlindIndex == 1) {
                bigBlindIndex = 0;
            }
        } else {
            dealerIndex = (int) (Math.random() * (players.length - 1));
            smallBlindIndex = dealerIndex + 1;
            bigBlindIndex = dealerIndex + 2;

            if (dealerIndex + 1 > players.length - 1) {
                smallBlindIndex = 0;
                bigBlindIndex = 1;
            } else if (dealerIndex + 2 > players.length - 1) {
                bigBlindIndex = 0;
            }
        }
    }

    public static void nextDealer(Player[] players) {
        if (players.length == 2) {
            if (dealerIndex == 0) {
                dealerIndex = 1;
                smallBlindIndex = 1;
                bigBlindIndex = 0;
            } else {
                dealerIndex = 0;
                smallBlindIndex = 0;
                bigBlindIndex = 1;
            }
        } else {
            dealerIndex++;
            if (dealerIndex > players.length - 1) {
                dealerIndex = 0;
            }

            smallBlindIndex = dealerIndex + 1;
            bigBlindIndex = dealerIndex + 2;


            if (dealerIndex + 1 > players.length - 1) {
                smallBlindIndex = 0;
                bigBlindIndex = 1;
            } else if (dealerIndex + 2 > players.length - 1) {
                bigBlindIndex = 0;
            }
        }
    }



    private static void printPlayers(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            String output = "";
            if (i == currentActionIndex) {
                output = ">> CURRENT ACTION";
            } else if (i == dealerIndex) {
                output = ">> DEALER";
            } else if (i == smallBlindIndex) {
                output = ">> SMALL BLIND";
            } else if (i == bigBlindIndex) {
                output = ">> BIG BLIND";
            } else if (i == startingActionIndex) {
                output = ">> STARTING ACTION";
            }

            System.out.println("PLAYER " + players[i].getPlayerNum() + " STACK: " + players[i].getStack() + " " + output);
        }
        System.out.println();
    }

    private static void dealHands(Player[] players) {
        for (Player player : players) {
            player.drawHand();
            player.printHand();
        }
    }

    private static void printHands(Player[] players) {
        for(Player player : players) {
            if(!player.hasFolded()) {
                player.printHand();
            }
        }
    }



    /**
     * @param players
     * @param street  0: preflop, 1: flop, 2: turn, 3: river
     */
    private static void setStartingActionIndex(Player[] players, int street) {
        if (street < 0) {
            street = 0;
        } else if (street > 3) {
            street = 3;
        }

        if (street == 0) {
            startingActionIndex = bigBlindIndex + 1;
        } else {
            startingActionIndex = dealerIndex + 1;
        }

        if (startingActionIndex > players.length - 1) {
            startingActionIndex = 0;
        }
    }

    private static void updateCurrentActionIndex(Player[] players) {
        currentActionIndex++;
        if (!checkValidIndex(players, currentActionIndex)) {
            currentActionIndex = 0;
        }
    }



    private static void dealFlop(Card[] board, Deck deck) {
        for (int i = 0; i < 3; i++) {
            board[i] = deck.drawCard();
        }
    }

    private static void dealFlop(Card[] board, Deck deck, String value1, String suit1, String value2, String suit2, String value3, String suit3) {
        board[0] = deck.drawCard(value1, suit1);
        board[1] = deck.drawCard(value2, suit2);
        board[2] = deck.drawCard(value3, suit3);
    }

    private static void dealTurn(Card[] board, Deck deck) {
        board[3] = deck.drawCard();
    }

    private static void dealTurn(Card[] board, Deck deck, String value1, String suit1) {
        board[3] = deck.drawCard(value1, suit1);
    }

    private static void dealRiver(Card[] board, Deck deck) {
        board[4] = deck.drawCard();
    }

    private static void dealRiver(Card[] board, Deck deck, String value1, String suit1) {
        board[4] = deck.drawCard(value1, suit1);
    }

    private static void printBoard(Card[] board) {
        System.out.println("Board:");
        for (Card card : board) {
            if (card != null) {
                System.out.println(card);
            }
        }
        System.out.println();
    }


    public static void equityCalculator(Player player1, String value1, String suit1, String value2, String suit2, Player player2, Card[] board, Deck deck) {
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
