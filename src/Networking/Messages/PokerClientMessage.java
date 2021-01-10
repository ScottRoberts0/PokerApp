package Networking.Messages;

import Logic.Card;
import Logic.Player;
import Networking.Messages.PokerMessage;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;
import com.fasterxml.jackson.core.*;

import java.io.IOException;
import java.util.ArrayList;

public class PokerClientMessage extends LegacyMessage {
    public final static int EXAMPLE_MESSAGE_ID = 1000;
    public PokerClientMessage() {
        super(EXAMPLE_MESSAGE_ID);
    }

    public PokerClientMessage(BeamMessage message) {
        super (message);
    }

    /**
     * This method handles the response from the server after sending a message
     * Called whenever a client sends a message
     * @param message
     */
    public static void handleServerResponse(LegacyMessage message){
        String messageType = message.getString(PokerMessage.MESSAGE_TYPE);
        System.out.println("Type: " + messageType);

        // ---------------------------- Handshake -----------------------
        if(messageType.equals(PokerMessage.MESSAGE_TYPE_HANDSHAKE)){
            handleHandshake(message);
        }
        // ---------------------------- Error -----------------------
        else if(messageType.equals(PokerMessage.MESSAGE_TYPE_ERROR)){

        }
    }

    public static void handleHandshake(LegacyMessage message){
        Integer playerNum = message.getInt(PokerMessage.MESSAGE_PLAYERNUM);

        System.out.println("PlayerNum: " + playerNum);
        System.out.println("");
    }

    /**
     * Handles a received broadcast from the server
     * Called by PokerHandler
     * @param message
     */
    public static void handleGameDataMessage(LegacyMessage message){
        JsonFactory factory = new JsonFactory();

        // grab the number of players
        Integer numPlyaers = message.getInt(PokerMessage.MESSAGE_NUMPLAYERS);
        System.out.println("Num Players: " + numPlyaers);

        // cycle through the players in the message
        ArrayList<String> playerData = new ArrayList<>();
        for(int i = 0; i < numPlyaers; i++){
            String playerName = "";
            int card0Value = -1, card0Suit = -1, card1Value = -1, card1Suit = -1, stack = -1;

            playerData.add(message.getString(PokerMessage.MESSAGE_PLAYER_STRING + i));
            System.out.println(playerData.get(i));

            // parse the data
            try {
                JsonParser parser = factory.createParser(playerData.get(i));

                // iterate through the json data
                while(!parser.isClosed()){
                    JsonToken token = parser.nextToken();

                    if(token != null && token.equals(JsonToken.FIELD_NAME)){
                        // grab the field name
                        String fieldName = parser.getCurrentName();

                        // the next token is the data
                        parser.nextToken();

                        if(fieldName.equals(Player.PLAYER_NAME)){
                            playerName = parser.getValueAsString();
                        }else if(fieldName.equals(Player.PLAYER_CARD1_VALUE)){
                            card0Value = parser.getIntValue();
                        }else if(fieldName.equals(Player.PLAYER_CARD1_SUIT)){
                            card0Suit = parser.getIntValue();
                        }else if(fieldName.equals(Player.PLAYER_CARD2_VALUE)){
                            card1Value = parser.getIntValue();
                        }else if(fieldName.equals(Player.PLAYER_CARD2_SUIT)){
                            card1Suit = parser.getIntValue();
                        }else if(fieldName.equals(Player.PLAYER_STACK)){
                            stack = parser.getIntValue();
                        }
                    }
                }
            }catch(JsonParseException e){
                // shit failed I guess?
                System.out.println("JSON parsing failed of player number: " + i);
            }catch (IOException e){
                // throw this away
            }

            Player player = new Player(i, stack, playerName);
            if(card0Suit == -1){
                player.setHand(new Card[] { null, null});
            }else{
                player.setHand(new Card[] { new Card(card0Value, card0Suit), new Card(card1Value, card1Suit)});
            }

            System.out.println(player);
        }

        System.out.println("");
    }
}
