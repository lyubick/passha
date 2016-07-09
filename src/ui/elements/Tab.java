package ui.elements;

import core.VaultManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import languages.Local;
import main.Exceptions;
import main.Terminator;

public class Tab extends javafx.scene.control.Tab
{
    private Label     l_label       = null;
    private TextField tf_textField  = null;

    private boolean   renameEnabled = true;

    private Tab       This          = null;

    public void setContent_(javafx.scene.Node value)
    {
        super.setContent(value);
        ((TabContent) value).activateTab();
    }

    public void setLabelText(String text)
    {
        l_label.setText(text);
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

        l_label =
            new Label(Local.Texts.LABEL_VAULT_WITH_COLLS.toString() + Local.Texts.LABEL_NEW.toString().toUpperCase());
        renameEnabled = false;
        tf_textField = new TextField();

        l_label.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (event.getClickCount() >= 2 && renameEnabled)
                {
                    tf_textField.setText(l_label.getText()
                        .substring(Local.Texts.LABEL_VAULT_WITH_COLLS.toString().length(), l_label.getText().length()));

                    This.setGraphic(tf_textField);
                    tf_textField.requestFocus();
                }

                event.consume();
                return;
            }

        });

        tf_textField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue == false)
                {
                    l_label.setText(Local.Texts.LABEL_VAULT_WITH_COLLS.toString() + tf_textField.getText());
                    This.setGraphic(l_label);
                    ((VaultTabContent) This.getContent()).setName(tf_textField.getText());
                }
            }
        });

        tf_textField.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                l_label.setText(Local.Texts.LABEL_VAULT_WITH_COLLS.toString() + tf_textField.getText());
                This.setGraphic(l_label);

                try
                {
                    VaultManager.getInstance().getActiveVault().setName(tf_textField.getText());
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        setGraphic(l_label);
    }
}
