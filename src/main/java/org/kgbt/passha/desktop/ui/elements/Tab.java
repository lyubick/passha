package org.kgbt.passha.desktop.ui.elements;

import org.kgbt.passha.core.VaultManager;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.kgbt.passha.desktop.languages.Local;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Terminator;

public class Tab extends javafx.scene.control.Tab
{
    private Label     l_tabName     = null;
    private TextField tf_newName    = null;

    private boolean   renameEnabled = true;

    private Tab       This          = null;

    public void setLabelText(String text)
    {
        l_tabName.setText(text);
    }

    public void setVaultName(String text)
    {
        setLabelText(Local.Texts.LABEL_VAULT_WITH_COLLS.toString() + text);
    }

    public void setRenameEnabled(boolean value)
    {
        renameEnabled = value;
    }

    public Tab()
    {
        super();

        This = this;

        l_tabName = new Label(
            Local.Texts.LABEL_VAULT_WITH_COLLS.toString() + Local.Texts.LABEL_UNNAMED.toString().toUpperCase());
        renameEnabled = false;
        tf_newName = new TextField();

        l_tabName.setOnMouseClicked(event ->
        {
            if (event.getClickCount() >= 2 && renameEnabled)
            {
                tf_newName.setText(l_tabName.getText().substring(Local.Texts.LABEL_VAULT_WITH_COLLS.toString().length(),
                    l_tabName.getText().length()));

                This.setGraphic(tf_newName);
                tf_newName.requestFocus();
            }

            event.consume();
        });

        tf_newName.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue)
            {
                l_tabName.setText(Local.Texts.LABEL_VAULT_WITH_COLLS.toString() + tf_newName.getText());
                This.setGraphic(l_tabName);
                ((VaultTabContent) This.getContent()).setName(tf_newName.getText());
            }
        });

        tf_newName.setOnAction(event ->
        {
            l_tabName.setText(Local.Texts.LABEL_VAULT_WITH_COLLS.toString() + tf_newName.getText());
            This.setGraphic(l_tabName);

            try
            {
                VaultManager.getInstance().getActiveVault().setName(tf_newName.getText());
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }
        });

        setGraphic(l_tabName);
    }

    public final void setTabContent(Node value)
    {
        setContent(value);
        setVaultName(((TabContent) value).getName());
        ((TabContent) value).activateTab();
    }
}
