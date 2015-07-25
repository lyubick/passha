package ui;

import java.util.Vector;

import ui.elements.EntryField;
import ui.elements.GridPane;
import ui.elements.LabeledItem;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.Label;
import languages.Texts.TextID;
import main.Exceptions;
import main.Exceptions.XC;
import db.PasswordCollection;
import db.SpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import main.Terminator;

public class FormShortcuts extends AbstractForm
{

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
                    PasswordCollection.getInstance().setSelected(
                            PasswordCollection.getInstance().getPasswordByShortcut(
                                    keyEvent.getText()));

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

        Label l_passwordName = new Label(TextID.FORM_MANAGEPWD_LABEL_PWD_NAME);
        Label l_shortcut = new Label(TextID.FORM_EDITPWD_LABEL_SHORTCUT);

        GridPane.setHalignment(l_passwordName, HPos.CENTER);
        GridPane.setHalignment(l_shortcut, HPos.CENTER);

        grid.addHElements(0, l_passwordName, l_shortcut);

        for (SpecialPassword sp : tmp)
        {
            EntryField ef = new EntryField(sp.getName(), TEXTFIELD.WIDTH.XS);
            ef.setEditable(false);
            ef.setText(sp.getShortcut());
            grid.addHElement((LabeledItem) ef);

            ef.getLabel().setAlignment(Pos.CENTER);
        }

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

        autoSize();

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        stage.setX(screen.getMaxX() - grid.getMinWidth());
        stage.setY(screen.getMaxY() - grid.getMinHeight());

        open();

        stage.requestFocus();
        stage.setAlwaysOnTop(true);
    }
}
