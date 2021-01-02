package UI;

import Logic.*;

import java.util.Arrays;

public class Main {

    private static int street;
    private static final int GAMESTATE_WAIT_ACTION = 1;
    private static final int GAMESTATE_PLAYER_ACTION = 2;

    private static Table gameTable;
    private static Deck deck;
    private static Card[] board;
    private static Player[] players;
    private static int[] bets;
    private static boolean[] playerHasActed;
    private static boolean[] playersInHand;
    private static int pot;

    private static int gameState;
    private static int sb;
    private static int bb;

    //TODO: implement resetting the game

    public static void main(String argsp[]){
        if(argsp.length > 0) {
            deck = new Deck();
            board = new Card[5];
            players = Game.createPlayers(5, deck, 25000);
            bets = new int[players.length];
            playerHasActed = new boolean[players.length];
            playersInHand = new boolean[players.length];
            street = 0;
            sb = 25;
            bb = 50;
            pot = 0;

            startGame();
        } else {
            //Logic.Game.testHands("FULL HOUSE", 1000, 3, 2);
            Deck deck = new Deck();
            Card[] board = new Card[5];
            Player[] players = Game.createPlayers(5, deck, 25000);

            Game.pickRandomDealer(players);
            for(int i = 0; i < 10; i++) {
                Game.hand(players, board, deck, 25, 50);
            }
        }
    }

    public static void addToPot(int betSize) {
        pot += betSize;
    }

    public static int getPot() {
        return pot;
    }

    private static void nextStreet() {
        Arrays.fill(bets, 0);
        Arrays.fill(playerHasActed, false);
        street++;
        if(street == 1) {
            Game.setStartingActionIndex(players, playersInHand, 1);
            Game.dealFlop(board, deck);
            gameTable.setTableCards(board);
        } else if(street == 2) {
            Game.setStartingActionIndex(players, playersInHand, 2);
            Game.dealTurn(board, deck);
            gameTable.setTableCards(board);
        } else if(street == 3) {
            Game.setStartingActionIndex(players, playersInHand, 3);
            Game.dealRiver(board, deck);
            gameTable.setTableCards(board);
        } else if(street >= 4) {
            boolean[] winners = Evaluator.findWinner(players, board, playersInHand);

            int winnerCount = 0;
            for(boolean winner : winners) {
                if(winner) {
                    winnerCount++;
                }
            }

            for(int i = 0; i < winners.length; i++) {
                if(winners[i]) {
                    players[i].win(pot / winnerCount);
                }
            }

            endGame();
        }
    }

    public static void callButtonAction(){
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].call(bets, playerHasActed);
            if(Game.checkFolds(players, playersInHand)) {
                endGame();
            } else if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }
        Game.printPlayers(players, bets, playersInHand);

        gameTable.updateButtons(players, bets, 0);
    }

    public static void foldButtonAction() {
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].fold(bets, playersInHand);
            gameTable.updatePlayer(players);
            if(Game.checkFolds(players, playersInHand)) {
                endGame();
            } else if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }
        Game.printPlayers(players, bets, playersInHand);

        gameTable.updateButtons(players, bets, 0);
    }

    public static void raiseButtonAction() {
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].bet(50, bets, playerHasActed);
            if(Game.checkFolds(players, playersInHand)) {
                endGame();
            } else if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }
        Game.printPlayers(players, bets, playersInHand);

        gameTable.updateButtons(players, bets, 50);
    }

    public static void checkButtonAction() {
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].check(playerHasActed);
            if(Game.checkFolds(players, playersInHand)) {
                endGame();
            } else if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }

        gameTable.updateButtons(players, bets, 0);
    }

    public static void resetButtonAction(){
        //TODO: Reset, motherfucker.
        System.out.println("Reset Pressed");
    }

    public static void startGame() {
            Arrays.fill(board, null);
            Arrays.fill(playersInHand, true);

            Game.pickRandomDealer(players);

            Game.dealHands(players);

            Game.setStartingActionIndex(players, playersInHand, street);
            players[Game.getSmallBlindIndex()].postBlind(sb, bets);
            players[Game.getBigBlindIndex()].postBlind(bb, bets);

            for (int bet : bets) {
                System.out.println(bet);
            }

            gameState = GAMESTATE_WAIT_ACTION;

            gameTable = new Table(players);
    }

    public static void endGame() {
        if(Game.checkFolds(players, playersInHand)) {
            for (int i = 0; i < playersInHand.length; i++) {
                if (playersInHand[i]) {
                    players[i].win(pot);
                    break;
                }
            }
        }

        //reset the game state for the next hand
        pot = 0;
        street = 0;
        deck.shuffle();
        Arrays.fill(playerHasActed, false);
        Arrays.fill(playersInHand, true);
        Arrays.fill(board, null);
        Arrays.fill(bets, 0);
        Game.nextDealer(players);
        Game.setStartingActionIndex(players, playersInHand, street);
    }
}
