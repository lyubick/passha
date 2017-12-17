/**
 *
 */
package org.kgbt.passha.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.text.TextAlignment;
import org.kgbt.passha.languages.Local.Texts;
import org.kgbt.passha.main.Exceptions;
import org.kgbt.passha.main.Terminator;
import org.kgbt.passha.ui.elements.Button;
import org.kgbt.passha.ui.elements.EntryField;
import org.kgbt.passha.ui.elements.LabeledItem;
import org.kgbt.passha.ui.elements.EntryField.TEXTFIELD;
import org.kgbt.passha.ui.elements.GridPane;
import org.kgbt.passha.ui.elements.Label;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.db.SpecialPassword;

/**
 * @author curious-odd-man
 *
 */
public class FormResetPwd extends AbstractForm
{
    private Button          b_ok               = null;
    private Button          b_cancel           = null;
    private Button          b_regenerate       = null;
    private Label           l_warning          = null;
    private Label           l_header           = null;
    private EntryField      ef_currentPassword = null;
    private EntryField      ef_newPassword     = null;

    private SpecialPassword newSp              = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return arg0 -> {
            try
            {
                VaultManager.getInstance().getActiveVault().replacePassword(newSp);
                close();
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }
        };
    }

    private EventHandler<ActionEvent> getOnCancelBtnAction()
    {
        return arg0 -> {
            newSp = null;
            close();
        };
    }

    private void showPassword()
    {
        try
        {
            SpecialPassword pwd = VaultManager.getSelectedPassword();
            ef_currentPassword.setText(pwd.getPassword());
            newSp = new SpecialPassword(pwd);
            newSp.changeCycles();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        ef_newPassword.setText(newSp.getPassword());
    }

    public FormResetPwd(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.FORM_RESETPWD_NAME, WindowPriority.ALWAYS_ON_TOP, false);

        // ========== BUTTONS ========== //

        b_ok = new Button(Texts.LABEL_OK);
        b_cancel = new Button(Texts.LABEL_CANCEL);
        b_regenerate = new Button("", Common.getRegenerateImage());

        ef_currentPassword = new EntryField(Texts.FORM_RESETWD_LABEL_CURRENT, TEXTFIELD.WIDTH.L);
        ef_newPassword = new EntryField(Texts.LABEL_NEW, TEXTFIELD.WIDTH.L);

        ef_currentPassword.setEditable(false);
        ef_newPassword.setEditable(false);

        // ========== LABELS ========== //
        l_warning = new Label(Texts.FORM_RESETPWD_MSG_WARNING.toString(), TEXTFIELD.WIDTH.XL);
        l_warning.setTextAlignment(TextAlignment.CENTER);
        l_warning.beError();

        l_header = new Label(Texts.FORM_RESETPWD_NAME.toString());
        l_header.beHeader();

        GridPane.setHalignment(l_warning, HPos.CENTER);
        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(ef_currentPassword, HPos.RIGHT);
        GridPane.setHalignment(ef_newPassword, HPos.RIGHT);
        GridPane.setHalignment(b_ok, HPos.LEFT);
        GridPane.setHalignment(b_cancel, HPos.RIGHT);
        GridPane.setHalignment(b_regenerate, HPos.RIGHT);

        grid.addHElement(l_header, 0, 2);
        grid.addHElement((LabeledItem) ef_currentPassword);
        grid.add(b_regenerate, 0);
        grid.addHElement((LabeledItem) ef_newPassword);
        grid.addHElement(l_warning, 0, 2);
        grid.addHElements(0, b_ok, b_cancel);

        b_ok.setOnAction(getOnOKBtnAction());
        b_cancel.setOnAction(getOnCancelBtnAction());
        b_regenerate.setOnAction(event -> showPassword());

        showPassword();

        open();
    }

    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
