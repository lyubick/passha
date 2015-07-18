package ui;

import db.PasswordCollection;
import db.SpecialPassword;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import ui.elements.Button;
import ui.elements.EntryField;
import ui.elements.Label;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;

public class FormEditPwd extends AbstractForm
{
    private final int TEXT_FIELDS_WIDTH = 350;

    private final class WINDOW
    {
        public static final int width  = 600;
        public static final int height = 400;
    }

    private EntryField      ef_PwdName   = null;
    private EntryField      ef_Shortcut  = null;
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
                    SpecialPassword sp = PasswordCollection.getInstance()
                            .getPasswordByShortcut(ef_Shortcut.getText());
                    if (sp == null)
                    {
                        pwd.setShortcut(ef_Shortcut.getText());
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

    private EventHandler<KeyEvent> getShortcutTFFiler()
    {
        return new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                if ("0123456789".contains(keyEvent.getCharacter()))
                    ef_Shortcut.setText(keyEvent.getCharacter());
                keyEvent.consume();
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

        ef_PwdName = new EntryField(TextID.FORM_MANAGEPWD_LABEL_PWD_NAME, TEXT_FIELDS_WIDTH);
        ef_Shortcut = new EntryField(TextID.FORM_EDITPWD_LABEL_SHORTCUT, 25);
        b_OK = new Button(TextID.COMMON_LABEL_OK.toString());

        ef_PwdName.setEditable(false);
        ef_Shortcut.addEventFilter(KeyEvent.KEY_TYPED, getShortcutTFFiler());

        ef_PwdName.setText(pwd.getName());
        ef_Shortcut.setText(pwd.getShortcut());

        grid.addHElement(ef_PwdName);
        grid.addHElement(ef_Shortcut);
        grid.add(l_errorLabel, 1, 2);
        grid.add(b_OK, 1, 3);

        b_OK.setOnAction(getOnOKBtnAction());

        open();
        ef_Shortcut.requestFocus();
    }

    /* OVERRIDE */
    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
