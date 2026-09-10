package aster.gui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A dialog box showing one message beside the picture of whoever said it.
 *
 * <p>The layout is described in {@code /view/DialogBox.fxml}. A message from the user
 * sits on the right with the picture after it, while a reply from Aster is flipped so
 * the picture comes first on the left, which keeps the two sides of the conversation
 * easy to tell apart.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the message on the left and the picture on the right.
     *
     * @param text the message to show.
     * @param image the picture of whoever said it.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // The layout is packaged with Aster, so failing to load it is a fault in the
            // build rather than something the user can correct.
            throw new UncheckedIOException("Could not load the dialog box layout.", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Returns a dialog box for a message the user sent.
     *
     * @param text the message, exactly as the user typed it.
     * @param image the user's picture.
     * @return a dialog box with the picture on the right.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box for a reply from Aster.
     *
     * @param text the reply, with its lines separated by line breaks.
     * @param image Aster's picture.
     * @return a dialog box with the picture on the left.
     */
    public static DialogBox getAsterDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Moves the picture to the left of the message and aligns the box to the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
