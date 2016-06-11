package ui.elements;

import core.Vault;
import core.VaultManager;
import db.iSpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import languages.Local.TextID;
import main.Exceptions;
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

                if (newValue == null)
                {
                    vault.setSelected(null);
                    return;
                }

                vault.setSelected(getSelectionModel().getSelectedItem().getOrigin());
                // TODO:
                /*
                 * b_copy.setDisable(true);
                 * tf_pass.setText(table.getSelectionModel
                 * ().getSelectedItem().getPassword());
                 * b_copy.setDisable(false);
                 */
            }
        };
    }

    public VaultTabContent(Tab ownerTab, Vault vault, AbstractForm parent)
    {
        this.vault = vault;
        this.parent = parent;
        t_ownTab = ownerTab;

        t_ownTab.setRenameEnabled(true);

        TableColumn<iSpecialPassword, String> cName =
            new TableColumn<iSpecialPassword, String>(TextID.FORM_MANAGEPWD_LABEL_PWD_NAME.toString());
        TableColumn<iSpecialPassword, String> cComment =
            new TableColumn<iSpecialPassword, String>(TextID.FORM_CREATEPWD_LABEL_COMMENT.toString());
        TableColumn<iSpecialPassword, String> cUrl =
            new TableColumn<iSpecialPassword, String>(TextID.FORM_CREATEPWD_LABEL_URL.toString());

        TableColumn<iSpecialPassword, String> cShortcut =
            new TableColumn<iSpecialPassword, String>(TextID.FORM_EDITPWD_LABEL_SHORTCUT.toString());

        getColumns().add(cName);
        getColumns().add(cComment);
        getColumns().add(cUrl);
        getColumns().add(cShortcut);

        cName.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("name"));
        cComment.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("comment"));
        cUrl.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("url"));
        cShortcut.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("shortcut"));

        getSelectionModel().selectedItemProperty().addListener(getSelectedItemPropertyListener());

        refreshTab();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void activateTab()
    {
        try
        {
            VaultManager.getInstance().activateVault(vault);
            ((FormVaultsManager) parent).setVaultControlsDisabled(false);
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void refreshTab()
    {
        getSelectionModel().clearSelection();
        setItems(vault.getIface());
    }

    public String getVaultName()
    {
        String name = vault.getName();
        return name.isEmpty() ? "unnamed" : name;
    }

    @Override
    public void setName(String name)
    {
        // TODO Auto-generated method stub
        vault.setName(name);
    }

}
