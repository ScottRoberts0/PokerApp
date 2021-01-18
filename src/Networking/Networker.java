package Networking;

import Networking.MessageHandlers.ClientHandler;
import Networking.MessageHandlers.ServerHandler;
import Networking.Messages.*;
import UI.LobbyWindow;
import com.codebrig.beam.BeamClient;
import com.codebrig.beam.BeamServer;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Networker {

    private static Networker instance;

    private BeamServer server;
    private BeamClient client;

    private boolean isServer;

    private ArrayList<String> playersInLobby;
    private ArrayList<Long> playerUIDs;
    private boolean[] playersReadied;

    /**
     * This constructor is used to begin a server Networker
     */
    public Networker(){
        instance = this;
        this.isServer = true;

        playersInLobby = new ArrayList<>();
        playerUIDs = new ArrayList<>();


        System.out.println("Server, baby!");
        // create and start the server
        server = new BeamServer("Poker Server", 45800, false);
        server.start();

        // add a handler for messages
        server.addHandler(ServerHandler.class);

        // check if it's alive and then open the UI
        if(!server.isAlive())
            return;

        System.out.println("Server is alive");

        // add yourself to the lobby list

    }

    /**
     * This constructor is used to begin a client Networker
     *
     * @param address IP Address
     */
    public Networker(String address, String playerName){
        // store the instance and set the server flag
        instance = this;
        this.isServer = false;

        // initialize the player list
        playersInLobby = new ArrayList<>();

        System.out.println("Client, baby!");
        try {
            // create the client and connect to the server
            client = new BeamClient("127.0.0.1", "PokerClient", 45800, false);

            System.out.println("Connecting...");
            client.connect();
        }catch (UnknownHostException e){
            System.out.println(e.getMessage());
            System.exit(2);
            return;
        }catch (IOException e1){
            client = null;
            System.out.println(e1.getMessage());
            System.exit(3);
            return;
        }

        // wait around until the connection is complete
        while(!client.isConnected()) {}
        System.out.println("Connected");

        // add a handler
        client.addHandler(ClientHandler.class);

        // handshake with the server. Send a player name
        ClientConnectedMessage mess = new ClientConnectedMessage(playerName);

        // the response will be the current player list
        BeamMessage response = client.sendMessage(mess);


        if(response != null){
            System.out.println("Message received: Connected to server\n");
        }else{
            System.out.println("Response not received\n");
        }
    }

    public boolean getClientConnected(){
        if(client != null){
            return client.isConnected();
        }
        return false;
    }

    public ArrayList<String> getPlayersInLobby(){
        return playersInLobby;
    }

    public void addPlayerToLobby(String playerName){
        playersInLobby.add(playerName);

        LobbyWindow.getInstance().updatePlayerList(playersInLobby);
    }

    public void addPlayerToLobby(String playerName, long communicatorUID){
        playersInLobby.add(playerName);
        playerUIDs.add(communicatorUID);

        LobbyWindow.getInstance().updatePlayerList(playersInLobby);
    }

    public void clearPlayerList(){
        playersInLobby.clear();
    }

    public static Networker getInstance(){
        return instance;
    }

    public boolean getIsServer(){
        return isServer;
    }

    public void broadCastInitialGameData(){
        if(!isServer)
            return;

        // get a message ready and put a message type header on it
        PlayerDataMessage message = new PlayerDataMessage();

        server.broadcast(message);
    }

    public void broadcastLobbyPlayerList(){
        // create the broadcast message
        PlayersInLobbyMessage playerListMessage = new PlayersInLobbyMessage();

        //FIRE!!
        server.broadcast(playerListMessage);
    }

    public void close(){
        if(isServer) {
            if(server != null && server.isAlive())
                // TODO: Broadcast a server closing message to let clients gracefully disconnect first.
                server.close();
        }else {
            if(client != null && client.isConnected())
                // TODO: Send a message to the server saying you're disconnecting. Might be useful later for reconnecting.
                client.close();
        }
    }

    public void sendStartGameMessages(){
        playersReadied = new boolean[playerUIDs.size()];

        for(int i = 0; i < playerUIDs.size(); i++){
            long id = playerUIDs.get(i);
            if(id > -1){
                // send off the player's number
                PlayerNumMessage message = new PlayerNumMessage(i);
                Communicator comm = server.getPool().getCommunicator(id);

                LegacyMessage response = new LegacyMessage(comm.send(message));

                if(response != null){
                    playersReadied[i] = true;
                    System.out.println("Player " + i + " readied.");
                }
            }else{
                playersReadied[i] = true;
            }
        }

        // double check to see if all the players are readied
        boolean allReadied = true;
        for(int i = 0; i < playersReadied.length; i ++){
            if(!playersReadied[i]){
                allReadied = false;
                break;
            }
        }

        // if they are, broadcast some game data, cards and shit
        if(allReadied){
            broadCastInitialGameData();
        }
    }
}
