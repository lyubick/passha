package ui.elements;

import core.Vault;
import core.VaultManager;
import db.iSpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import ui.AbstractForm;
import ui.DlgExport;
import ui.FormCreatePwd;
import ui.FormDeletePwd;
import ui.FormEditPwd;
import ui.FormResetPwd;

public class VaultTabContent extends TableView<iSpecialPassword> implements TabContent
{
    private Vault        vault = null;
    private AbstractForm owner = null; // TODO:

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

    public VaultTabContent(Vault vault, AbstractForm parent)
    {
        this.vault = vault;
        this.owner = parent;

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
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public EventHandler<ActionEvent> getOnNewBtnAction() throws Exceptions
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent ae)
            {
                new FormCreatePwd(owner);
            }
        };
    }

    public EventHandler<ActionEvent> getOnEditBtnAction() throws Exceptions
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    if (vault.getSelected() == null) return;
                    new FormEditPwd(owner);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        };
    }

    public EventHandler<ActionEvent> getOnCopyToClipboardBtnAction() throws Exceptions
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printError("Feature is not implemented yet!!!");
                // parent.minimize();
                // copyToClipboard(); // TODO: pass password to copy
            }
        };
    }

    public EventHandler<ActionEvent> getOnDeleteBtnAction() throws Exceptions
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                if (vault.getSelected() == null) return;
                new FormDeletePwd(owner);
            }
        };
    }

    public EventHandler<ActionEvent> getOnResetBtnAction() throws Exceptions
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                if (vault.getSelected() == null) return;
                new FormResetPwd(owner);
            }
        };
    }

    public EventHandler<ActionEvent> getOnExportBtnAction() throws Exceptions
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                new DlgExport(owner);
            }
        };
    }

    public void refreshTab()
    {
        getSelectionModel().clearSelection();
        setItems(vault.getIface());
    }

}
