package Networking;

import Logic.Game;
import Logic.Player;
import Networking.MessageHandlers.PokerHandler;
import Networking.Messages.PokerClientMessage;
import Networking.Messages.PokerMessage;
import Networking.Messages.PokerServerMessage;
import com.codebrig.beam.BeamClient;
import com.codebrig.beam.BeamServer;
import com.codebrig.beam.messages.BeamMessage;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Networker {

    private static Networker instance;

    private BeamServer server;
    private BeamClient client;

    private boolean isServer;

    public Networker(boolean isServer){
        instance = this;
        this.isServer = isServer;

        if(isServer){
            beginServer();
        }else{
            beginClient();
        }
    }

    public static Networker getInstance(){
        return instance;
    }

    public void beginServer(){
        System.out.println("Server, baby!");
        // create and start the server
        server = new BeamServer("Poker Server", 45800, false);
        server.start();

        // add a handler for messages
        server.addHandler(PokerHandler.class);

        // check if it's alive and then open the UI
        if(server.isAlive()){
            System.out.println("Server is alive");
        }


    }

    public void beginClient(){
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
        client.addHandler(PokerHandler.class);

        // handshake with the server. Send a player name and wait for your player number.
        PokerClientMessage message = new PokerClientMessage();
        message.setString(PokerMessage.MESSAGE_TYPE, PokerMessage.MESSAGE_TYPE_HANDSHAKE);
        message.setString(PokerMessage.MESSAGE_PLAYERNAME, "Tyler");

        BeamMessage response = client.sendMessage(message);

        if(response != null){
            PokerClientMessage cm = new PokerClientMessage(response);
            PokerClientMessage.handleServerResponse(cm);
        }else{
            System.out.println("Response not received");
        }
    }

    public boolean getIsServer(){
        return isServer;
    }

    public void broadCastInitialGameData(){
        if(!isServer)
            return;

        // get a message ready and put a message type header on it
        PokerServerMessage message = new PokerServerMessage();
        message.setString(PokerMessage.MESSAGE_TYPE, PokerMessage.MESSAGE_TYPE_GAME_DATA);

        // grab the players
        List<Player> players = Game.getPlayers();

        // start plugging this message full of data
        // add number of players
        message.setInt(PokerMessage.MESSAGE_NUMPLAYERS, players.size());

        // add player data
        for(int i = 0; i < players.size(); i ++){
            message.setString(PokerMessage.MESSAGE_PLAYER_STRING + i, players.get(i).getPlayerStateForNetwork());
        }

        server.broadcast(message);
    }

    public void close(){
        if(isServer) {
            server.close();
        }else {
            client.close();
        }
    }
}
