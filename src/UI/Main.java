package UI;

import Logic.Card;
import Logic.Deck;
import Logic.Game;
import Logic.Player;

public class Main {

    private static Table gameTable;
    private static Deck deck;
    private static Card[] board;
    private static Player[] players;

    public static void main(String argsp[]){
        if(argsp.length > 0) {
            gameTable = new Table(5);
            deck = new Deck();
            board = new Card[5];
            players = Game.createPlayers(5, deck, 25000);

            Game.dealHands(players);
            Game.hand();

            gameTable.setPlayerCard(players[0]);
            gameTable.setPlayerCard(players[1]);
            gameTable.setPlayerCard(players[2]);
            gameTable.setPlayerCard(players[3]);
            gameTable.setPlayerCard(players[4]);

            Game.dealFlop(board, deck);

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
        Game.dealTurn(board, deck);

        gameTable.setTableCards(board);
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
