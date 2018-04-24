package org.kgbt.passha.desktop.ui;

import java.awt.TrayIcon.MessageType;
import java.util.Vector;

import org.kgbt.passha.core.db.Vault;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.desktop.ui.elements.EntryField;
import org.kgbt.passha.desktop.ui.elements.GridPane;
import org.kgbt.passha.desktop.ui.elements.LabeledItem;
import org.kgbt.passha.desktop.ui.elements.EntryField.TEXTFIELD;
import org.kgbt.passha.desktop.ui.elements.Label;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.db.SpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import org.kgbt.passha.core.common.Terminator;

public class FormShortcuts extends AbstractForm
{

    private ChangeListener<Boolean> getFocusedPropertyListner()
    {
        return (observable, oldValue, newValue) -> {
            if (oldValue) close();
        };
    }

    private EventHandler<KeyEvent> getOnKeyPressed()
    {
        return keyEvent -> {
            Logger.printDebug(
                "Key pressed: '" + keyEvent.getText().toLowerCase() + "' keyCode = " + keyEvent.getCode());

            switch (keyEvent.getCode())
            {
                case ESCAPE:
                    close();
                break;

                case TAB:
                    try
                    {
                        // ignore TAB - no other vaults to switch to
                        if (VaultManager.getInstance().size() == 1)
                        {
                            keyEvent.consume();
                            return;
                        }

                        VaultManager.getInstance().activateNextVault();
                        close();
                        new FormShortcuts(parent);
                    }
                    catch (Exceptions e)
                    {
                        try
                        {
                            if (e.getCode() == XC.VAULTS_NOT_FOUND)
                                TrayAgent.getInstance().showNotification(Texts.MSG_VAULTS_MISSING,
                                    Texts.MSG_VAULTS_MISSING_ACTION, MessageType.WARNING);
                            else
                                Terminator.terminate(e);
                        }
                        catch (Exceptions e1)
                        {
                            Terminator.terminate(e1);
                        }
                    }
                    return;

                default:
                break;
            }

            if (keyEvent.getText().isEmpty())
            {
                keyEvent.consume();
                return;
            }

            try
            {
                Vault vault = VaultManager.getInstance().getActiveVault();
                vault.setSelected(vault.getPasswordByShortcut(keyEvent.getText().toLowerCase()));

                if (vault.getSelected() == null)
                {
                    Logger.printError("No password with this shortcut '" + keyEvent.getText().toLowerCase()
                        + "' in " + vault.getName());
                    keyEvent.consume();
                    return;
                }

                FormVaultsManager.copyToClipboard();

            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }

            close();
        };

    }

    private void fillFormWithPwds() throws Exceptions
    {
        Vault vault = VaultManager.getInstance().getActiveVault();
        if (vault == null) vault = VaultManager.getInstance().activateNextVault();  // throws if no vaults open

        String vaultName = VaultManager.getInstance().getActiveVault().getName();
        if (vaultName.isEmpty()) vaultName = Texts.LABEL_UNNAMED.toString().toUpperCase();
        Label l_vaultName = new Label(Texts.LABEL_VAULT_WITH_COLLS.toString() + vaultName);
        l_vaultName.beHeader();

        Label l_passwordName = new Label(Texts.FORM_MANAGEPWD_LABEL_PWD_NAME);
        Label l_shortcut = new Label(Texts.LABEL_SHORTCUT);

        GridPane.setHalignment(l_vaultName, HPos.CENTER);
        GridPane.setHalignment(l_passwordName, HPos.CENTER);
        GridPane.setHalignment(l_shortcut, HPos.CENTER);

        grid.addHElement(l_vaultName, 0, 2);

        Vector<SpecialPassword> passwordsWithShortcuts = vault.getPasswordsWithShortcut();
        if (passwordsWithShortcuts.size() == 0)
        {
            Label l_error = new Label(Texts.MSG_SHORTCUTS_MISSING);
            l_error.beError();
            GridPane.setHalignment(l_error, HPos.CENTER);
            grid.addHElement(l_error, 0, 2);
            return;
        }

        grid.addHElements(0, l_passwordName, l_shortcut);

        passwordsWithShortcuts.stream().map(sp ->
        {
            EntryField ef = new EntryField(sp.getName(), TEXTFIELD.WIDTH.XS);
            ef.setEditable(false);
            ef.setText(sp.getShortcut());
            return ef;
        }).forEach(ef ->
        {
            GridPane.setHalignment(ef, HPos.CENTER);
            ef.getLabel().setAlignment(Pos.CENTER);
            grid.addHElement((LabeledItem) ef);
        });

        grid.getChildren().get(0).requestFocus(); // Remove focus from EntryField
    }

    public FormShortcuts(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.LABEL_EMPTY, WindowPriority.NORMAL, false);
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
