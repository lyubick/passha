package ui.elements;

import core.Vault;
import core.VaultManager;
import cryptosystem.Autologin;
import db.iSpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import languages.Local.Texts;
import main.Exceptions;
import main.Terminator;
import ui.AbstractForm;
import ui.FormVaultsManager;

public class VaultTabContent extends TableView<iSpecialPassword> implements TabContent
{
    private Vault        vault    = null;
    private AbstractForm parent   = null;
    private Tab          t_ownTab = null;

    private ChangeListener<Object> getSelectedItemPropertyListener()
    {
        return new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue)
            {
                if (oldValue != null)
                {
                    ((iSpecialPassword) oldValue).setPasswordVisible(false);
                    refresh();
                }

                if (newValue == null)
                {
                    vault.setSelected(null);
                    return;
                }

                vault.setSelected(((iSpecialPassword) newValue).getOrigin());

            }
        };
    }

    public VaultTabContent(Tab ownerTab, Vault vault, AbstractForm parent)
    {
        this.vault = vault;
        this.parent = parent;
        t_ownTab = ownerTab;

        t_ownTab.setRenameEnabled(true);

        TableColumn<iSpecialPassword, String> cName = new TableColumn<>(Texts.FORM_MANAGEPWD_LABEL_PWD_NAME.toString());
        TableColumn<iSpecialPassword, String> cComment = new TableColumn<>(Texts.LABEL_COMMENT.toString());
        TableColumn<iSpecialPassword, String> cUrl = new TableColumn<>(Texts.LABEL_URL.toString());

        TableColumn<iSpecialPassword, String> cShortcut = new TableColumn<>(Texts.LABEL_SHORTCUT.toString());

        TableColumn<iSpecialPassword, String> cPassword = new TableColumn<>("Password");

        getColumns().addAll(cName, cShortcut, cComment, cUrl, cPassword);

        cName.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("name"));
        cComment.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("comment"));
        cUrl.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("url"));
        cShortcut.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("shortcut"));
        cPassword.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("password"));

        getSelectionModel().selectedItemProperty().addListener(getSelectedItemPropertyListener());

        reload();

        this.setOnMouseClicked(event ->
        {
            if (event.getClickCount() < 2)
            {
                event.consume();
                return;
            }

            this.getSelectionModel().getSelectedItem().setPasswordVisible(true);

            this.getColumns().get(0).setVisible(false);
            this.getColumns().get(0).setVisible(true);
        });

    }

    @Override
    public void closeTab()
    {
        try
        {
            VaultManager.getInstance().removeVault();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }

    @Override
    public void activateTab()
    {
        try
        {
            VaultManager.getInstance().activateVault(vault);
            ((FormVaultsManager) parent).setVaultControlsDisabled(false);
            Autologin.getInstance().check(vault);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }

    public void reload()
    {
        getSelectionModel().clearSelection();
        setItems(vault.getIface());
    }

    public void refresh()
    {
        if (getColumns().isEmpty()) return;

        getColumns().get(0).setVisible(false);
        getColumns().get(0).setVisible(true);
    }

    @Override
    public void setName(String name)
    {
        vault.setName(name);
    }

    @Override
    public String getName()
    {
        String name = vault.getName();
        return name.isEmpty() ? Texts.LABEL_UNNAMED.toString().toUpperCase() : name;
    }
}
