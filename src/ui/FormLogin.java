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
import ui.elements.Button;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.Label;
import db.PasswordCollection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import ui.FormManagePwd;

/**
 * @author lyubick
 *
 */
public class FormLogin extends AbstractForm
{
    private Label         l_header           = null;
    private Label         l_warning          = null;
    private PasswordField pf_password        = null;
    private PasswordField pf_passwordConfirm = null;
    private Button        b_login            = null;
    private Button        b_register         = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnLoginBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                if (pf_password.getText().length() != 0)
                {
                    if (!pf_passwordConfirm.isVisible())
                        try
                        {
                            init(pf_password.getText().toString(), false);
                        }
                        catch (Exceptions e)
                        {
                            if (e.getCode() == XC.USER_UNKNOWN)
                            {
                                b_register.setVisible(true);

                                pf_passwordConfirm.setVisible(true);
                                pf_password.setDisable(true);

                                l_warning.setText(TextID.FORM_LOGIN_MSG_INCORRECT_PWD.toString());
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
        };
    }

    private EventHandler<ActionEvent> getOnRegisterBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("b_Register button pressed");

                try
                {
                    if (pf_password.getText().equals(pf_passwordConfirm.getText()))
                        init(pf_passwordConfirm.getText(), true);
                    else
                    {
                        reset();
                        l_warning.setText(TextID.FORM_LOGIN_MSG_PWDS_DONT_MATCH.toString());
                    }
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        };
    }

    /* PRIVATE ROUTINE */
    private void reset()
    {
        pf_password.clear();
        pf_password.setDisable(false);
        pf_password.requestFocus();
        pf_passwordConfirm.clear();
        pf_passwordConfirm.setVisible(false);
        b_register.setVisible(false);
        l_warning.setText("");
    }

    private void init(String password, boolean isNewUser) throws Exceptions
    {
        CryptoSystem.init(password, isNewUser);
        PasswordCollection.init();

        new FormManagePwd();
        close();
    }

    /* PUBLIC ROUTINE */
    public FormLogin()
    {
        super(null, TextID.FORM_LOGIN_NAME.toString()); // Login doesn't have
                                                        // parents :(

        // ========== LABELS ========== //

        l_header = new Label(TextID.FORM_LOGIN_LABEL_ENTER_PWD.toString());
        l_header.beHeader();
        l_warning = new Label();
        l_warning.beError();

        // ========== TEXTS ========== //

        pf_password = new PasswordField();
        pf_passwordConfirm = new PasswordField();

        pf_password.setMinWidth(TEXTFIELD.WIDTH.XL);
        pf_password.setMaxWidth(TEXTFIELD.WIDTH.XL);
        pf_password.setMinHeight(TEXTFIELD.HEIGTH.M);
        pf_password.setMaxHeight(TEXTFIELD.HEIGTH.M);

        pf_passwordConfirm.setMinWidth(TEXTFIELD.WIDTH.XL);
        pf_passwordConfirm.setMaxWidth(TEXTFIELD.WIDTH.XL);
        pf_passwordConfirm.setMinHeight(TEXTFIELD.HEIGTH.M);
        pf_passwordConfirm.setMaxHeight(TEXTFIELD.HEIGTH.M);

        // ========== BUTTONS ========== //

        b_login = new Button(TextID.FORM_LOGIN_LABEL_LOGIN.toString());
        b_register = new Button(TextID.FORM_LOGIN_LABEL_REGISTER.toString());

        b_login.setDefaultButton(true);

        // ========== GRID ========== //

        grid.addHElement(l_header);

        grid.addHElement(pf_password);
        grid.addHElement(pf_passwordConfirm);

        grid.add(b_register, 0);
        grid.addHElement(b_login);

        grid.addHElement(l_warning);

        // ========== PROPERTIES ========== //

        pf_password.setMinWidth(TEXTFIELD.WIDTH.XL);
        pf_password.setPromptText(TextID.FORM_LOGIN_LABEL_PASSWORD.toString());

        pf_passwordConfirm.setPromptText(TextID.FORM_LOGIN_LABEL_RETYPE.toString());
        pf_passwordConfirm.setVisible(false);

        b_register.setVisible(false);

        grid.setAlignment(Pos.CENTER);

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(b_login, HPos.RIGHT);
        GridPane.setHalignment(b_register, HPos.LEFT);
        GridPane.setHalignment(l_warning, HPos.CENTER);

        // ========== LISTENERS ========== //
        b_login.setOnAction(getOnLoginBtnAction());

        b_register.setOnAction(getOnRegisterBtnAction());

        open();
    }

    /* OVERRIDE */
    @Override
    protected void onUserCloseRequest()
    {
        close();
        Terminator.terminate(new Exceptions(XC.END));
    }

    @Override
    protected void onUserMinimizeRequest()
    {
        // do nothing
    }
}
