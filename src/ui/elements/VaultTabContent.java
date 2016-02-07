package ui.elements;

import core.Vault;
import db.PasswordCollection;
import db.SpecialPassword;
import db.iSpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;

public class VaultTabContent extends TableView<iSpecialPassword>
{
    private Vault vault = null;

    private ChangeListener<Object> getSelectedItemPropertyListener()
    {
        return new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue)
            {
                try
                {
                    if (newValue == null)
                    {
                        PasswordCollection.getInstance().setSelected(null);
                        return;
                    }

                    PasswordCollection.getInstance().setSelected(getSelectionModel().getSelectedItem().getOrigin());
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

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

    public VaultTabContent(Vault vault)
    {
        this.vault = vault;

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
    }

    public ObservableList<iSpecialPassword> getIface()
    {
        return vault.getIface();
    }
}
