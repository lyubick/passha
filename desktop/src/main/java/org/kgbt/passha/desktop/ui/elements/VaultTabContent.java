package org.kgbt.passha.desktop.ui.elements;

import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.db.SpecialPassword;
import org.kgbt.passha.core.db.Vault;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.FormSearch;
import org.kgbt.passha.desktop.ui.FormVaultsManager;
import org.kgbt.passha.desktop.ui.interfaces.iSpecialPassword;
import sun.rmi.runtime.Log;

import java.util.Arrays;

public class VaultTabContent extends TableView<iSpecialPassword> implements TabContent {
    private Vault vault = null;
    private Tab t_ownTab = null;

    private ChangeListener<iSpecialPassword> getSelectedItemPropertyListener() {
        return (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.setPasswordVisible(false);
                refresh();
            }

            if (newValue == null) {
                vault.setSelected(null);
                return;
            }

            vault.setSelected(newValue.getOrigin());
        };
    }

    public VaultTabContent(Tab ownerTab, Vault vault) {
        this.vault = vault;
        t_ownTab = ownerTab;

        t_ownTab.setRenameEnabled(true);

        TableColumn<iSpecialPassword, String> cName = new TableColumn<>(Texts.FORM_MANAGEPWD_LABEL_PWD_NAME.toString());
        TableColumn<iSpecialPassword, String> cComment = new TableColumn<>(Texts.LABEL_COMMENT.toString());
        TableColumn<iSpecialPassword, String> cUrl = new TableColumn<>(Texts.LABEL_URL.toString());

        TableColumn<iSpecialPassword, String> cShortcut = new TableColumn<>(Texts.LABEL_SHORTCUT.toString());

        TableColumn<iSpecialPassword, String> cPassword = new TableColumn<>("Password");

        // Array.asList is used to avoid "Unchecked..." warning
        getColumns().addAll(Arrays.asList(cName, cShortcut, cComment, cUrl, cPassword));

        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        cUrl.setCellValueFactory(new PropertyValueFactory<>("url"));
        cShortcut.setCellValueFactory(new PropertyValueFactory<>("shortcut"));
        cPassword.setCellValueFactory(new PropertyValueFactory<>("password"));

        getSelectionModel().selectedItemProperty().addListener(getSelectedItemPropertyListener());

        reload();

        this.setOnKeyReleased(event -> {
            if ((event.isControlDown() && event.getCode() == KeyCode.F)
                    || event.getCode() == KeyCode.F3
            ) {
                try {
                    new FormSearch(FormVaultsManager.getThis(), this, (FilteredList<iSpecialPassword>) this.getItems());
                } catch (Exceptions e) {
                    Logger.printError("Failed to search with" + e.getMessage());
                }
            }
        });

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue /* false */ && oldValue /* true */) {
                ((FilteredList<iSpecialPassword>) this.getItems()).setPredicate(p -> true);
            }
        });

        this.setOnMouseClicked(event ->
        {
            iSpecialPassword selectedItem = this.getSelectionModel()
                    .getSelectedItem();

            if (selectedItem == null || event.getClickCount() < 2) {
                event.consume();
                return;
            }

            selectedItem.setPasswordVisible(true);

            this.getColumns()
                    .get(0)
                    .setVisible(false);
            this.getColumns()
                    .get(0)
                    .setVisible(true);
        });
    }

    @Override
    public void closeTab() {
        try {
            VaultManager.getInstance()
                    .removeVault();
        } catch (Exceptions e) {
            Terminator.terminate(e);
        }
    }

    @Override
    public void activateTab() {
        try {
            VaultManager.getInstance()
                    .activateVault(vault);
        } catch (Exceptions e) {
            Terminator.terminate(e);
        }
    }

    public void reload() {
        getSelectionModel().clearSelection();
        setItems(iSpecialPassword.getIface(vault.getPasswords()));
    }

    public void refresh() {
        if (getColumns().isEmpty()) return;

        getColumns().get(0)
                .setVisible(false);
        getColumns().get(0)
                .setVisible(true);
    }

    @Override
    public void setName(String name) {
        vault.setName(name);
    }

    @Override
    public String getName() {
        String name = vault.getName();
        return name.isEmpty() ? Texts.LABEL_UNNAMED.toString()
                .toUpperCase() : name;
    }

    public boolean hasVault(Vault vault) {
        return this.vault == vault;
    }
}
