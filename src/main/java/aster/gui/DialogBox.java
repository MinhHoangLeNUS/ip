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
 *
 * <p>Each dialog box also carries exactly one style class saying whose message it is, or
 * that it is a reply containing an error, so the stylesheet can show each kind its own
 * way. A reply containing an error sits on Aster's side like any other reply.
 */
public class DialogBox extends HBox {
    // Shared with the stylesheet tests, which is why these are not private.
    static final String USER_STYLE_CLASS = "user-dialog";
    static final String ASTER_STYLE_CLASS = "aster-dialog";
    static final String ERROR_STYLE_CLASS = "error-dialog";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the message on the left and the picture on the right.
     *
     * @param text the message to show.
     * @param image the picture of whoever said it.
     * @param kindStyleClass the one style class saying what kind of message this is.
     */
    private DialogBox(String text, Image image, String kindStyleClass) {
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
        getStyleClass().add(kindStyleClass);
    }

    /**
     * Returns a dialog box for a message the user sent.
     *
     * @param text the message, exactly as the user typed it.
     * @param image the user's picture.
     * @return a dialog box with the picture on the right.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, USER_STYLE_CLASS);
    }

    /**
     * Returns a dialog box for a reply from Aster.
     *
     * @param text the reply, with its lines separated by line breaks.
     * @param image Aster's picture.
     * @return a dialog box with the picture on the left.
     */
    public static DialogBox getAsterDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, ASTER_STYLE_CLASS);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Returns a dialog box for a reply from Aster that contains an error.
     *
     * <p>It sits on Aster's side like any other reply, and is styled so that it stands out.
     *
     * @param text the reply, with its lines separated by line breaks.
     * @param image Aster's picture.
     * @return a dialog box with the picture on the left, marked as containing an error.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, ERROR_STYLE_CLASS);
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
