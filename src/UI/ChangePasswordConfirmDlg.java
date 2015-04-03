/**
 *
 */
package UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Common.Exceptions;
import Languages.Texts.TextID;
import Main.ABEND;
import Main.PasswordCollection;
import Main.SpecialPassword;
import UI.Controller.FORMS;

/**
 * @author curious-odd-man
 *
 */
public class ChangePasswordConfirmDlg extends AbstractForm
{
    private Button          b_OK            = null;
    private Button          b_Cancel        = null;
    private Label           l_Warning       = null;
    private Label           l_Header        = null;
    private HBox            CurrentPassword = null;
    private HBox            NewPassword     = null;

    private SpecialPassword newSp           = null;

    ChangePasswordConfirmDlg()
    {
        // ========== LABELS ========== //
        l_Warning = new Label(TextID.CHANGE_PWD_WARNING.toString());
        l_Header = getLabel(TextID.CHANGE_PWD_HEADER.toString());

        // ========== BUTTONS ========== //

        b_OK = getButton(TextID.OK.toString());
        b_Cancel = getButton(TextID.CANCEL.toString());
        GridPane.setHalignment(b_OK, HPos.RIGHT);
        GridPane.setHalignment(b_Cancel, HPos.RIGHT);

        GridPane.setMargin(b_OK, new Insets(0, buttonXWidth + HGAP, 0, 0));

        grid.add(l_Header, 0, 0);

        CurrentPassword = getTextEntry("Current");
        NewPassword = getTextEntry("New");

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
                    ctrl.switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
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
                    ctrl.switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
                }
            }
        });
    }

    @Override
    public void draw(Stage stage) throws Exceptions
    {
        stage.setTitle(TextID.PROGRAM_NAME.toString() + " " + TextID.VERSION.toString());

        CurrentPassword.getEntryTextField().setText(
                PasswordCollection.getInstance().getSelected().getPassword());

        newSp = new SpecialPassword(PasswordCollection.getInstance().getSelected());

        NewPassword.getEntryTextField().setText(newSp.getPassword(null));

        stage.setResizable(false);
        stage.setScene(scene);

        stage.show();
    }
}
