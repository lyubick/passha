/**
 *
 */
package ui;

import cryptosystem.CryptoSystem;
import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import ui.elements.Label;
import db.PasswordCollection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import ui.ManagePasswordsForm;

/**
 * @author lyubick
 *
 */
public class LoginForm extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 350;
        public static final int height = 200;
    }

    private Label         l_Header           = null;
    private Label         l_Warning          = null;
    private PasswordField pf_Password        = null;
    private PasswordField pf_PasswordConfirm = null;
    private Button        b_Login            = null;
    private Button        b_Register         = null;

    public LoginForm()
    {
        super(null); // Login doesn't have parents :(

        int gridRowCounter = 0;

        // ========== LABELS ========== //

        l_Header = new Label(TextID.FORM_LOGIN_LABEL_ENTER_PWD.toString() + ":");
        l_Warning = new Label("");

        // ========== TEXTS ========== //

        pf_Password = new PasswordField();
        pf_PasswordConfirm = new PasswordField();

        // ========== BUTTONS ========== //

        b_Login = getButton(TextID.FORM_LOGIN_LABEL_LOGIN.toString());
        b_Register = getButton(TextID.FORM_LOGIN_LABEL_REGISTER.toString());

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
        pf_Password.setPromptText(TextID.FORM_LOGIN_LABEL_PASSWORD.toString());

        pf_PasswordConfirm.setPromptText(TextID.FORM_LOGIN_LABEL_RETYPE.toString());
        pf_PasswordConfirm.setVisible(false);

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
                if (pf_Password.getText().length() != 0)
                {
                    if (!pf_PasswordConfirm.isVisible())
                        try
                        {
                            init(pf_Password.getText().toString(), false);
                        }
                        catch (Exceptions e)
                        {
                            if (e.getCode() == XC.USER_UNKNOWN)
                            {
                                b_Register.setVisible(true);

                                pf_PasswordConfirm.setVisible(true);
                                pf_Password.setDisable(true);

                                l_Warning.setText(TextID.FORM_LOGIN_ERROR_PWD_INCORRECT.toString());
                            }
                            else
                            {
                                Terminator.terminate(e);
                            }
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
                Logger.printDebug("b_Register button pressed");

                try
                {
                    if (pf_Password.getText().equals(pf_PasswordConfirm.getText()))
                        init(pf_PasswordConfirm.getText(), true);
                    else
                    {
                        reset();
                        l_Warning.setText(TextID.FORM_LOGIN_ERROR_PWDS_DONT_MATCH.toString());
                    }
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
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

    private void init(String password, boolean isNewUser) throws Exceptions
    {
        CryptoSystem.init(password, isNewUser);
        PasswordCollection.init();

        new ManagePasswordsForm().show();
        stage.hide();
    }

    @Override
    public void hide() throws Exceptions
    {
        stage.close();
        Terminator.terminate(new Exceptions(XC.END));
    }

    @Override
    public void show() throws Exceptions
    {
        Logger.printDebug("LoginForm preparing...");

        stage.setTitle(TextID.COMMON_LABEL_APP_NAME.toString());

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        Logger.printDebug("LoginForm displaying");
        stage.show();
    }

    @Override
    public void onUserCloseRequest() throws Exceptions
    {
        hide(); // FIXME maybe we should minimize
    }
}
