package hr.gregl.gogame.game.networking;

import hr.gregl.gogame.game.utility.GameSaveState;

public interface GameStateUpdateListener {
    void onGameStateReceived(GameSaveState gameState);
}
