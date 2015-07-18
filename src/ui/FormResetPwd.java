/**
 *
 */
package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.text.TextAlignment;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;
import ui.elements.Button;
import ui.elements.EntryField;
import ui.elements.GridPane;
import ui.elements.Label;
import db.PasswordCollection;
import db.SpecialPassword;

/**
 * @author curious-odd-man
 *
 */
public class FormResetPwd extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 350;
        public static final int height = 250;
    }

    private final int MAX_WARNING_WIDTH = EntryField.LABEL_WIDTH + TEXTFIELD_WIDTH.L;

    private Button     b_OK            = null;
    private Button     b_Cancel        = null;
    private Label      l_Warning       = null;
    private Label      l_Header        = null;
    private EntryField CurrentPassword = null;
    private EntryField NewPassword     = null;

    private SpecialPassword newSp = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    PasswordCollection.getInstance().replacePasword(newSp);
                    close();
                }
                catch (Exceptions e)
                {
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
                newSp = null;
                close();
            }
        };
    }

    /* PUBLIC ROUTINE */
    public FormResetPwd(AbstractForm parent)
    {
        super(parent, TextID.FORM_RESETPWD_NAME.toString());
        priority = WindowPriority.ALWAYS_ON_TOP;

        stage.setWidth(WINDOW.width);
        stage.setHeight(WINDOW.height);

        // ========== LABELS ========== //
        l_Warning = new Label(TextID.FORM_RESETPWD_MSG_WARNING.toString(), MAX_WARNING_WIDTH);
        l_Warning.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(l_Warning, HPos.CENTER);

        l_Warning.beError();

        l_Header = new Label(TextID.FORM_RESETPWD_NAME.toString());
        GridPane.setHalignment(l_Header, HPos.CENTER);

        // ========== BUTTONS ========== //

        b_OK = new Button(TextID.COMMON_LABEL_OK.toString());
        b_Cancel = new Button(TextID.COMMON_LABEL_CANCEL.toString());

        GridPane.setHalignment(b_OK, HPos.LEFT);
        GridPane.setHalignment(b_Cancel, HPos.RIGHT);

        grid.add(l_Header, 0, 0);

        CurrentPassword = new EntryField(TextID.FORM_RESETWD_LABEL_CURRENT, TEXTFIELD_WIDTH.L);
        NewPassword = new EntryField(TextID.COMMON_LABEL_NEW, TEXTFIELD_WIDTH.L);

        CurrentPassword.setEditable(false);
        NewPassword.setEditable(false);

        grid.addRow(1, CurrentPassword.getHBoxed());
        grid.addRow(2, NewPassword.getHBoxed());

        grid.addRow(3, l_Warning);
        grid.add(b_OK, 0, 4);
        grid.add(b_Cancel, 0, 4);

        b_OK.setOnAction(getOnOKBtnAction());
        b_Cancel.setOnAction(getOnCancelBtnAction());

        try
        {
            CurrentPassword.setText(PasswordCollection.getInstance().getSelected().getPassword());
            newSp = new SpecialPassword(PasswordCollection.getInstance().getSelected());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        NewPassword.setText(newSp.getPassword());

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        open();
    }

    /* OVERRIDE */
    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
