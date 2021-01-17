package Networking.Messages;

import Logic.Game;
import Logic.Player;
import Networking.Networker;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;
import com.sun.jdi.connect.Connector;

public class ClientConnectedMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1000;

    public ClientConnectedMessage() {
        super(MESSAGE_ID);
    }

    public ClientConnectedMessage(String playerName){
        super(MESSAGE_ID);
        this.setString(PokerMessage.MESSAGE_PLAYERNAME, playerName);
    }

    public ClientConnectedMessage(BeamMessage message) {
        super (message);
    }

    /**
     * This is called when the server receives a message from a client that has just connected
     *
     * @param communicator
     * @param message
     * @return
     */
    public static ClientConnectedMessage serverClientConnected(Communicator communicator, LegacyMessage message){
        ClientConnectedMessage response = new ClientConnectedMessage();


        return response;
    }


}

