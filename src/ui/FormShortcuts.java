package ui;

import java.util.Vector;

import core.Vault;
import core.VaultManager;
import ui.elements.EntryField;
import ui.elements.GridPane;
import ui.elements.LabeledItem;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.Label;
import languages.Local.Texts;
import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
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

    private ChangeListener<Boolean> getFocusedPropertyListner()
    {
        return new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
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
                    Vault vault = VaultManager.getInstance().getActiveVault();
                    vault.setSelected(vault.getPasswordByShortcut(keyEvent.getText().toLowerCase()));

                    FormVaultsManager.copyToClipboard();

                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                close();
            }
        };
    }

    private void fillFormWithPwds() throws Exceptions
    {
        Vector<SpecialPassword> tmp = VaultManager.getInstance().getActiveVault().getPasswordsWithShortcut();
        if (tmp.size() == 0) throw new Exceptions(XC.NO_SHORTCUTS_EXISTS);

        Label l_passwordName = new Label(Texts.FORM_MANAGEPWD_LABEL_PWD_NAME);
        Label l_shortcut = new Label(Texts.LABEL_SHORTCUT);

        GridPane.setHalignment(l_passwordName, HPos.CENTER);
        GridPane.setHalignment(l_shortcut, HPos.CENTER);

        grid.addHElements(0, l_passwordName, l_shortcut);

        for (SpecialPassword sp : tmp)
        {
            EntryField ef = new EntryField(sp.getName(), TEXTFIELD.WIDTH.XS);
            ef.setEditable(false);
            ef.setText(sp.getShortcut());
            GridPane.setHalignment(ef, HPos.CENTER);
            grid.addHElement((LabeledItem) ef);

            ef.getLabel().setAlignment(Pos.CENTER);
        }

        grid.getChildren().get(0).requestFocus(); // remove focus from ef
    }

    public FormShortcuts(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.LABEL_EMPTY, WindowPriority.NORMAL);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.focusedProperty().addListener(getFocusedPropertyListner());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, getOnKeyPressed());

        fillFormWithPwds();

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        open();
        Logger.printDebug("X: " + screen.getMaxX() + " Y: " + screen.getMaxY());
        Logger.printDebug("W: " + stage.getWidth() + " H: " + stage.getHeight());
        stage.setX(screen.getMaxX() - stage.getWidth());
        stage.setY(screen.getMaxY() - stage.getHeight());

        stage.requestFocus();
        stage.setAlwaysOnTop(true);
    }
}
