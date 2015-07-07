/**
 *
 */
package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;
import ui.elements.EntryField;
import ui.elements.GridPane;
import ui.elements.Label;
import db.PasswordCollection;
import db.SpecialPassword;

/**
 * @author curious-odd-man
 *
 */
public class ChangePasswordConfirmDlg extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 350;
        public static final int height = 250;
    }

    private final int       MAX_WARNING_WIDTH = EntryField.LABEL_WIDTH + FIELD_WIDTH.L;

    private Button          b_OK              = null;
    private Button          b_Cancel          = null;
    private Label           l_Warning         = null;
    private Label           l_Header          = null;
    private EntryField      CurrentPassword   = null;
    private EntryField      NewPassword       = null;

    private SpecialPassword newSp             = null;

    ChangePasswordConfirmDlg(AbstractForm parent)
    {
        super(parent);

        // ========== LABELS ========== //
        l_Warning = new Label(TextID.FORM_PWD_CHANGE_WARNING.toString(), MAX_WARNING_WIDTH);
        l_Warning.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(l_Warning, HPos.CENTER);

        l_Warning.beError();

        l_Header = new Label(TextID.FORM_PWD_CHANGE_NAME.toString());
        GridPane.setHalignment(l_Header, HPos.CENTER);

        // ========== BUTTONS ========== //

        b_OK = getButton(TextID.COMMON_LABEL_OK.toString());
        b_Cancel = getButton(TextID.COMMON_LABEL_CANCEL.toString());

        GridPane.setHalignment(b_OK, HPos.RIGHT);
        GridPane.setHalignment(b_Cancel, HPos.RIGHT);
        GridPane.setMargin(b_OK, new Insets(0, BUTTON.xWidth + GAP.H, 0, 0));

        grid.add(l_Header, 0, 0);

        CurrentPassword = new EntryField(TextID.FORM_PWD_CHANGE_LABEL_CURRENT.toString(), FIELD_WIDTH.L);
        NewPassword = new EntryField(TextID.FORM_PWD_CHANGE_LABEL_NEW.toString(), FIELD_WIDTH.L);

        CurrentPassword.setEditable(false);
        NewPassword.setEditable(false);

        grid.addRow(1, CurrentPassword.getHBoxed());
        grid.addRow(2, NewPassword.getHBoxed());

        grid.addRow(3, l_Warning);
        grid.add(b_OK, 0, 4);
        grid.add(b_Cancel, 0, 4);

        b_OK.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    PasswordCollection.getInstance().replacePasword(newSp);
                    hide();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        b_Cancel.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    newSp = null;
                    hide();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });
    }

    @Override
    public void onUserCloseRequest() throws Exceptions
    {
        // TODO Auto-generated method stub
        hide();
    }

    @Override
    public void hide() throws Exceptions
    {
        // TODO Auto-generated method stub
        stage.hide();
    }

    @Override
    public void show() throws Exceptions
    {
        // TODO Auto-generated method stub
        stage.setTitle(TextID.COMMON_LABEL_APP_NAME.toString() + " " + TextID.COMMON_LABEL_VERSION.toString());

        CurrentPassword.setText(PasswordCollection.getInstance().getSelected().getPassword());

        newSp = new SpecialPassword(PasswordCollection.getInstance().getSelected());

        NewPassword.setText(newSp.getPassword());

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.show();
    }
}
