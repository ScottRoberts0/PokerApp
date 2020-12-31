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

    }

    public static void foldAction(){

    }

    public static void raiseAction(){

    }

    public static void checkAction(){

    }
}
