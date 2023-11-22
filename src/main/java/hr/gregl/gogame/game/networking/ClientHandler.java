package hr.gregl.gogame.game.networking;

import hr.gregl.gogame.game.utility.GameSaveState;
import hr.gregl.gogame.game.utility.LogUtil;
import hr.gregl.gogame.game.utility.MessageState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;
    private final GameStateUpdateListener gameStateUpdateListener;

    public ClientHandler(Socket socket, GameStateUpdateListener listener) throws IOException {
        this.socket = socket;
        this.gameStateUpdateListener = listener;
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectOutputStream.flush();
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Object object = objectInputStream.readObject();
                if (object instanceof GameSaveState gameState) {
                    gameStateUpdateListener.onGameStateReceived(gameState);
                } else if (object instanceof MessageState messageState) {
                    gameStateUpdateListener.onMessageReceived(messageState);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.logError(e);
        } finally {
            cleanup();
        }
    }

    public void sendGameState(GameSaveState gameState) throws IOException {
        objectOutputStream.writeObject(gameState);
        objectOutputStream.flush();
    }

    private void cleanup() {
        cleanClientHandler(objectInputStream, objectOutputStream, socket);
    }

    static void cleanClientHandler(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Socket socket) {
        closeSocket(objectInputStream, objectOutputStream, socket);
    }

    public void closeConnection() {
        closeSocket(objectInputStream, objectOutputStream, socket);
    }

    private static void closeSocket(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Socket socket) {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    public void sendMessageState(MessageState messageState) throws IOException {
        objectOutputStream.writeObject(messageState);
        objectOutputStream.flush();
    }
}
