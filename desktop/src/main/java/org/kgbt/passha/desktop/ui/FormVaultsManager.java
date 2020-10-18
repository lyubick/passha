package org.kgbt.passha.desktop.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.common.cfg.Settings;
import org.kgbt.passha.core.db.Database.Status;
import org.kgbt.passha.core.db.SpecialPassword;
import org.kgbt.passha.core.db.Vault;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.desktop.Autologin;
import org.kgbt.passha.desktop.autotype.KeyMapper;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.LoginTabContents;
import org.kgbt.passha.desktop.ui.elements.Tab;
import org.kgbt.passha.desktop.ui.elements.TabContent;
import org.kgbt.passha.desktop.ui.elements.VaultTabContent;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class FormVaultsManager extends AbstractForm {
    private final class WINDOW {
        public static final int width = 900;
        public static final int height = 500;
    }

    private static final double STATUS_CIRCLE_RADIUS = 10;
    private static final int TAB_PANE_MIN_HEIGHT = WINDOW.height - 150;
    private static final int TAB_PANE_MIN_WIDTH = WINDOW.width - 200;

    private static AbstractForm This = null;

    static public AbstractForm getThis() {
        return This;
    }

    private HBox hb_statusBar = null;
    private Circle c_dbStatus = null;
    private ObjectProperty<Status> op_dbStatusProperty = null;
    private Tooltip tt_dbStatusText = null;

    private ContextMenu cm_vault = null;
    private ContextMenu cm_default = null;

    private Menu m_file = null;
    private Menu m_vault = null;
    private Menu m_password = null;
    private Menu m_help = null;

    private MenuItem mi_about = null;
    private MenuItem mi_exit = null;
    private MenuItem mi_new = null;
    private MenuItem mi_edit = null;
    private MenuItem mi_reset = null;
    private MenuItem mi_delete = null;
    private MenuItem mi_settings = null;
    private MenuItem mi_copy = null;
    private MenuItem mi_autotype = null;
    private MenuItem mi_export = null;

    private CheckMenuItem cmi_autologin = null;

    private TabPane tp_vaults = null;
    private Tab t_newTabCreator = null;

    static Task<Void> tsk_pwdLifeTime = null;

    ChangeListener<Status> dbStatusListener = null;

    public static FormVaultsManager getInstance() throws Exceptions {
        if (This == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return (FormVaultsManager) This;
    }

    public FormVaultsManager() throws Exceptions {
        // No parents it is a main Form
        super(null, Texts.FORM_MANAGEPWD_NAME, WindowPriority.ONLY_ONE_OPENED, true);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);
        stage.setMinHeight(WINDOW.height);
        stage.setMinWidth(WINDOW.width);

        This = this;

        dbStatusListener = (observable, oldValue, newValue) -> setDBStatus(newValue);

        // ========== STATUS BAR ========== //
        hb_statusBar = new HBox();
        hb_statusBar.setAlignment(Pos.CENTER_RIGHT);
        c_dbStatus = new Circle(STATUS_CIRCLE_RADIUS, Color.GREY);
        c_dbStatus.setStroke(Color.BLACK);
        tt_dbStatusText = new Tooltip();
        Tooltip.install(c_dbStatus, tt_dbStatusText);
        hb_statusBar.getChildren().add(c_dbStatus);

        // ========== MENU ========== //
        menuMain = new MenuBar();
        m_file = new Menu(Texts.LABEL_FILE.toString());
        m_vault = new Menu(Texts.LABEL_VAULT.toString());
        m_password = new Menu(Texts.LABEL_PASSWORD.toString());
        m_help = new Menu(Texts.LABEL_HELP.toString());

        mi_settings = new MenuItem(Texts.LABEL_SETTINGS.toString());
        mi_exit = new MenuItem(Texts.LABEL_EXIT.toString());

        mi_new = new MenuItem(Texts.LABEL_NEW.toString());
        mi_edit = new MenuItem(Texts.LABEL_EDIT.toString());
        mi_reset = new MenuItem(Texts.FORM_MANAGEPWD_LABEL_RESET.toString());
        mi_delete = new MenuItem(Texts.LABEL_DELETE.toString());
        mi_copy = new MenuItem(Texts.FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD.toString());
        mi_autotype = new MenuItem(Texts.FORM_MANAGEPWD_LABEL_AUTO_TYPE.toString());
        mi_export = new MenuItem(Texts.FORM_MANAGEPWD_LABEL_EXPORT.toString());

        mi_about = new MenuItem(Texts.LABEL_ABOUT.toString());

        cmi_autologin = new CheckMenuItem(Texts.LABEL_AUTOLOGIN.toString());
        cmi_autologin.selectedProperty().bindBidirectional(Autologin.getInstance().onProperty());

        m_file.getItems().addAll(mi_settings, new SeparatorMenuItem(), mi_exit);
        m_vault.getItems().addAll(m_password, mi_export, new SeparatorMenuItem(), cmi_autologin);
        m_vault.setDisable(true);
        m_password.getItems().addAll(mi_new, mi_edit, mi_delete, new SeparatorMenuItem(), mi_reset,
                new SeparatorMenuItem(), mi_copy, mi_autotype);
        m_help.getItems().addAll(mi_about);
        menuMain.getMenus().addAll(m_file, m_vault, m_help);

        mi_export.setOnAction(getOnExportAction());

        cmi_autologin.setOnAction(event ->
        {
            try {
                if (VaultManager.getInstance().getActiveVault() == null) {
                    Logger.printError("No active vault selected for autologin! Ignoring...");
                    cmi_autologin.setSelected(false);
                    event.consume();
                    return;
                }

                if (cmi_autologin.isSelected())
                    Autologin.getInstance().setAutologinON(VaultManager.getInstance().getActiveVault());
                else
                    Autologin.getInstance().setAutologinOFF(VaultManager.getInstance().getActiveVault());
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
        });

        mi_settings.setOnAction(event ->
        {
            try {
                new FormSettings(This);
            } catch (Exceptions e) {
                if (e.getCode().equals(XC.FORM_ALREADY_OPEN))
                    ; // Ignore
                else
                    Logger.printError("Unhandled exception: " + e.getCode());
            }
        });

        mi_exit.setOnAction(event -> Terminator.terminate(new Exceptions(XC.END)));

        mi_new.setOnAction(getOnNewAction());
        mi_edit.setOnAction(getOnEditAction());
        mi_reset.setOnAction(getOnResetAction());
        mi_delete.setOnAction(getOnDeleteAction());
        mi_copy.setOnAction(getOnCopyAction());
        mi_autotype.setOnAction(getOnAutoType());

        mi_copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        mi_autotype.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));

        mi_about.setOnAction(event ->
        {
            try {
                new FormAbout(This);
            } catch (Exceptions e) {
                if (e.getCode().equals(XC.FORM_ALREADY_OPEN))
                    ; // Ignore
                else
                    Logger.printError("Unhandled exception: " + e.getCode());
            }
        });

        cm_vault = new ContextMenu();
        cm_vault.getItems().addAll(m_password.getItems());

        // ========== REFRESH ========== //
        group.getChildren().remove(grid);
        group.getChildren().addAll(menuMain, grid);

        // ========== TAB pane =========== //

        tp_vaults = new TabPane();
        tp_vaults.setMinHeight(TAB_PANE_MIN_HEIGHT);
        tp_vaults.setMinWidth(TAB_PANE_MIN_WIDTH);

        t_newTabCreator = new Tab();

        t_newTabCreator.setClosable(false);
        t_newTabCreator.setLabelText("+");
        t_newTabCreator.setRenameEnabled(false);
        t_newTabCreator.setOnSelectionChanged(event ->
        {
            if (t_newTabCreator.isSelected()) AddLoginTab();
            event.consume();
        });

        tp_vaults.setOnContextMenuRequested(event ->
        {
            if (children.stream().anyMatch(p -> p.priority == WindowPriority.ALWAYS_ON_TOP)) stage.requestFocus();
        });

        tp_vaults.getTabs().add(t_newTabCreator);

        VaultManager.getInstance().setOnActiveVaultChanged((oldValue, newValue) -> {
            if (newValue == null) {
                setVaultControlsDisabled(true);
                if (oldValue != null)
                    oldValue.setOnDbStatusChanged(null);
                c_dbStatus.setFill(Color.GREY);
                return;
            }

            setVaultControlsDisabled(false);

            try {
                newValue.setOnDbStatusChanged(this::setDBStatus);
                setDBStatus(newValue.getDBStatus());
                Autologin.getInstance().check(newValue);
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }

            Logger.printDebug("Active vault changed from '" + (oldValue == null ? "null" : oldValue.getName())
                    + "' to '" + newValue.getName() + "'.");

            tp_vaults.getTabs().stream().filter(tab ->
                    tab.getContent() instanceof VaultTabContent
                            && ((VaultTabContent) tab.getContent()).hasVault(newValue)).limit(1).findAny().ifPresent(tab -> tp_vaults.getSelectionModel().select(tab));
        });

        try {
            VaultManager.getInstance().deactivateVault();
            for (int i = 0; i < VaultManager.getInstance().size(); ++i)
                AddVaultTab(VaultManager.getInstance().activateNextVault());

            tp_vaults.getTabs().remove(0);
        } catch (Exceptions e) {
            if (e.getCode() != XC.VAULTS_NOT_FOUND) throw e;
        }

        // ========== GRID ========== //
        grid.add(tp_vaults, 0, 0);
        grid.add(hb_statusBar, 0, 1);

        open();
    }

    private void setVaultControlsDisabled(boolean value) {
        m_vault.setDisable(value);
        if (value)
            tp_vaults.setContextMenu(cm_default);
        else
            tp_vaults.setContextMenu(cm_vault);
    }

    private void setDBStatus(Status status) {
        Logger.printDebug("DB status changed: " + status.name());
        switch (status) {
            default:
            case SYNCHRONIZATION_FAILED:
                c_dbStatus.setFill(Color.RED);
                tt_dbStatusText.setText(Texts.MSG_DB_SYNC_FAILED.toString());
                break;

            case SYNCHRONIZED:
                c_dbStatus.setFill(Color.LIGHTGREEN);
                tt_dbStatusText.setText(Texts.MSG_DB_SYNC_SUCCESS.toString());
                break;

            case SYNCHRONIZING:
            case DESYNCHRONIZED:
                c_dbStatus.setFill(Color.GREY);
                tt_dbStatusText.setText(Texts.MSG_DB_SYNC_UNAVAILABLE.toString());
                break;
        }
    }

    private void AddVaultTab(Vault vault) {
        Tab tab = AddTab();
        tab.setTabContent(new VaultTabContent(tab, vault));
        tp_vaults.getSelectionModel().select(tab);
    }

    // Called when user presses '+' tab or at the start when auto login is off
    private void AddLoginTab() {
        Tab tab = AddTab();
        tab.setTabContent(new LoginTabContents(tab));
        tp_vaults.getSelectionModel().select(tab);
    }

    private Tab AddTab() {
        Tab tab = new Tab();

        tab.setOnSelectionChanged(event ->
        {
            if (tab.isSelected() && tab.getContent() != null) ((TabContent) tab.getContent()).activateTab();
        });

        tab.setOnCloseRequest(event ->
        {
            ((TabContent) tab.getContent()).closeTab();
            tp_vaults.getTabs().get(tp_vaults.getTabs().size() - 1).setDisable(false);
        });

        tp_vaults.getTabs().add(Math.max(0, tp_vaults.getTabs().indexOf(t_newTabCreator)), tab);

        // Disable '+' tab, which is the last tab
        if (tp_vaults.getTabs().size() > Properties.CORE.VAULT.MAX_COUNT)
            tp_vaults.getTabs().get(tp_vaults.getTabs().size() - 1).setDisable(true);

        return tab;
    }

    public static void reload() throws Exceptions {
        try {
            Settings.getInstance().setRestartRequired(false);
        } catch (Exceptions e) {
            Logger.printError("Settings are not available!");
        }

        if (This != null) {
            This.close();
        }
        new FormVaultsManager();
    }

    public static synchronized void autoType(boolean altTab) {
        SpecialPassword sp = null;
        try {
            sp = VaultManager.getSelectedPassword();
        } catch (Exceptions e) {
            Terminator.terminate(e);
        }
        if (sp == null) {
            Logger.printError("No password selected");
            return;
        }

        KeyMapper.typeString(sp.getPassword(), altTab);
    }

    public static synchronized void copyToClipboard() {
        SpecialPassword sp = null;
        try {
            sp = VaultManager.getSelectedPassword();
        } catch (Exceptions e) {
            Terminator.terminate(e);
        }
        if (sp == null) {
            Logger.printError("No password selected");
            return;
        }

        String pwd = sp.getPassword();

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(pwd), null);

        if (tsk_pwdLifeTime != null) tsk_pwdLifeTime.cancel();

        tsk_pwdLifeTime = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(9, 10);

                int timeToLive = 0;

                try {
                    timeToLive = Settings.getInstance().getClipboardLiveTime();
                } catch (Exceptions e) {
                    Terminator.terminate(e);
                }

                try {
                    TrayAgent.getInstance().showNotification(Texts.TRAY_MSG_PWD_COPIED_TO_CLIPBOARD,
                            Texts.TRAY_MSG_TIME_LEFT, ": " + Settings.getInstance().getClipboardLiveTime() / 1000 + " "
                                    + Texts.LABEL_SECONDS.toString(),
                            MessageType.INFO);
                } catch (Exceptions e) {
                    Terminator.terminate(e);
                }

                for (int i = 0; i <= timeToLive && !isCancelled(); i += 100) {
                    updateProgress(timeToLive - i, timeToLive);
                    Thread.sleep(100);
                }

                return null;
            }
        };

        tsk_pwdLifeTime.setOnSucceeded(event ->
        {
            Logger.printTrace("PWDCLIP -> Successfully finished.");
            try {
                if (pwd.equals(clipboard.getData(DataFlavor.stringFlavor))) {
                    clipboard.setContents(new StringSelection(""), null);
                    TrayAgent.getInstance().showNotification(Texts.TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD,
                            MessageType.INFO);
                }
            } catch (UnsupportedFlavorException | IOException | Exceptions e) {
                Terminator.terminate(new Exceptions(XC.ERROR));
            }
        });

        tsk_pwdLifeTime.setOnCancelled(event -> Logger.printTrace("PWDCLIP -> Cancelled finished"));
        Thread calculatePasswordThread = new Thread(tsk_pwdLifeTime);
        calculatePasswordThread.setDaemon(false);
        calculatePasswordThread.start();
    }

    public EventHandler<ActionEvent> getOnNewAction() {
        return ae -> {
            try {
                new FormCreatePwd(This);
            } catch (Exceptions e) {
                if (e.getCode().equals(XC.FORM_ALREADY_OPEN))
                    ; // Ignore
                else
                    Logger.printError("Unhandled exception: " + e.getCode());
            }
        };
    }

    public EventHandler<ActionEvent> getOnEditAction() {
        return event -> {
            try {
                if (VaultManager.getSelectedPassword() == null) return;
                new FormEditPwd(This);
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
        };
    }

    public EventHandler<ActionEvent> getOnCopyAction() {
        return arg0 -> {
            try {
                if (VaultManager.getSelectedPassword() == null) return;
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
            This.minimize();
            copyToClipboard();
        };
    }

    public EventHandler<ActionEvent> getOnAutoType() {
        // FIXME: arg0 is funny, fix it to something fancy
        return arg0 -> {
            try {
                if (VaultManager.getSelectedPassword() == null) return;
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
            This.minimize();
            autoType(true);
        };
    }

    public EventHandler<ActionEvent> getOnDeleteAction() {
        return arg0 -> {
            try {
                if (VaultManager.getSelectedPassword() == null) return;
                new FormDeletePwd(This);
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
        };
    }

    public EventHandler<ActionEvent> getOnResetAction() {
        return arg0 -> {
            try {
                if (VaultManager.getSelectedPassword() == null) return;
                new FormResetPwd(This);
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
        };
    }

    public EventHandler<ActionEvent> getOnExportAction() {
        return event -> {
            try {
                new FormExport(This);
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
        };
    }

    // FIXME: Think something else maybe
    public void reloadDB() {
        ((TabContent) tp_vaults.getSelectionModel().getSelectedItem().getContent()).reload();
    }
}
