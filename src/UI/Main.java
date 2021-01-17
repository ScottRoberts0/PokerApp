package UI;

import Logic.Game;
import Networking.Networker;

public class Main {

    private static MainWindow gameWindow;
    private static LobbyWindow lobbyWindow;

    public static void main(String[] argsp) {
        if(argsp != null && argsp.length > 0){
            if(argsp[0].equals("-network")){
                if(argsp.length > 1 && argsp[1] != null) {
                    beginLobbyUI(argsp[1]);
                }else{
                    beginLobbyUI("Tyler");
                }
            }else{
                beginGameUI();
                Game.startGame();
                gameWindow.updateButtons();
            }
        }
    }

    public static void beginGameUI(){
        gameWindow = new MainWindow();
    }

    public static void beginLobbyUI(String playerNameDefault){
        lobbyWindow = new LobbyWindow(playerNameDefault);
    }

    public static MainWindow getGameWindow(){
        return gameWindow;
    }

}