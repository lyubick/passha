package org.kgbt.passha.ui;

import org.kgbt.passha.main.Main;
import org.kgbt.passha.ui.elements.EntryField;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public final class Common
{
    public static EventHandler<KeyEvent> getShortcutTFFiler(EntryField ef)
    {
        return keyEvent -> {
            ef.beNormal();
            if (Character.isLetterOrDigit(keyEvent.getCharacter().charAt(0)))
            {
                ef.setText(keyEvent.getCharacter().toLowerCase());
            }

            keyEvent.consume();
        };
    }

    public static ImageView getRegenerateImage()
    {
        ImageView imgView = new ImageView(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("regenerate.png")));
        imgView.setStyle("-fx-background-color:transparent");

        return imgView;
    }

    public static ImageView getFindFolderImage()
    {
        ImageView imgView = new ImageView(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("find_folder.png")));
        imgView.setStyle("-fx-background-color:transparent");

        return imgView;
    }
}
