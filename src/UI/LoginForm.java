/**
 *
 */
package UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Common.Exceptions;
import Common.Exceptions.XC;
import Common.Return.RC;
import CryptoSystem.CryptoSystem;
import Languages.Texts.TextID;
import Logger.Logger;
import Main.PasswordCollection;
import UI.Controller.FORMS;

/**
 * @author lyubick
 *
 */
public class LoginForm extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 300;
        public static final int height = 200;
    }

    private Label         l_Header           = null;
    private Label         l_Warning          = null;
    private PasswordField pf_Password        = null;
    private PasswordField pf_PasswordConfirm = null;
    private Button        b_Login            = null;
    private Button        b_Register         = null;

    private String        prevPass           = "";

    int                   currPos            = 0;

    LoginForm()
    {
        l_Header = new Label(TextID.ENTER_PASSWORD.toString() + ":");
        l_Warning = new Label("");

        pf_Password = new PasswordField();
        pf_PasswordConfirm = new PasswordField();

        b_Login = new Button(TextID.LOGIN.toString());
        b_Register = new Button(TextID.REGISTER.toString());

        b_Login.setDefaultButton(true);

        // Add to grid
        grid.add(l_Header, 0, currPos++);

        grid.add(pf_Password, 0, currPos++);
        grid.add(pf_PasswordConfirm, 0, currPos++);

        grid.add(b_Register, 0, currPos);
        grid.add(b_Login, 0, currPos++);

        grid.add(l_Warning, 0, currPos++);

        // Set properties
        pf_Password.setMinWidth(WINDOW.width - 50);
        pf_Password.setPromptText("Password...");

        pf_PasswordConfirm.setPromptText("Re-type...");
        pf_PasswordConfirm.setVisible(false);

        b_Register.setVisible(false);

        grid.setAlignment(Pos.CENTER);

        GridPane.setHalignment(l_Header, HPos.CENTER);
        GridPane.setHalignment(b_Login, HPos.RIGHT);
        GridPane.setHalignment(b_Register, HPos.LEFT);

        // Listeners
        b_Login.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("Entered Password: " + pf_Password.getText());

                if (pf_Password.getText().length() != 0)
                {
                    if (!pf_PasswordConfirm.isVisible())
                        try
                        {
                            init(pf_Password.getText().toString(), false);
                        }
                        catch (Exceptions e)
                        {
                            // TODO Auto-generated catch block
                            b_Register.setVisible(true);
                            pf_PasswordConfirm.setVisible(true);

                            pf_Password.setDisable(true);

                            l_Warning.setText("Password is incorrect.");
                        }
                    else
                    {
                        reset();
                    }
                }
            }
        });

        b_Register.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("b_NEW button pressed");

                try
                {
                    if (pf_Password.getText().equals(pf_PasswordConfirm.getText()))
                        init(pf_PasswordConfirm.getText(), true);
                    else
                    {
                        reset();
                        l_Warning.setText("Passwords doesn't match!");
                    }
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                }
            }
        });

    }

    private void reset()
    {
        pf_Password.clear();
        pf_Password.setDisable(false);
        pf_Password.requestFocus();
        pf_PasswordConfirm.clear();
        pf_PasswordConfirm.setVisible(false);
        b_Register.setVisible(false);
        l_Warning.setText("");
    }

    @Override
    @SuppressWarnings("static-access")
    public void draw(Stage stage)
    {
        Logger.printDebug("LoginForm preparing...");

        stage.setTitle("pasSHA");
        stage.setResizable(false);
        stage.setMaximized(false);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.setScene(scene);

        Logger.printDebug("LoginForm displaying");
        stage.show();
    }

    private void init(String password, boolean isNewUser) throws Exceptions
    {

        CryptoSystem.init(password, isNewUser);

        // ========== Database activation START:
        try
        {
            PasswordCollection.init();
        }
        catch (Exceptions e)
        {
            System.exit(RC.SECURITY_FAILURE.ordinal()); // TODO
                                                        // abend
        }
        // ========== Database activation END:

        try
        {
            ctrl.switchForm(FORMS.MANAGE_PWDS);
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
