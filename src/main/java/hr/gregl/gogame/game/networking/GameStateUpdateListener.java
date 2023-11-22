package hr.gregl.gogame.game.networking;

import hr.gregl.gogame.game.utility.GameSaveState;
import hr.gregl.gogame.game.utility.MessageState;

public interface GameStateUpdateListener {
    void onGameStateReceived(GameSaveState gameState);
    void onMessageReceived(MessageState messageState);
}
