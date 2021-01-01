package UI;

import Logic.Card;
import Logic.Deck;
import Logic.Game;
import Logic.Player;

public class Main {

    private static final int STATE_PREFLOP = 0;
    private static final int STATE_WAIT_ACTION = 1;

    private static Table gameTable;
    private static Deck deck;
    private static Card[] board;
    private static Player[] players;

    private static int gameState;

    public static void main(String argsp[]){
        if(argsp.length > 0) {
            deck = new Deck();
            board = new Card[5];
            players = Game.createPlayers(5, deck, 25000);

            Game.dealHands(players);

            gameTable = new Table(players);
            gameTable.setTableCards(board);

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

    public static void callAction(){
        if(gameState == STATE_PREFLOP){

        }
    }

    public static void foldAction(){

    }

    public static void raiseAction(){

    }

    public static void checkAction(){

    }

    public static void startGame(){
    }
}
