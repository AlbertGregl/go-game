package hr.gregl.gogame.game.networking;

import hr.gregl.gogame.game.utility.GameSaveState;
import hr.gregl.gogame.game.utility.LogUtil;
import hr.gregl.gogame.game.utility.MessageState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient {
    private final Socket clientSocket;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final GameStateUpdateListener gameStateUpdateListener;

    public GameClient(String host, int serverPort, GameStateUpdateListener listener) throws IOException {
        this.gameStateUpdateListener = listener;
        this.clientSocket = new Socket(host, serverPort);
        this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        this.objectOutputStream.flush();
        this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println(LogUtil.infoLogMsg5 + host + ":" + serverPort);
        LogUtil.logInfo(LogUtil.infoLogMsg5 + host + ":" + serverPort);
        startListening();
    }
    private void startListening() {
        new Thread(this::listen).start();
    }

    private void listen() {
        try {
            while (!clientSocket.isClosed()) {
                Object object = objectInputStream.readObject();
                if (object instanceof GameSaveState gameState) {
                    gameStateUpdateListener.onGameStateReceived(gameState);
                } else if (object instanceof MessageState messageState) {
                    gameStateUpdateListener.onMessageReceived(messageState);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.logError(e);
            cleanup();
        }
    }

    public void sendGameState(GameSaveState gameState) throws IOException {
        objectOutputStream.writeObject(gameState);
        objectOutputStream.flush();
    }

    private void cleanup() {
        ClientHandler.cleanClientHandler(objectInputStream, objectOutputStream, clientSocket);
    }

    public void disconnect() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    public void sendMessage(MessageState messageState) throws IOException {
        objectOutputStream.writeObject(messageState);
        objectOutputStream.flush();
    }
}
