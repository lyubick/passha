package ui;

import db.PasswordCollection;
import db.SpecialPassword;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import ui.elements.Button;
import ui.elements.EntryField;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.Label;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;

public class FormEditPwd extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 600;
        public static final int height = 250;
    }

    private EntryField      ef_pwdName   = null;
    private EntryField      ef_comment   = null;
    private EntryField      ef_url       = null;
    private EntryField      ef_shortcut  = null;
    private Button          b_OK         = null;
    private SpecialPassword pwd          = null;
    private final Label     l_errorLabel = new Label("");

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
                    SpecialPassword sp =
                            PasswordCollection.getInstance().getPasswordByShortcut(
                                    ef_shortcut.getText());
                    if (sp == null || ef_shortcut.getText().equals("")
                            || sp.getName().equals(pwd.getName()))
                    {
                        pwd.setAllOptionalFields(ef_comment.getText(), ef_url.getText(),
                                ef_shortcut.getText());
                        PasswordCollection.getInstance().replacePasword(pwd);
                        close();
                    }
                    else
                    {
                        l_errorLabel.setText(TextID.FORM_EDITPWD_MSG_SHORTCUT_IN_USE.toString()
                                + " " + sp.getName());
                        l_errorLabel.beError();
                    }
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

            }
        };
    }

    /* PUBLIC ROUTINE */
    public FormEditPwd(AbstractForm parent) throws Exceptions
    {
        super(parent, TextID.FORM_EDITPWD_NAME.toString());

        priority = WindowPriority.ALWAYS_ON_TOP;

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        pwd = PasswordCollection.getInstance().getSelected();

        ef_pwdName = new EntryField(TextID.FORM_MANAGEPWD_LABEL_PWD_NAME, TEXTFIELD.WIDTH.XXL);
        ef_comment = new EntryField(TextID.FORM_CREATEPWD_LABEL_COMMENT, TEXTFIELD.WIDTH.XXL);
        ef_url = new EntryField(TextID.FORM_CREATEPWD_LABEL_URL, TEXTFIELD.WIDTH.XXL);
        ef_shortcut = new EntryField(TextID.FORM_EDITPWD_LABEL_SHORTCUT, TEXTFIELD.WIDTH.XS);

        b_OK = new Button(TextID.COMMON_LABEL_OK.toString());

        ef_pwdName.setEditable(false);
        ef_shortcut.addEventFilter(KeyEvent.KEY_TYPED,
                CommonEventHandlers.getShortcutTFFiler(ef_shortcut));

        ef_pwdName.setText(pwd.getName());
        ef_comment.setText(pwd.getComment());
        ef_url.setText(pwd.getUrl());
        ef_shortcut.setText(pwd.getShortcut());

        grid.addHElement(ef_pwdName);
        grid.addHElement(ef_comment);
        grid.addHElement(ef_url);
        grid.addHElement(ef_shortcut);
        grid.addHElement(l_errorLabel, 1);
        grid.addHElement(b_OK, 1);

        b_OK.setOnAction(getOnOKBtnAction());

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
