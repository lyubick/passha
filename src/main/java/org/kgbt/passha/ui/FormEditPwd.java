package org.kgbt.passha.ui;

import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.db.SpecialPassword;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.kgbt.passha.ui.elements.Button;
import org.kgbt.passha.ui.elements.EntryField;
import org.kgbt.passha.ui.elements.Label;
import org.kgbt.passha.ui.elements.LabeledItem;
import org.kgbt.passha.ui.elements.EntryField.TEXTFIELD;
import org.kgbt.passha.languages.Local.Texts;
import org.kgbt.passha.main.Exceptions;
import org.kgbt.passha.main.Terminator;
import org.kgbt.passha.main.Exceptions.XC;

public class FormEditPwd extends AbstractForm
{
    private EntryField      ef_pwdName    = null;
    private EntryField      ef_comment    = null;
    private EntryField      ef_url        = null;
    private EntryField      ef_shortcut   = null;
    private Button          b_ok          = null;
    private Button          b_cancel      = null;
    private SpecialPassword pwd           = null;
    private EntryField      ef_errorLabel = null;
    private Label           l_header      = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return event -> {
            try
            {
                pwd.setAllOptionalFields(ef_comment.getText(), ef_url.getText(), ef_shortcut.getText());
                VaultManager.getInstance().getActiveVault().replacePassword(pwd);
                close();
            }
            catch (Exceptions e)
            {
                if (e.getCode() == XC.PASSWORD_SHORTCUT_IN_USE)
                {
                    ef_errorLabel.setVisible(true);
                    ef_errorLabel.setText(((SpecialPassword) e.getObject()).getName());
                    return;
                }
                Terminator.terminate(e);
            }

        };
    }

    /* PUBLIC ROUTINE */
    public FormEditPwd(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.FORM_EDITPWD_NAME, WindowPriority.ALWAYS_ON_TOP, false);

        l_header = new Label(Texts.FORM_EDITPWD_LABEL_HEADER);
        l_header.beHeader();
        pwd = new SpecialPassword(VaultManager.getSelectedPassword());

        ef_pwdName = new EntryField(Texts.FORM_MANAGEPWD_LABEL_PWD_NAME, TEXTFIELD.WIDTH.XXL);
        ef_comment = new EntryField(Texts.LABEL_COMMENT, TEXTFIELD.WIDTH.XXL);
        ef_url = new EntryField(Texts.LABEL_URL, TEXTFIELD.WIDTH.XXL);
        ef_shortcut = new EntryField(Texts.LABEL_SHORTCUT, TEXTFIELD.WIDTH.XS);
        ef_errorLabel = new EntryField(Texts.MSG_SHORTCUT_IN_USE_BY, TEXTFIELD.WIDTH.XXL);
        ef_errorLabel.setEditable(false);
        ef_errorLabel.beError();
        ef_errorLabel.setVisible(false);

        b_ok = new Button(Texts.LABEL_OK.toString());
        b_cancel = new Button(Texts.LABEL_CANCEL.toString());

        ef_pwdName.setEditable(false);
        ef_shortcut.addEventFilter(KeyEvent.KEY_TYPED, Common.getShortcutTFFiler(ef_shortcut));

        ef_pwdName.setText(pwd.getName());
        ef_comment.setText(pwd.getComment());
        ef_url.setText(pwd.getUrl());
        ef_shortcut.setText(pwd.getShortcut());

        grid.addHElement(l_header, 0, 2);
        grid.addHElement((LabeledItem) ef_pwdName);
        grid.addHElement((LabeledItem) ef_comment);
        grid.addHElement((LabeledItem) ef_url);
        grid.addHElement((LabeledItem) ef_shortcut);
        grid.addHElement((LabeledItem) ef_errorLabel);
        grid.addAll(1, b_ok, b_cancel);

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(b_cancel, HPos.RIGHT);

        b_ok.setOnAction(getOnOKBtnAction());
        b_cancel.setOnAction(event -> close());

        open();
        ef_shortcut.requestFocus();
    }

    /* OVERRIDE */
    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
