package ui;

import java.util.Vector;
import ui.elements.EntryField;
import ui.elements.Label;
import languages.Texts.TextID;
import main.Exceptions;
import main.Exceptions.XC;
import db.PasswordCollection;
import db.SpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import main.Terminator;

public class FormShortcuts extends AbstractForm
{

    private final class WINDOW
    {
        public static final int width = 200;
    }

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private ChangeListener<Boolean> getFocusedPropertyListner()
    {
        return new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                if (oldValue) close();
            }
        };
    }

    private EventHandler<KeyEvent> getOnKeyPressed()
    {
        return new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                try
                {
                    PasswordCollection.getInstance().setSelected(PasswordCollection.getInstance()
                            .getPasswordByShortcut(keyEvent.getText()));

                    FormManagePwd.copyToClipboard();

                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                close();
            }
        };
    }

    /* PRIVATE ROUTINE */
    private void fillFormWithPwds() throws Exceptions
    {
        Vector<SpecialPassword> tmp = PasswordCollection.getInstance().getPasswordsWithShortcut();
        if (tmp.size() == 0) throw new Exceptions(XC.NO_SHORTCUTS_EXISTS);

        grid.add(new Label(TextID.FORM_MANAGEPWD_LABEL_PWD_NAME.toString()), 0, 0);
        grid.add(new Label(TextID.FORM_EDITPWD_LABEL_SHORTCUT.toString()), 1, 0);

        grid.setNextLine(1);
        for (SpecialPassword sp : tmp)
        {
            EntryField ef = new EntryField(sp.getName(), 25);
            ef.setEditable(false);
            ef.setText(sp.getShortcut());
            grid.addNextEntryField(ef);
        }

        stage.setHeight((tmp.size() + 1) * 40);

        grid.getChildren().get(0).requestFocus(); // remove focus from ef
    }

    /* PUBLIC ROUTINE */
    public FormShortcuts(AbstractForm parent) throws Exceptions
    {
        // TODO: background
        super(parent, "");
        stage.initStyle(StageStyle.UNDECORATED);

        stage.focusedProperty().addListener(getFocusedPropertyListner());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, getOnKeyPressed());

        fillFormWithPwds();

        stage.setWidth(WINDOW.width);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(
                primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - stage.getWidth());
        stage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight()
                - stage.getHeight());

        open();

        stage.requestFocus();
        stage.setAlwaysOnTop(true);
    }
}
