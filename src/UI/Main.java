package UI;

import Logic.Card;
import Logic.Deck;
import Logic.Game;
import Logic.Player;

public class Main {

    private static Table gameTable;

    public static void main(String argsp[]){
        if(argsp.length > 0) {
            gameTable = new Table(12);
        }else {
            //Logic.Game.testHands("FULL HOUSE", 1000, 3, 2);

        }
    }

    public static void callAction(){
        System.out.println("Call");
        gameTable.setPlayerCard(0, 0, 0, 1);
    }

    public static void foldAction(){
        System.out.println("Fold");
        gameTable.setPlayerCard(0, 1, 0, 2);
    }

    public static void raiseAction(){
        System.out.println("Raise");
        gameTable.setPlayerCard(0, 0, 0, 0);
    }

    public static void checkAction(){
        System.out.println("Check");
        gameTable.setPlayerCard(0, 1, 0, 3);
    }
}
