package UI;

import Logic.Card;
import Logic.Deck;
import Logic.Game;
import Logic.Player;

import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;

public class Main {

    private static int street;
    private static final int GAMESTATE_WAIT_ACTION = 1;

    private static Table gameTable;
    private static Deck deck;
    private static Card[] board;
    private static Player[] players;
    private static int[] bets;
    private static boolean[] playerHasActed;
    private static boolean[] playersInHand;
    private static int pot;
    private static int limitRaiseSize;
    private static int startingStackSize;

    private static int sb;
    private static int bb;

    public static void main(String argsp[]) {
        deck = new Deck();
        board = new Card[5];
        street = 0;
        sb = 500;
        bb = 1000;
        pot = 0;
        startingStackSize = 100000;
        limitRaiseSize = bb;

        //players = Game.createPlayers(5, deck, startingStackSize);
        players = new Player[6];
        players[0] = new Player(0, deck, startingStackSize, "Reid");
        players[1] = new Player(1, deck, startingStackSize, "Tyler");
        players[2] = new Player(2, deck, startingStackSize);
        players[3] = new Player(3, deck, startingStackSize);
        players[4] = new Player(4, deck, startingStackSize, "Dan");
        players[5] = new Player(5, deck, startingStackSize / 10, "Cody");

        bets = new int[players.length];
        playerHasActed = new boolean[players.length];
        playersInHand = new boolean[players.length];

        startGame();
    }

    public static void addToPot(int betSize) {
        pot += betSize;
    }

    public static int getPot() {
        return pot;
    }

    private static void nextStreet() {
        //i'd like to do something better with this method maybe
        Arrays.fill(playerHasActed, false);
        Game.resetBets(players, bets);
        street++;
        if (street == 1) {
            Game.flop(players, board, deck, playersInHand, gameTable);
        } else if (street == 2) {
            Game.turn(players, board, deck, playersInHand, gameTable);
        } else if (street == 3) {
            Game.river(players, board, deck, playersInHand, gameTable);
        } else if (street >= 4) {
            Game.getWinners(players, board, playersInHand, pot);
            endHand();
        }
    }

    public static void callButtonAction() {
        players[Game.getCurrentActionIndex()].call(bets, playerHasActed);
        if (Game.checkFolds(players, playersInHand)) {
            endHand();
        } else if (Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
            nextStreet();
        } else {
            Game.updateCurrentAction(players, playersInHand);
        }

        Game.printPlayers(players, bets, playersInHand);

        gameTable.updateButtons(players, bets, 0);
    }

    public static void foldButtonAction() {
        players[Game.getCurrentActionIndex()].fold(bets, playersInHand);
        gameTable.updatePlayer(players);
        if (Game.checkFolds(players, playersInHand)) {
            endHand();
        } else if (Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
            nextStreet();
        } else {
            Game.updateCurrentAction(players, playersInHand);
        }

        Game.printPlayers(players, bets, playersInHand);

        gameTable.updateButtons(players, bets, 0);
    }

    public static void raiseButtonAction() {
        players[Game.getCurrentActionIndex()].bet(limitRaiseSize, bets, playerHasActed);
        if (Game.checkFolds(players, playersInHand)) {
            endHand();
        } else if (Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
            nextStreet();
        } else {
            Game.updateCurrentAction(players, playersInHand);
        }

        Game.printPlayers(players, bets, playersInHand);

        gameTable.updateButtons(players, bets, 50);
    }

    public static void checkButtonAction() {
        players[Game.getCurrentActionIndex()].check(playerHasActed);
        if (Game.checkFolds(players, playersInHand)) {
            endHand();
        } else if (Game.checkBettingRoundCompleted(players, bets, playersInHand, playerHasActed)) {
            nextStreet();
        } else {
            Game.updateCurrentAction(players, playersInHand);
        }

        Game.printPlayers(players, bets, playersInHand);

        gameTable.updateButtons(players, bets, 0);
    }

    public static void resetButtonAction() {
        resetHand();
        System.out.println("Reset Pressed");
    }

    public static void testButtonAction(){

    }

    public static void startGame() {
        Arrays.fill(board, null);
        Arrays.fill(playersInHand, true);
        Game.resetBets(players, bets);

        Game.pickRandomDealer(players);

        Game.dealHands(players);

        Game.setStartingActionIndex(players, playersInHand, street);
        players[Game.getSmallBlindIndex()].postBlind(sb, bets);
        players[Game.getBigBlindIndex()].postBlind(bb, bets);

        Game.printPlayers(players, bets, playersInHand);

        gameTable = new Table(players);
        gameTable.updateButtons(players, bets, 0);

        gameTable.getTable().createPlayerCards(true);
    }

    public static void endHand() {
        if (Game.checkFolds(players, playersInHand)) {
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
        Game.printHands(players, playersInHand);
        Game.printBoard(board);

        pot = 0;
        street = 0;
        deck.shuffle();

        Arrays.fill(playerHasActed, false);
        Arrays.fill(playersInHand, true);
        Arrays.fill(board, null);

        Game.resetFolds(players);
        Game.resetBets(players, bets);
        Game.nextDealer(players);
        Game.setStartingActionIndex(players, playersInHand, street);
        Game.dealHands(players);

        players[Game.getSmallBlindIndex()].postBlind(sb, bets);
        players[Game.getBigBlindIndex()].postBlind(bb, bets);

        gameTable.updateButtons(players, bets, 0);

        gameTable.getTable().deletePlayerCards();

        gameTable.getTable().createPlayerCards(true);
    }
}
