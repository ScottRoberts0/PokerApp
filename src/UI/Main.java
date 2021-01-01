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

    public static void main(String argsp[]){
        if(argsp.length > 0) {
            gameTable = new Table(5);
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
            Game.setStartingActionIndex(players, playersInHand, 1, bets, sb, bb);
            Game.dealFlop(board, deck);
            gameTable.setTableCards(board);
        } else if(street == 2) {
            Game.setStartingActionIndex(players, playersInHand, 2, bets, sb, bb);
            Game.dealTurn(board, deck);
            gameTable.setTableCards(board);
        } else if(street == 3) {
            Game.setStartingActionIndex(players, playersInHand, 3, bets, sb, bb);
            Game.dealRiver(board, deck);
            gameTable.setTableCards(board);
        } else if(street == 4) {
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
                    System.out.println("Player " + players[i].getPlayerNum() + " wins " + (pot / winnerCount));
                }
            }
        }
    }

    public static void callAction(){
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].call(bets, playerHasActed);
            if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }
        Game.printPlayers(players, bets, playersInHand);
    }

    public static void foldAction() {
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].fold(bets, playersInHand);
            if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }
        Game.printPlayers(players, bets, playersInHand);
    }

    public static void raiseAction() {
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].bet(50, bets, playerHasActed);
            if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }
        Game.printPlayers(players, bets, playersInHand);
    }

    public static void checkAction() {
        if(gameState == GAMESTATE_WAIT_ACTION) {
            players[Game.getCurrentActionIndex()].check(playerHasActed);
            if(Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
                nextStreet();
            } else {
                Game.updateCurrentAction(players, playersInHand);
            }
        }
    }

    public static void startGame() {
        Arrays.fill(board, null);
        Arrays.fill(playersInHand, true);

        Game.pickRandomDealer(players);
        Game.dealHands(players);
        for(int i = 0; i < players.length; i++) {
            gameTable.setPlayerCard(players[i]);
        }
        Game.setStartingActionIndex(players, playersInHand, 0, bets, sb, bb);

        for(int bet : bets) {
            System.out.println(bet);
        }

        gameState = GAMESTATE_WAIT_ACTION;
    }
}
