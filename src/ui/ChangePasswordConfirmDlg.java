/**
 *
 */
package ui;

import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;
import ui.Controller.FORMS;
import db.PasswordCollection;
import db.SpecialPassword;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

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

    private final int       MAX_WARNING_WIDTH = LABEL_WIDTH + FIELD_WIDTH_PWD;

    private Button          b_OK              = null;
    private Button          b_Cancel          = null;
    private Label           l_Warning         = null;
    private Label           l_Header          = null;
    private HBox            CurrentPassword   = null;
    private HBox            NewPassword       = null;

    private SpecialPassword newSp             = null;

    ChangePasswordConfirmDlg()
    {
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
        GridPane.setMargin(b_OK, new Insets(0, BUTTON_X_WIDTH + HGAP, 0, 0));

        grid.add(l_Header, 0, 0);

        CurrentPassword = getTextEntry(TextID.FORM_PWD_CHANGE_LABEL_CURRENT.toString(), FIELD_WIDTH_PWD);
        NewPassword = getTextEntry(TextID.FORM_PWD_CHANGE_LABEL_NEW.toString(), FIELD_WIDTH_PWD);

        CurrentPassword.getEntryTextField().setEditable(false);
        NewPassword.getEntryTextField().setEditable(false);

        grid.add(CurrentPassword, 0, 1);
        grid.add(NewPassword, 0, 2);

        grid.add(l_Warning, 0, 3);
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
                    Controller.getInstance().switchForm(FORMS.MANAGE_PWDS);
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
                    Controller.getInstance().switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });
    }

    @Override
    public void draw(Stage stage) throws Exceptions
    {
        stage.setScene(scene);

        stage.setTitle(TextID.COMMON_LABEL_APP_NAME.toString() + " " + TextID.COMMON_LABEL_VERSION.toString());

        CurrentPassword.getEntryTextField().setText(PasswordCollection.getInstance().getSelected().getPassword());

        newSp = new SpecialPassword(PasswordCollection.getInstance().getSelected());

        NewPassword.getEntryTextField().setText(newSp.getPassword());

        stage.setResizable(false);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.show();
    }
}
