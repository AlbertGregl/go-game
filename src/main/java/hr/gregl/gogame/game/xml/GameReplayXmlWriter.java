package hr.gregl.gogame.game.xml;

import hr.gregl.gogame.game.model.GameMove;
import hr.gregl.gogame.game.utility.LogUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
public class GameReplayXmlWriter {

    public void writeGameMovesToFile(List<GameMove> gameMoves, String filePath) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element root = document.createElement("gameReplay");
            document.appendChild(root);

            for (GameMove move : gameMoves) {
                Element moveElement = document.createElement("move");

                moveElement.appendChild(createElementWithValue(document, "player", String.valueOf(move.getPlayer())));
                moveElement.appendChild(createElementWithValue(document, "row", String.valueOf(move.getRow())));
                moveElement.appendChild(createElementWithValue(document, "column", String.valueOf(move.getColumn())));
                moveElement.appendChild(createElementWithValue(document, "timestamp", move.getTimestamp().toString()));

                root.appendChild(moveElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));

            transformer.transform(domSource, streamResult);

        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }

    private Element createElementWithValue(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.appendChild(document.createTextNode(value));
        return element;
    }
}
