package org.kgbt.passha.desktop.ui.elements;

import org.kgbt.passha.core.VaultManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.desktop.ui.elements.EntryField.TEXTFIELD;

public class LoginTabContents extends org.kgbt.passha.desktop.ui.elements.GridPane implements TabContent
{
    private Label         l_header           = null;
    private Label         l_warning          = null;
    private PasswordField pf_password        = null;
    private PasswordField pf_passwordConfirm = null;
    private Button        b_login            = null;
    private Button        b_register         = null;
    private Tab           t_ownTab           = null;

    private EventHandler<ActionEvent> getOnLoginBtnAction()
    {
        return arg0 -> {
            if (pf_password.getText().length() != 0)
            {
                if (!pf_passwordConfirm.isVisible())
                    try
                    {
                        init(pf_password.getText(), false);
                    }
                    catch (Exceptions e)
                    {
                        switch (e.getCode())
                        {
                            case USER_UNKNOWN:
                                b_register.setVisible(true);

                                pf_passwordConfirm.setVisible(true);
                                pf_password.setDisable(true);

                                l_warning.setText(Texts.FORM_LOGIN_MSG_INCORRECT_PWD);
                                break;
                            case VAULT_OPENED:
                                l_warning.setText(Texts.MSG_VAULT_ALREADY_OPENED.toString());
                                break;
                            default:
                                Terminator.terminate(e);
                                break;
                        }
                    }
                else
                {
                    reset();
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnRegisterBtnAction()
    {
        return arg0 -> {
            Logger.printTrace("b_Register button pressed");

            try
            {
                if (pf_password.getText().equals(pf_passwordConfirm.getText()))
                    init(pf_passwordConfirm.getText(), true);
                else
                {
                    reset();
                    l_warning.setText(Texts.FORM_LOGIN_MSG_PWDS_DONT_MATCH);
                }
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }
        };
    }

    private void reset()
    {
        pf_password.clear();
        pf_password.setDisable(false);
        pf_passwordConfirm.clear();
        pf_passwordConfirm.setVisible(false);
        b_register.setVisible(false);
        l_warning.setText("");
    }

    private void init(String password, boolean isNewUser) throws Exceptions
    {
        Logger.printTrace("init user");

        try
        {
            VaultTabContent newContent =
                new VaultTabContent(t_ownTab, VaultManager.getInstance().addVault(password, isNewUser, ""));
            t_ownTab.setTabContent(newContent);
            t_ownTab.setVaultName(newContent.getName());
        }
        catch (Exceptions e)
        {
            if (e.getCode() == XC.FILE_DOES_NOT_EXIST)
                throw new Exceptions(XC.USER_UNKNOWN);
            else
                throw e;
        }
    }

    public LoginTabContents(Tab ownTab)
    {
        t_ownTab = ownTab;

        // ========== LABELS ========== //
        l_header = new Label(Texts.LABEL_ENTER_PASSWORD.toString() + ", "
            + System.getProperty("user.name", Texts.LABEL_UNKNOWN_USER.toString()) + "!");
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

        b_login = new Button(Texts.LABEL_LOGIN.toString());
        b_register = new Button(Texts.LABEL_REGISTER.toString());

        b_login.setDefaultButton(true);

        // ========== GRID ========== //

        addHElement(l_header);

        addHElement(pf_password);
        addHElement(pf_passwordConfirm);

        add(b_register, 0);
        addHElement(b_login);

        addHElement(l_warning);

        // ========== PROPERTIES ========== //

        pf_password.setMinWidth(TEXTFIELD.WIDTH.XL);
        pf_password.setPromptText(Texts.LABEL_PASSWORD.toString());

        pf_passwordConfirm.setPromptText(Texts.LABEL_RETYPE.toString());
        pf_passwordConfirm.setVisible(false);

        b_register.setVisible(false);

        setAlignment(Pos.CENTER);

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(b_login, HPos.RIGHT);
        GridPane.setHalignment(b_register, HPos.LEFT);
        GridPane.setHalignment(l_warning, HPos.CENTER);

        // ========== LISTENERS ========== //
        b_login.setOnAction(getOnLoginBtnAction());

        b_register.setOnAction(getOnRegisterBtnAction());
    }

    @Override
    public void closeTab()
    {
    }

    @Override
    public void activateTab()
    {
        Logger.printTrace("activateTab called");
        try
        {
            VaultManager.getInstance().deactivateVault();
            Platform.runLater(() -> pf_password.requestFocus());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }

    @Override
    public void reload()
    {
    }

    @Override
    public void setName(String name)
    {
    }

    @Override
    public String getName()
    {
        return Texts.LABEL_UNNAMED.toString().toUpperCase();
    }
}
