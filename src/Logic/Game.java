package Logic;

import Networking.Networker;
import UI.Main;
import UI.MainWindow;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Game {
    private static HandHistory handHistory;
    private final static boolean recordHandHistory = true;

    private static int dealerIndex;
    private static int smallBlindIndex;
    private static int bigBlindIndex;
    private static int currentActionIndex;
    private static int street;
    private static double rebuyThreshold;

    private static ArrayList<Player> players;
    private static ArrayList<Pot> pots;

    private static Deck deck;
    private static Card[] board;

    private static Pot mainPot;
    private static Pot currentPot;

    private static int lastRaiseSize;
    private static int startingStackSize;
    private static int sb;
    private static int bb;
    private static int minBet;

    private static int localPlayerNum = 0;

    public static int getLocalPlayerNum() {
        return localPlayerNum;
    }

    public static void setLocalPlayerNum(int localPlayerNum) {
        Game.localPlayerNum = localPlayerNum;
    }

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
        return players.size();
    }

    public static void addPlayer(Player player){
        if(players == null){
            players = new ArrayList<Player>();
        }

        players.add(player);
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

        updateStartingStackValues();

        //TODO: once satisified with side pots, enable the commented out lines below which will just finish hands automatically
        if(checkHandCompleted()) {
            runHand();
        } else {
            street++;
            setStartingActionIndex();
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
        rebuyThreshold = 0.25;

        if(Networker.getInstance() == null) {
            addPlayer(new Player(0, startingStackSize, "Reid"));
            addPlayer(new Player(1, 75000, "Tyler"));
            addPlayer(new Player(2, 50000, "Dan"));
            addPlayer(new Player(3, 125000, "Scott"));
            addPlayer(new Player(4, 110000, "Pat"));
            addPlayer(new Player(5, 12000, "Denis"));
        }else{
            ArrayList<String> playerNames = Networker.getInstance().getPlayersInLobby();
            for(int i = 0; i < playerNames.size(); i ++){
                addPlayer(new Player(i, 50000, playerNames.get(i)));
            }
        }

/*        if(Networker.getInstance() == null) {
            addPlayer(new Player(0, 50000, "Reid"));
            addPlayer(new Player(1, 50000, "Tyler"));
            addPlayer(new Player(2, startingStackSize, "Dan"));
            addPlayer(new Player(3, startingStackSize, "Scott"));
            addPlayer(new Player(4, startingStackSize, "Pat"));
            addPlayer(new Player(5, startingStackSize, "Denis"));
        }*/

        //init pot
        mainPot = new Pot(1);
        currentPot = mainPot;
        pots = new ArrayList<>();
        pots.add(mainPot);

        dealHands();

        pickRandomDealer();

        //initialize the hand history
        try {
            if(recordHandHistory) {
                handHistory = new HandHistory(new Date());
                handHistory.writeHandStart(players);
            }
        } catch(IOException e) {
            System.out.println(e + e.getMessage() + "\nHand histories not recorded for this session");
        }

        setStartingActionIndex();
        players.get(smallBlindIndex).postBlind(sb, mainPot);
        players.get(bigBlindIndex).postBlind(bb, mainPot);

        printPlayersAndPot();


        Main.getGameWindow().getTable().createPlayerCards(true);

        Main.getGameWindow().setIsGameStarted(true);
    }

    public static void endHand() {
        //if every has folded, award the pot to the last player in the hand
        if (checkFolds()) {
            currentPot.getPlayersInPot().get(0).win(currentPot.getPotValue());
        }

        resetHand();
    }

    public static boolean checkHandCompleted() {
        //checks to see if a hand should be completed based on the number of players with money left in their stack (0 or 1)
        if(currentPot.getNumPlayersInPot() - currentPot.getNumPlayersAllIn() <= 1) {
            return true;
        }
        return false;
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
        Main.getGameWindow().getTable().resetTableCardsAnimated();

        updateStartingStackValues();
        resetHands();
        dealHands();
        resetFolds();
        nextDealer();
        setStartingActionIndex();

        try {
            if(recordHandHistory) {
                handHistory.writeHandStart(currentPot.getPlayersInPot());
            }
        } catch(IOException e) {
            System.out.println(e + e.getMessage() + "\nHand histories not recorded for this session");
        }

        players.get(getSmallBlindIndex()).postBlind(sb, mainPot);
        players.get(getBigBlindIndex()).postBlind(bb, mainPot);
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

    /*
     * Tests hands over a given number of games.
     *
     * @param numLoops   Number of games to test over
     * @param numPlayers Number of players to test
     */
    /*public static void testHands(int numLoops, int numPlayers) {
        int a = 1;

        Deck deck = new Deck();
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < players.size(); i++) {
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
    }*/

    /*
     * Tests hands over a given number of games.
     *
     * @param handToTest String value, all caps, of what hand you want to be looking at. Eg. "PAIR" or "FOUR OF A KIND"
     * @param numLoops   Number of games to test over
     * @param numPlayers Number of players to test
     * @param ties       The number of ties you want to test. Eg. if you have 3 players and you want to look at cases where all three players
     *                   have the same hand, you will call testHands(handToTest, numLoops, 3, 3);
     *
    public static void testHands(String handToTest, int numLoops, int numPlayers, int ties) {
        int a = 1;

        if (ties > numPlayers) {
            ties = numPlayers;
        }

        Deck deck = new Deck();
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < players.size(); i++) {
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
    }*/

    public static ArrayList<Player> createPlayersList(int numPlayers, int stackSize) {
        ArrayList<Player> players = new ArrayList<>();
        for(int i = 0; i < numPlayers; i++) {
            players.add(new Player(i, stackSize));
        }
        return players;
    }

    public static ArrayList<Player> createPlayersList(Player[] inputPlayers) {
        ArrayList<Player> players = new ArrayList<>();
        players.addAll(Arrays.asList(inputPlayers));
        return players;
    }

    public static void pickRandomDealer() {
        dealerIndex = (int) (Math.random() * players.size());

        setSmallBlindIndex();
        setBigBlindIndex();
    }

    public static void nextDealer() {
        dealerIndex++;
        wrapDealerIndex();

        while(players.get(dealerIndex).getStack() == 0) {
            dealerIndex++;
            wrapDealerIndex();
        }

        System.out.println("Dealer index: " + dealerIndex);

        setSmallBlindIndex();
        setBigBlindIndex();
    }

    public static void wrapDealerIndex() {
        if (dealerIndex > players.size() - 1) {
            dealerIndex = 0;
        }
    }

    public static void setSmallBlindIndex() {
        smallBlindIndex = dealerIndex + 1;
        if (smallBlindIndex > players.size() - 1) {
            smallBlindIndex = 0;
        }

        while(players.get(smallBlindIndex).getStack() == 0) {
            smallBlindIndex++;
            if (smallBlindIndex > players.size() - 1) {
                smallBlindIndex = 0;
            }
        }
    }

    public static void setBigBlindIndex() {
        bigBlindIndex = smallBlindIndex + 1;
        if (bigBlindIndex > players.size() - 1) {
            bigBlindIndex = 0;
        }

        while(players.get(bigBlindIndex).getStack() == 0) {
            bigBlindIndex++;
            if (bigBlindIndex > players.size() - 1) {
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
        dealFlop();
        gameWindow.setTableCards(board);
        printBoard();
    }

    public static void turn(MainWindow gameWindow) {
        System.out.println(">>>>>>>>>> TURN <<<<<<<<<<<<<<<<");
        dealTurn();
        gameWindow.setTableCards(board);
        printBoard();
    }

    public static void river(MainWindow gameWindow) {
        System.out.println(">>>>>>>>>> RIVER <<<<<<<<<<<<<<<<");
        dealRiver();
        gameWindow.setTableCards(board);
        printBoard();
    }

    public static void getWinners() {
        for(Pot pot : pots) {
            ArrayList<Player> winners = Evaluator.findWinner(board, pot);
            for(int i = 0; i < winners.size(); i++) {
                System.out.print(pot + ": ");
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
            if((currentPot.getBets()[i] == currentPot.getHighestBet() || players.get(i).getStack() == 0) && currentPot.containsPlayer(players.get(i))) {
                matchCount++;
            }
        }

        //check that all players left in the hand have acted
        int actedCount = 0;
        for(int i = 0; i < currentPot.getPlayerHasActed().length; i++) {
            if(currentPot.getPlayerHasActed()[i] && currentPot.containsPlayer(players.get(i))) {
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
        if(players.get(currentActionIndex).getStack() - betValue < 0) {
            betValue = players.get(currentActionIndex).getStack();
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
        if (players.get(currentActionIndex).getStack() == 0) {
            return false;
        }

        return true;
    }

    public static boolean checkCallAllowed() {
        if (currentPot.getBets()[currentActionIndex] == getHighestBet()) {
            return false;
        } else if(players.get(currentActionIndex).getStack() == 0) {
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

    public static boolean checkRebuyAllowed() {
        return players.get(currentActionIndex).getStack() <= (int) (startingStackSize * rebuyThreshold);
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
            while (!currentPot.containsPlayer(players.get(currentActionIndex)) || players.get(currentActionIndex).checkHasHand() ||
                    players.get(currentActionIndex).getStack() == 0) {
                currentActionIndex++;
                wrapCurrentActionIndex();

                if(currentPot.getNumPlayersAllIn() == currentPot.getNumPlayersInPot()) {
                    break;
                }
            }
        } else {
            //preflop, if the pot does not contain the player, or the player is all in, move the action forward until we find a valid player
            while(players.get(currentActionIndex).checkHasHand() || players.get(currentActionIndex).getStack() == 0) {
                currentActionIndex++;
                wrapCurrentActionIndex();

                if(currentPot.getNumPlayersAllIn() == currentPot.getNumPlayersInPot()) {
                    break;
                }
            }
        }
    }

    public static void wrapCurrentActionIndex() {
        if (currentActionIndex > players.size() - 1) {
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
        for (int i = 0; i < players.size(); i++) {
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

            System.out.println(players.get(i).getPlayerName() + " " + currentPot.getBets()[i] + " " + players.get(i).getPlayerName() + " STACK: "
                    + players.get(i).getStack() + " " + info);
        }
        System.out.println();
        for(Pot pot : pots) {
            pot.printPlayersInPot();
        }
        System.out.println();
    }

    public static void printHands() {
        for (int i = 0; i < players.size(); i++) {
            if (mainPot.containsPlayer(players.get(i))) {
                players.get(i).printHand();
            }
        }
    }


    public static void dealHands() {
        for (Player player : players) {
            if(player.getStack() > 0) {
                player.drawHand(deck);
            }
        }
    }

    public static void rebuy() {
        players.get(currentActionIndex).resetStack();
    }

    public static void sitOut() {

    }

    public static void dealFlop() {
        for (int i = 0; i < 3; i++) {
            board[i] = deck.drawCard();
        }

        tryWriteActionToHH("Flop:" + writeBoardHelper());
    }

    private static void dealFlop(Card[] board, Deck deck, int value1, int suit1, int value2, int
            suit2, int value3, int suit3) {
        board[0] = deck.drawCard(value1, suit1);
        board[1] = deck.drawCard(value2, suit2);
        board[2] = deck.drawCard(value3, suit3);
    }

    public static void dealTurn() {
        board[3] = deck.drawCard();

        tryWriteActionToHH("Turn:" + writeBoardHelper());
    }

    private static void dealTurn(Card[] board, Deck deck, int value1, int suit1) {
        board[3] = deck.drawCard(value1, suit1);
    }

    public static void dealRiver() {
        board[4] = deck.drawCard();

        tryWriteActionToHH("River:" + writeBoardHelper());
    }

    private static void dealRiver(Card[] board, Deck deck, int value1, int suit1) {
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


    public static void equityCalculator(Player player1, int value1, int suit1, int value2, int
            suit2, Player player2, Card[] board, Deck deck) {
        double player1Wins = 0;
        double player2Wins = 0;
        double ties = 0;
        for (int i = 0; i < 1000000; i++) {
            player1.drawHand(deck, value1, suit1, value2, suit2);
            player2.drawHand(deck);
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

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public static void updateStartingStackValues() {
        for(Player player : players) {
            player.updateStreetStartingStackSize();
        }
    }

    public static void setLastRaiseSize(int lastRaiseSize) {
        Game.lastRaiseSize = lastRaiseSize;
    }

    public static void tryWriteActionToHH(String s) {
        try {
            if(recordHandHistory) {
                handHistory.writeAction(s);
            }
        } catch (IOException ignored) {
            //something here laters
        }
    }

    public static StringBuilder writeBoardHelper() {
        StringBuilder s = new StringBuilder();
        for(Card card : board) {
            if(card != null) {
                s.append(" ").append(card.getShortName());
            }
        }
        return s;
    }
}