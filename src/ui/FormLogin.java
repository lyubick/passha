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
    private Label         l_Header           = null;
    private Label         l_Warning          = null;
    private PasswordField pf_Password        = null;
    private PasswordField pf_PasswordConfirm = null;
    private Button        b_Login            = null;
    private Button        b_Register         = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnLoginBtnAction()
    {
        return new EventHandler<ActionEvent>()
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

                                l_Warning.setText(TextID.FORM_LOGIN_MSG_INCORRECT_PWD.toString());
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
                    if (pf_Password.getText().equals(pf_PasswordConfirm.getText()))
                        init(pf_PasswordConfirm.getText(), true);
                    else
                    {
                        reset();
                        l_Warning.setText(TextID.FORM_LOGIN_MSG_PWDS_DONT_MATCH.toString());
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

        new FormManagePwd();
        close();
    }

    /* PUBLIC ROUTINE */
    public FormLogin()
    {
        super(null, TextID.FORM_LOGIN_NAME.toString()); // Login doesn't have
                                                        // parents :(

        // ========== LABELS ========== //

        l_Header = new Label(TextID.FORM_LOGIN_LABEL_ENTER_PWD.toString() + ":");
        l_Warning = new Label();

        // ========== TEXTS ========== //

        pf_Password = new PasswordField();
        pf_PasswordConfirm = new PasswordField();

        pf_Password.setMinWidth(TEXTFIELD.WIDTH.XL);
        pf_Password.setMaxWidth(TEXTFIELD.WIDTH.XL);
        pf_Password.setMinHeight(TEXTFIELD.HEIGTH.M);
        pf_Password.setMaxHeight(TEXTFIELD.HEIGTH.M);

        pf_PasswordConfirm.setMinWidth(TEXTFIELD.WIDTH.XL);
        pf_PasswordConfirm.setMaxWidth(TEXTFIELD.WIDTH.XL);
        pf_PasswordConfirm.setMinHeight(TEXTFIELD.HEIGTH.M);
        pf_PasswordConfirm.setMaxHeight(TEXTFIELD.HEIGTH.M);

        // ========== BUTTONS ========== //

        b_Login = new Button(TextID.FORM_LOGIN_LABEL_LOGIN.toString());
        b_Register = new Button(TextID.FORM_LOGIN_LABEL_REGISTER.toString());

        b_Login.setDefaultButton(true);

        // ========== GRID ========== //

        grid.addHElement(l_Header);

        grid.addHElement(pf_Password);
        grid.addHElement(pf_PasswordConfirm);

        grid.add(b_Register, 0);
        grid.addHElement(b_Login);

        grid.addHElement(l_Warning);

        // ========== PROPERTIES ========== //

        pf_Password.setMinWidth(300); // FIXME
        pf_Password.setPromptText(TextID.FORM_LOGIN_LABEL_PASSWORD.toString());

        pf_PasswordConfirm.setPromptText(TextID.FORM_LOGIN_LABEL_RETYPE.toString());
        pf_PasswordConfirm.setVisible(false);

        b_Register.setVisible(false);

        grid.setAlignment(Pos.CENTER);

        GridPane.setHalignment(l_Header, HPos.CENTER);
        GridPane.setHalignment(b_Login, HPos.RIGHT);
        GridPane.setHalignment(b_Register, HPos.LEFT);

        // ========== LISTENERS ========== //
        b_Login.setOnAction(getOnLoginBtnAction());

        b_Register.setOnAction(getOnRegisterBtnAction());

        autoSize();

        open();
    }

    /* OVERRIDE */
    @Override
    protected void onUserCloseRequest()
    {
        close(); // FIXME maybe we should minimize
        Terminator.terminate(new Exceptions(XC.END));
    }

    @Override
    protected void onUserMinimizeRequest()
    {
        // do nothing
    }
}
