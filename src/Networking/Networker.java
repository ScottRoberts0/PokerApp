package Networking;

import Networking.MessageHandlers.PokerHandler;
import Networking.Messages.PokerClientMessage;
import Networking.Messages.PokerMessage;
import Networking.Messages.PokerServerMessage;
import com.codebrig.beam.BeamClient;
import com.codebrig.beam.BeamServer;
import com.codebrig.beam.messages.BeamMessage;

import java.io.IOException;
import java.net.UnknownHostException;

public class Networker {

    private static Networker instance;

    private BeamServer server;
    private BeamClient client;

    private int numPlayers;
    private int playerNum;

    private boolean isServer;

    public Networker(boolean isServer){
        instance = this;
        this.isServer = isServer;

        this.numPlayers = 0;

        if(isServer){
            beginServer();
        }else{
            beginClient();
        }
    }

    public static Networker getInstance(){
        return instance;
    }

    public void incrementPlayers(){
        numPlayers++;
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
            System.out.println("Response received");
            PokerClientMessage cm = new PokerClientMessage(response);
            PokerClientMessage.handleServerResponse(cm);
        }else{
            System.out.println("Response not received");
        }
    }

    public boolean getIsServer(){
        return isServer;
    }

    public void broadCastGameData(){
        if(!isServer)
            return;

        PokerServerMessage message = new PokerServerMessage();
        message.setString(PokerMessage.MESSAGE_TYPE, PokerMessage.MESSAGE_TYPE_GAME_DATA); // put in the message type as neccessary

        // start plugging this message full of data
        
        message.setInt(PokerMessage.MESSAGE_NUMPLAYERS, 2);


        server.broadcast(message);
    }

    public void close(){
        if(isServer) {
            server.close();
        }else {

            client.close();
        }
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
