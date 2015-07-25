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
import ui.elements.EntryField.TEXTFIELD;
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
    private Button     b_ok               = null;
    private Button     b_cancel           = null;
    private Label      l_warning          = null;
    private Label      l_header           = null;
    private EntryField ef_currentPassword = null;
    private EntryField ef_newPassword     = null;

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

        // ========== LABELS ========== //
        l_warning = new Label(TextID.FORM_RESETPWD_MSG_WARNING.toString(), TEXTFIELD.WIDTH.XXL);
        l_warning.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(l_warning, HPos.CENTER);

        l_warning.beError();

        l_header = new Label(TextID.FORM_RESETPWD_NAME.toString());
        GridPane.setHalignment(l_header, HPos.CENTER);

        // ========== BUTTONS ========== //

        b_ok = new Button(TextID.COMMON_LABEL_OK.toString());
        b_cancel = new Button(TextID.COMMON_LABEL_CANCEL.toString());

        GridPane.setHalignment(b_ok, HPos.LEFT);
        GridPane.setHalignment(b_cancel, HPos.RIGHT);

        grid.add(l_header, 0);

        ef_currentPassword = new EntryField(TextID.FORM_RESETWD_LABEL_CURRENT, TEXTFIELD.WIDTH.L);
        ef_newPassword = new EntryField(TextID.COMMON_LABEL_NEW, TEXTFIELD.WIDTH.L);

        ef_currentPassword.setEditable(false);
        ef_newPassword.setEditable(false);

        grid.addRow(1, ef_currentPassword.getHBoxed());
        grid.addRow(2, ef_newPassword.getHBoxed());

        grid.addRow(3, l_warning);
        grid.add(b_ok, 0, 4);
        grid.add(b_cancel, 0, 4);

        b_ok.setOnAction(getOnOKBtnAction());
        b_cancel.setOnAction(getOnCancelBtnAction());

        try
        {
            ef_currentPassword
                    .setText(PasswordCollection.getInstance().getSelected().getPassword());
            newSp = new SpecialPassword(PasswordCollection.getInstance().getSelected());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        ef_newPassword.setText(newSp.getPassword());

        autoSize();
        open();
    }

    /* OVERRIDE */
    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
