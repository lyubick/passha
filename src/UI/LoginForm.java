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
import CryptoSystem.CryptoSystem;
import Languages.Texts.TextID;
import Logger.Logger;
import Main.ABEND;
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
        public static final int width  = 350;
        public static final int height = 250;
    }

    private Label         l_Header           = null;
    private Label         l_Warning          = null;
    private PasswordField pf_Password        = null;
    private PasswordField pf_PasswordConfirm = null;
    private Button        b_Login            = null;
    private Button        b_Register         = null;

    LoginForm()
    {
        int gridRowCounter = 0;

        // ========== LABELS ========== //

        l_Header = getLabel(TextID.ENTER_PASSWORD.toString() + ":");
        l_Warning = getWarningLabel("");

        // ========== TEXTS ========== //

        pf_Password = new PasswordField();
        pf_PasswordConfirm = new PasswordField();

        // ========== BUTTONS ========== //

        b_Login = getButton(TextID.LOGIN.toString());
        b_Register = getButton(TextID.REGISTER.toString());

        b_Login.setDefaultButton(true);

        // ========== GRID ========== //

        grid.add(l_Header, 0, gridRowCounter++);

        grid.add(pf_Password, 0, gridRowCounter++);
        grid.add(pf_PasswordConfirm, 0, gridRowCounter++);

        grid.add(b_Register, 0, gridRowCounter);
        grid.add(b_Login, 0, gridRowCounter++);

        grid.add(l_Warning, 0, gridRowCounter++);

        // ========== PROPERTIES ========== //

        pf_Password.setMinWidth(WINDOW.width - 50);
        pf_Password.setPromptText(TextID.PWD.toString());

        pf_PasswordConfirm.setPromptText(TextID.RETYPE.toString());
        pf_PasswordConfirm.setVisible(false);

        b_Login.setMinWidth(BUTTON_WIDTH);
        b_Register.setMinWidth(BUTTON_WIDTH);
        b_Register.setVisible(false);

        grid.setAlignment(Pos.CENTER);

        GridPane.setHalignment(l_Header, HPos.CENTER);
        GridPane.setHalignment(b_Login, HPos.RIGHT);
        GridPane.setHalignment(b_Register, HPos.LEFT);

        // ========== LISTENERS ========== //
        b_Login.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                // We definetely must comment such logs in production version :D
                Logger.printDebug("Entered Password: " + pf_Password.getText());

                // maybe just disable buttons when password length is 0
                if (pf_Password.getText().length() != 0)
                {
                    if (!pf_PasswordConfirm.isVisible())
                        try
                        {
                            init(pf_Password.getText().toString(), false);
                        }
                        catch (Exceptions e)
                        {
                            if (e.getCode() == XC.UNKNOWN_USER)
                            {
                                b_Register.setVisible(true);

                                pf_PasswordConfirm.setVisible(true);
                                pf_Password.setDisable(true);

                                l_Warning.setText(TextID.PASSWORD_INCORRECT.toString());
                            }
                            else
                            {
                                ABEND.terminate(e);
                            }
                        }
                    else
                    {
                        reset();
                    }
                }
            }
        });

        // disable button when length == 0; (maybe even disable unless length is
        // same as pf_Password )
        b_Register.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("b_Register button pressed");

                try
                {
                    if (pf_Password.getText().equals(pf_PasswordConfirm.getText()))
                        init(pf_PasswordConfirm.getText(), true);
                    else
                    {
                        reset();
                        l_Warning.setText(TextID.PASSWORDS_DONT_MATCH.toString());
                    }
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
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
    public void draw(Stage stage)
    {
        Logger.printDebug("LoginForm preparing...");

        stage.setTitle(TextID.PROGRAM_NAME.toString());
        stage.setResizable(false);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.setScene(scene);

        Logger.printDebug("LoginForm displaying");
        stage.show();
    }

    private void init(String password, boolean isNewUser) throws Exceptions
    {
        CryptoSystem.init(password, isNewUser);

        try
        {
            // ========== Database activation START:
            PasswordCollection.init();
            // ========== Database activation END:

            ctrl.switchForm(FORMS.MANAGE_PWDS);
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }
    }

}
