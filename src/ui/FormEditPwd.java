package ui;

import db.PasswordCollection;
import db.SpecialPassword;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import ui.elements.Button;
import ui.elements.EntryField;
import ui.elements.Label;
import ui.elements.LabeledItem;
import ui.elements.EntryField.TEXTFIELD;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;

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
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    pwd.setAllOptionalFields(ef_comment.getText(), ef_url.getText(),
                            ef_shortcut.getText());
                    PasswordCollection.getInstance().replacePasword(pwd);
                    close();
                }
                catch (Exceptions e)
                {
                    if (e.getCode() == XC.PASSWORD_SHORTCUT_ALREADY_IN_USE)
                    {
                        ef_errorLabel.setVisible(true);
                        ef_errorLabel.setText(e.getText());
                        return;
                    }
                    Terminator.terminate(e);
                }

            }
        };
    }

    private EventHandler<ActionEvent> getOnCancelBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                close();
            }
        };
    }

    /* PUBLIC ROUTINE */
    public FormEditPwd(AbstractForm parent) throws Exceptions
    {
        super(parent, TextID.FORM_EDITPWD_NAME.toString());

        priority = WindowPriority.ALWAYS_ON_TOP;

        l_header = new Label(TextID.FORM_EDITPWD_LABEL_HEADER);
        l_header.beHeader();
        pwd = PasswordCollection.getInstance().getSelected();

        ef_pwdName = new EntryField(TextID.FORM_MANAGEPWD_LABEL_PWD_NAME, TEXTFIELD.WIDTH.XXL);
        ef_comment = new EntryField(TextID.FORM_CREATEPWD_LABEL_COMMENT, TEXTFIELD.WIDTH.XXL);
        ef_url = new EntryField(TextID.FORM_CREATEPWD_LABEL_URL, TEXTFIELD.WIDTH.XXL);
        ef_shortcut = new EntryField(TextID.FORM_EDITPWD_LABEL_SHORTCUT, TEXTFIELD.WIDTH.XS);
        ef_errorLabel =
                new EntryField(TextID.FORM_EDITPWD_MSG_SHORTCUT_IN_USE, TEXTFIELD.WIDTH.XXL);
        ef_errorLabel.setEditable(false);
        ef_errorLabel.beError();
        ef_errorLabel.setVisible(false);

        b_ok = new Button(TextID.COMMON_LABEL_OK.toString());
        b_cancel = new Button(TextID.COMMON_LABEL_CANCEL.toString());

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
        b_cancel.setOnAction(getOnCancelBtnAction());

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
