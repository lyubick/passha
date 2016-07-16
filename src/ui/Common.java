package ui;

import main.Main;
import ui.elements.EntryField;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public final class Common
{
    public static EventHandler<KeyEvent> getShortcutTFFiler(EntryField ef)
    {
        return new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                ef.beNormal();
                if (Character.isLetterOrDigit(keyEvent.getCharacter().charAt(0)))
                {
                    ef.setText(keyEvent.getCharacter().toLowerCase());
                }

                keyEvent.consume();
            }
        };
    }

    public static ImageView getRegenerateImage()
    {
        ImageView imgView = new ImageView(new Image(Main.class.getResourceAsStream("/resources/regenerate.png")));
        imgView.setStyle("-fx-background-color:transparent");

        return imgView;
    }

    public static ImageView getFindFolderImage()
    {
        ImageView imgView = new ImageView(new Image(Main.class.getResourceAsStream("/resources/find_folder.png")));
        imgView.setStyle("-fx-background-color:transparent");

        return imgView;
    }
}
