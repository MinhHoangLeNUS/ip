package aster.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Tests the layouts the chat window is built from.
 *
 * <p>The layouts are read as plain XML, so nothing here starts JavaFX. That checks what the
 * layouts declare, such as sizes and the controls the window's code relies on, without a
 * display; how the window actually looks is checked by hand.
 */
class MainWindowLayoutTest {
    private static final String FXML_NAMESPACE = "http://javafx.com/fxml/1";

    @Test
    void dialogLayout_picture_is32PixelsAndSmooth() throws IOException, ParserConfigurationException, SAXException {
        Element picture = findById(readLayout("/view/DialogBox.fxml"), "displayPicture");

        assertEquals("ImageView", picture.getTagName());
        assertEquals("32.0", picture.getAttribute("fitWidth"), "the picture must be shown 32 pixels wide");
        assertEquals("32.0", picture.getAttribute("fitHeight"), "the picture must be shown 32 pixels high");
        assertEquals("true", picture.getAttribute("smooth"), "the picture must be scaled smoothly");
    }

    /**
     * Returns a layout on the class path, parsed as XML.
     *
     * @param path the layout's path on the class path.
     * @return the parsed layout.
     */
    private static Document readLayout(String path) throws IOException, ParserConfigurationException, SAXException {
        URL layout = MainWindowLayoutTest.class.getResource(path);
        assertNotNull(layout, "the layout must be on the class path: " + path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try (InputStream in = layout.openStream()) {
            return factory.newDocumentBuilder().parse(in);
        }
    }

    /**
     * Returns the element a layout gives the given {@code fx:id}.
     *
     * @param layout the parsed layout.
     * @param id the {@code fx:id} to look for.
     * @return the element with that {@code fx:id}.
     */
    private static Element findById(Document layout, String id) {
        NodeList elements = layout.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (id.equals(element.getAttributeNS(FXML_NAMESPACE, "id"))) {
                return element;
            }
        }
        throw new AssertionError("the layout has no element with fx:id " + id);
    }
}
