package hr.gregl.gogame.game.networking;

import hr.gregl.gogame.game.utility.GameSaveState;
import hr.gregl.gogame.game.utility.LogUtil;
import hr.gregl.gogame.game.utility.MessageState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private final ServerSocket serverSocket;
    private final GameStateUpdateListener gameStateUpdateListener;
    private ClientHandler clientHandler;

    public GameServer(int serverPort, GameStateUpdateListener listener) throws IOException {
        this.gameStateUpdateListener = listener;
        this.serverSocket = new ServerSocket(serverPort);
        System.out.println(LogUtil.infoLogMsg6 + serverPort);
        LogUtil.logInfo(LogUtil.infoLogMsg6 + serverPort);
        startListening();
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(LogUtil.infoLogMsg7 + serverSocket.getLocalPort());
                    LogUtil.logInfo(LogUtil.infoLogMsg7 + serverSocket.getLocalPort());
                    clientHandler = new ClientHandler(clientSocket, gameStateUpdateListener);
                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        }).start();
    }

    public void sendGameStateToClient(GameSaveState gameState) throws IOException {
        clientHandler.sendGameState(gameState);
    }

    public void shutdownServer() {
        try {
            if (clientHandler != null) {
                clientHandler.closeConnection();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    public void sendMessageToClient(MessageState messageState) {
        clientHandler.sendMessageState(messageState);
    }
}
