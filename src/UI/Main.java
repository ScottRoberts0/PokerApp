package UI;

import Logic.Game;
import Networking.Networker;

public class Main {

    private static MainWindow gameWindow;
    private static Networker networker;

    public static void main(String[] argsp) {
        if(argsp != null && argsp.length > 0){
            if(argsp[0].equals("-client")){
                networker = new Networker(false);
                beginUI();
            }else if(argsp[0].equals("-server")){
                networker = new Networker(true);
                //Game.startGame();
                //gameWindow.updateButtons();
                beginUI();
            }else{
                beginUI();
                Game.startGame();
                gameWindow.updateButtons();
            }
        }
    }

    public static Networker getNetworker(){
        return networker;
    }

    public static void beginUI(){
        gameWindow = new MainWindow();
    }

    public static MainWindow getGameWindow(){
        return gameWindow;
    }

}