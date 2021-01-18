package Networking.Messages;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import UI.Main;
import UI.MainWindow;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDataMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1001;

    public PlayerDataMessage() {
        super(MESSAGE_ID);

        // grab the players
        List<Player> players = Game.getPlayers();

        // start plugging this message full of data
        // add number of players
        this.setInt(PokerMessage.MESSAGE_NUMPLAYERS, players.size());

        // add player data
        for(int i = 0; i < players.size(); i ++){
            this.setString(PokerMessage.MESSAGE_PLAYER_STRING + i, players.get(i).getPlayerStateForNetwork());
        }
    }

    public PlayerDataMessage(BeamMessage message) {
        super (message);
    }

    public static void clientHandle(Communicator communicator, LegacyMessage message){
        // this message is always a broadcast, no need for a response
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

            Game.addPlayer(player);
        }

        System.out.println("");


        Main.getGameWindow().getTable().createPlayerCards(true);

        //Main.getGameWindow().setIsGameStarted(true);
    }
}
