package hr.gregl.gogame.game.xml;

import hr.gregl.gogame.game.model.GameMove;
import hr.gregl.gogame.game.utility.LogUtil;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameReplayXmlReader {

    public List<GameMove> readGameReplay(String filePath) {
        try {
            File inputFile = new File(filePath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GameMoveHandler handler = new GameMoveHandler();
            saxParser.parse(inputFile, handler);
            return handler.getGameMoves();
        } catch (Exception e) {
            LogUtil.logError(e);
            return new ArrayList<>();
        }
    }

    private static class GameMoveHandler extends DefaultHandler {

        private List<GameMove> gameMoves;
        private GameMove gameMove;
        private StringBuilder elementValue;

        @Override
        public void startDocument() {
            gameMoves = new ArrayList<>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            elementValue = new StringBuilder();
            if (qName.equalsIgnoreCase("move")) {
                gameMove = new GameMove();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            elementValue.append(new String(ch, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            switch (qName.toLowerCase()) {
                case "move":
                    gameMoves.add(gameMove);
                    break;
                case "player":
                    gameMove.setPlayer(Integer.parseInt(elementValue.toString()));
                    break;
                case "row":
                    gameMove.setRow(Integer.parseInt(elementValue.toString()));
                    break;
                case "column":
                    gameMove.setColumn(Integer.parseInt(elementValue.toString()));
                    break;
                case "timestamp":
                    gameMove.setTimestamp(elementValue.toString());
                    break;
            }
        }

        public List<GameMove> getGameMoves() {
            return gameMoves;
        }
    }
}
