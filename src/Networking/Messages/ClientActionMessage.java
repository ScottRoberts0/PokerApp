package Networking.Messages;

import Logic.Game;
import Networking.Networker;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

public class ClientActionMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1006;

    private final static String CLIENT_ACTION = "clientAction";
    private final static String RAISE_VALUE = "raiseValue";

    public ClientActionMessage(int action, int raiseValue){
        super(MESSAGE_ID);

        setInt(CLIENT_ACTION, action);
        setInt(RAISE_VALUE, raiseValue);
    }

    public ClientActionMessage(BeamMessage message) {
        super (message);
    }

    public static void serverHandle(Communicator communicator, LegacyMessage message){
        // grab the things
        int actionsAllowed = message.getInt(CLIENT_ACTION);
        int raiseValue = message.getInt(RAISE_VALUE);

        // do the things
        if((actionsAllowed & ActionPromptMessage.CHECK_BIT) == ActionPromptMessage.CHECK_BIT){
            Main.getGameWindow().checkButtonAction();
        }else if((actionsAllowed & ActionPromptMessage.FOLD_BIT) == ActionPromptMessage.FOLD_BIT){
            Main.getGameWindow().foldButtonAction();
        }else if((actionsAllowed & ActionPromptMessage.CALL_BIT) == ActionPromptMessage.CALL_BIT){
            Main.getGameWindow().callButtonAction();
        }else if((actionsAllowed & ActionPromptMessage.RAISE_BIT) == ActionPromptMessage.RAISE_BIT){
            Main.getGameWindow().raiseButtonAction(raiseValue);
        }

        // send back some game data
        Networker.getInstance().broadcastGameData();
    }
}