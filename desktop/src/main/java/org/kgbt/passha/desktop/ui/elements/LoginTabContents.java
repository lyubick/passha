package org.kgbt.passha.desktop.ui.elements;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.db.Vault;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.EntryField.TEXTFIELD;
import org.kgbt.passha.desktop.ui.tasks.LoggedTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginTabContents extends org.kgbt.passha.desktop.ui.elements.GridPane implements TabContent
{
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Label         l_header           = null;
    private Label         l_warning          = null;
    private PasswordField pf_password        = null;
    private PasswordField pf_passwordConfirm = null;
    private Button        b_login            = null;
    private Button        b_register         = null;
    private Tab           t_ownTab           = null;

    private void onLoginBtnAction(ActionEvent event)
    {
        if (pf_password.getText().isEmpty())
            return;

        if (pf_passwordConfirm.isVisible())
            reset();
        else
        {
            Task<Vault> init = init(pf_password.getText(), false);
            init.setOnFailed(failedEvent -> {
                Exceptions e = (Exceptions) init.getException();
                Platform.runLater(() -> {
                    t_ownTab.setTabContent(this);
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
                });
            });

            executorService.submit(init);
        }
    }

    private void onRegisterBtnAction(ActionEvent event)
    {
        Logger.printTrace("b_Register button pressed");

        if (pf_password.getText().equals(pf_passwordConfirm.getText()))
        {
            Task<Vault> init = init(pf_passwordConfirm.getText(), true);
            init.setOnFailed(failedEvent -> {
                Exceptions e = (Exceptions) init.getException();
                Platform.runLater(() -> Terminator.terminate(e));
            });
            executorService.submit(init);
        }
        else
        {
            reset();
            l_warning.setText(Texts.FORM_LOGIN_MSG_PWDS_DONT_MATCH);
        }
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

    private Task<Vault> init(String password, boolean isNewUser)
    {
        t_ownTab.setTabContent(new LoggingInTabContent());
        Logger.printTrace("init user");

        Task<Vault> task = new LoggedTask<Vault>()
        {
            @Override
            protected Vault call() throws Exception
            {
                try
                {
                    return VaultManager.getInstance().addVault(password, isNewUser, "");
                }
                catch (Exceptions e)
                {
                    if (e.getCode() == XC.FILE_DOES_NOT_EXIST)
                        throw new Exceptions(XC.USER_UNKNOWN);
                    else
                        throw e;
                }
            }
        };

        task.setOnSucceeded(event -> Platform.runLater(() -> {
            VaultTabContent newContent = new VaultTabContent(t_ownTab, task.getValue());
            t_ownTab.setTabContent(newContent);
            t_ownTab.setVaultName(newContent.getName());
        }));

        return task;
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
        b_login.setOnAction(this::onLoginBtnAction);

        b_register.setOnAction(this::onRegisterBtnAction);
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
