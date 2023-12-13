package hr.gregl.gogame.game.utility;

import hr.gregl.gogame.game.model.GameMove;
import hr.gregl.gogame.game.xml.GameReplayXmlReader;
import hr.gregl.gogame.game.xml.GameReplayXmlWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GameReplayUtil {

    private static final String REPLAY_FILE_PATH = "src/main/java/hr/gregl/gogame/game/xml/replay.xml";
    private static GameReplayUtil instance;
    private final GameReplayXmlWriter replayWriter = new GameReplayXmlWriter();
    private final GameReplayXmlReader replayReader = new GameReplayXmlReader();

    private GameReplayUtil() {}

    public static GameReplayUtil getInstance() {
        if (instance == null) {
            instance = new GameReplayUtil();
        }
        return instance;
    }

    public void saveGameReplay(List<GameMove> gameMoves) {
        replayWriter.writeGameMovesToFile(gameMoves, REPLAY_FILE_PATH);
    }

    public void ensureReplayFileExists() {
        File file = new File(REPLAY_FILE_PATH);
        try {
            if (file.exists() && !file.delete()) {
                LogUtil.logWarning(LogUtil.warningLogMsg2 + REPLAY_FILE_PATH);
            }
            if (!file.createNewFile()) {
                LogUtil.logWarning(LogUtil.warningLogMsg3 + REPLAY_FILE_PATH);
            }
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    public List<GameMove> loadGameReplay() {
        return replayReader.readGameReplay(REPLAY_FILE_PATH);
    }
}
