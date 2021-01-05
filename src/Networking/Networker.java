package Networking;

import Networking.MessageHandlers.PokerHandler;
import Networking.MessageHandlers.PokerMessage;
import UI.Main;
import com.codebrig.beam.BeamClient;
import com.codebrig.beam.BeamServer;
import com.codebrig.beam.messages.BeamMessage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Networker {

    private BeamServer server;
    private BeamClient client;

    private boolean isServer;

    public Networker(boolean isServer){
        this.isServer = isServer;

        if(isServer){
            beginServer();
        }else{
            beginClient();
        }
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
            //System.exit(3);
            return;
        }
        while(!client.isConnected()) {

        }
        System.out.println("Connected");
    }

    public boolean getIsServer(){
        return isServer;
    }

    public void close(){
        if(isServer) {
            server.close();
        }else {
            client.close();
        }
    }
}
