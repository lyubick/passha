package ui;

import core.VaultManager;

import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import db.SpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import languages.Local.TextID;
import logger.Logger;

import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import main.Properties;
import main.Settings;

import ui.elements.Button;
import ui.elements.Button.BUTTON.SIZE;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.LoginTabContents;
import ui.elements.Tab;
import ui.elements.TabContent;

public class FormVaultsManager extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 900;
        public static final int height = 500;
    }

    private final int           tabpaneMinHeight = WINDOW.height - 150;
    private final int           tabpaneMinWidth  = WINDOW.width - 200;

    private static AbstractForm This             = null;

    private VBox                vb_VaultButtons  = null;

    private TextField           tf_pass          = null;

    private Button              b_new            = null;
    private Button              b_delete         = null;
    private Button              b_reset          = null;
    private Button              b_copy           = null;
    private Button              b_export         = null;
    private Button              b_edit           = null;

    private Menu                m_file           = null;
    private Menu                m_vault          = null;
    private Menu                m_password       = null;
    private Menu                m_help           = null;

    private MenuItem            mi_about         = null;
    private MenuItem            mi_exit          = null;
    private MenuItem            mi_new           = null;
    private MenuItem            mi_edit          = null;
    private MenuItem            mi_reset         = null;
    private MenuItem            mi_delete        = null;
    private MenuItem            mi_settings      = null;

    private TabPane             tp_vaults        = null;

    static Task<Void>           tsk_pwdLifeTime  = null;

    public static FormVaultsManager getInstance() throws Exceptions
    {
        if (This == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return (FormVaultsManager) This;
    }

    public FormVaultsManager()
    {
        super(null, TextID.FORM_MANAGEPWD_NAME.toString()); // No parents it
        // is a main Form

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);
        stage.setMinHeight(WINDOW.height);
        stage.setMinWidth(WINDOW.width);
        stage.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValule, Boolean newValue)
            {
                // FIXME: Create TabPane class
                if (newValue) ((TabContent) tp_vaults.getSelectionModel().getSelectedItem().getContent()).refreshTab();
            }
        });

        This = this;

        // ========== MENU ========== //
        // TODO: create class for menu
        menuMain = new MenuBar();
        m_file = new Menu(TextID.MENU_LABEL_FILE.toString());
        m_vault = new Menu("Vault"); // FIXME: local
        m_password = new Menu("Password"); // FIXME: local
        m_help = new Menu(TextID.MENU_LABEL_HELP.toString());

        mi_settings = new MenuItem(TextID.FORM_SETTINGS_NAME.toString());
        mi_exit = new MenuItem(TextID.MENU_LABEL_EXIT.toString());

        mi_new = new MenuItem(TextID.COMMON_LABEL_NEW.toString());
        mi_edit = new MenuItem(TextID.FORM_MANAGEWD_LABEL_EDIT.toString());
        mi_reset = new MenuItem(TextID.FORM_MANAGEPWD_LABEL_RESET.toString());
        mi_delete = new MenuItem(TextID.FORM_CREATEPWD_MANAGER_LABEL_DELETE.toString());

        mi_about = new MenuItem(TextID.MENU_LABEL_ABOUT.toString());

        m_file.getItems().addAll(mi_settings, mi_exit);
        m_vault.getItems().addAll(m_password);
        m_password.getItems().addAll(mi_new, mi_edit, mi_reset, mi_delete);
        m_help.getItems().addAll(mi_about);
        menuMain.getMenus().addAll(m_file, m_vault, m_help);

        mi_settings.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                new FormSettings(This);
            }
        });

        mi_exit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Terminator.terminate(new Exceptions(XC.END));
            }
        });

        mi_new.setOnAction(getOnNewAction());
        mi_edit.setOnAction(getOnEditAction());
        mi_reset.setOnAction(getOnResetAction());
        mi_delete.setOnAction(getOnDeleteAction());

        mi_about.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                new FormAbout(This);
            }
        });

        // ========== REFRESH ========== //
        group.getChildren().remove(grid);
        group.getChildren().addAll(menuMain, grid);

        // ========== BUTTONS ========== //
        vb_VaultButtons = new VBox();

        // FIXME LANG shortcuts
        b_new = new Button("_" + TextID.COMMON_LABEL_NEW.toString(), SIZE.L);
        b_delete = new Button("_" + TextID.FORM_CREATEPWD_MANAGER_LABEL_DELETE.toString(), SIZE.L);
        b_copy = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD.toString(), SIZE.L);
        b_export = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_EXPORT.toString(), SIZE.L);
        b_reset = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_RESET.toString(), SIZE.L);
        b_edit = new Button("_" + TextID.FORM_MANAGEWD_LABEL_EDIT.toString(), SIZE.L);

        b_new.setOnAction(getOnNewAction());
        b_edit.setOnAction(getOnEditAction());
        b_delete.setOnAction(getOnDeleteAction());
        b_reset.setOnAction(getOnResetAction());
        b_export.setOnAction(getOnExportAction());
        b_copy.setOnAction(getOnCopyAction());

        VBox.setMargin(b_new, new Insets(10, 10, 0, 10));
        VBox.setMargin(b_edit, new Insets(10, 10, 0, 10));

        VBox.setMargin(b_reset, new Insets(50, 10, 0, 10));

        VBox.setMargin(b_delete, new Insets(50, 10, 0, 10));

        VBox.setMargin(b_copy, new Insets(50, 10, 0, 10));
        VBox.setMargin(b_export, new Insets(10, 10, 0, 10));

        b_copy.setDisable(true);

        vb_VaultButtons.getChildren().add(b_new);
        vb_VaultButtons.getChildren().add(b_edit);
        vb_VaultButtons.getChildren().add(b_reset);
        vb_VaultButtons.getChildren().add(b_delete);
        vb_VaultButtons.getChildren().add(b_copy);
        vb_VaultButtons.getChildren().add(b_export);

        // ========== TEXT FIELD ========== //
        tf_pass = new TextField();
        tf_pass.setMaxWidth(TEXTFIELD.WIDTH.L);
        tf_pass.setEditable(false);

        // ========== TAB pane =========== //

        tp_vaults = new TabPane();
        tp_vaults.setMinHeight(tabpaneMinHeight);
        tp_vaults.setMinWidth(tabpaneMinWidth);

        Tab t_newTabCreator = new Tab();

        t_newTabCreator.setClosable(false);
        t_newTabCreator.setLabelText("+");
        t_newTabCreator.setRenameEnabled(false);
        t_newTabCreator.setOnSelectionChanged(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                if (t_newTabCreator.isSelected()) AddTab();
                event.consume();
            }

        });
        tp_vaults.getTabs().add(t_newTabCreator);

        // ========== GRID ========== //
        grid.add(tp_vaults, 0, 0);

        grid.add(tf_pass, 0, 1);

        grid.add(vb_VaultButtons, 1, 0);

        open();
    }

    public void setVaultControlsDisabled(boolean value)
    {
        m_vault.setDisable(value);
        vb_VaultButtons.setDisable(value);
        b_copy.setDisable(value);
    }

    // called when user presses '+' tab or at the start when auto login is off
    private void AddTab()
    {
        Tab tab = new Tab();

        tab.setOnSelectionChanged(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                if (tab.isSelected()) ((TabContent) tab.getContent()).activateTab();
            }
        });

        tab.setOnCloseRequest(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                ((TabContent) tab.getContent()).closeTab();
                tp_vaults.getTabs().get(tp_vaults.getTabs().size() - 1).setDisable(false);

            }
        });

        tp_vaults.getTabs().add(Math.max(0, tp_vaults.getTabs().size() - 1), tab);

        tab.setContent(new LoginTabContents(tab, This));

        // Disable '+' tab, which is the last tab
        if (tp_vaults.getTabs().size() > Properties.CORE.VAULT.MAX_COUNT)
            tp_vaults.getTabs().get(tp_vaults.getTabs().size() - 1).setDisable(true);

        tp_vaults.getSelectionModel().select(tab);
    }

    public static void reload()
    {
        try
        {
            Settings.getInstance().setRestartRequired(false);
        }
        catch (Exceptions e)
        {
            Logger.printError("Settings are not available!");
        }

        if (This != null)
        {
            ((FormVaultsManager) This).close();
        }
        new FormVaultsManager();
    }

    public static synchronized void copyToClipboard()
    {
        SpecialPassword sp = null;
        try
        {
            sp = VaultManager.getSelectedPassword();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
        if (sp == null)
        {
            Logger.printError("No password selected");
            return;
        }

        String pwd = sp.getPassword();

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(pwd), null);

        if (tsk_pwdLifeTime != null) tsk_pwdLifeTime.cancel();

        tsk_pwdLifeTime = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {

                updateProgress(9, 10);

                int timeToLive = 0;

                try
                {
                    timeToLive = Settings.getInstance().getClipboardLiveTime();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                try
                {
                    TrayAgent.getInstance().showNotification(TextID.TRAY_MSG_PWD_COPIED_TO_CLIPBOARD,
                        TextID.TRAY_MSG_TIME_LEFT, ": " + Settings.getInstance().getClipboardLiveTime() / 1000 + " "
                            + TextID.COMMON_LABEL_SECONDS.toString(),
                        MessageType.INFO);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                for (int i = 0; i <= timeToLive && !isCancelled(); i += 100)
                {
                    updateProgress(timeToLive - i, timeToLive);
                    Thread.sleep(100);
                }

                return null;
            }
        };

        tsk_pwdLifeTime.setOnSucceeded(EventHandler ->
        {
            Logger.printDebug("PWDCLIP -> Successfully finished.");
            try
            {
                if (pwd.equals(clipboard.getData(DataFlavor.stringFlavor)))
                {
                    clipboard.setContents(new StringSelection(""), null);
                    TrayAgent.getInstance().showNotification(TextID.TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD,
                        MessageType.INFO);
                }
            }
            catch (UnsupportedFlavorException | IOException | Exceptions e)
            {
                Terminator.terminate(new Exceptions(XC.ERROR));
            }
        });

        tsk_pwdLifeTime.setOnCancelled(EventHandler ->
        {
            Logger.printDebug("PWDCLIP -> Cancelled finished");
        });
        Thread calculatePasswordThread = new Thread(tsk_pwdLifeTime);
        calculatePasswordThread.setDaemon(false);
        calculatePasswordThread.start();
    }

    public EventHandler<ActionEvent> getOnNewAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent ae)
            {
                new FormCreatePwd(This);
            }
        };
    }

    public EventHandler<ActionEvent> getOnEditAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    if (VaultManager.getSelectedPassword() == null) return;
                    new FormEditPwd(This);
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    public EventHandler<ActionEvent> getOnCopyAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printError("Feature is not implemented yet!!! PICHALJKA");
                // parent.minimize();
                // copyToClipboard(); // TODO: pass password to copy
            }
        };
    }

    public EventHandler<ActionEvent> getOnDeleteAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    if (VaultManager.getSelectedPassword() == null) return;
                    new FormDeletePwd(This);
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };
    }

    public EventHandler<ActionEvent> getOnResetAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    if (VaultManager.getSelectedPassword() == null) return;
                    new FormResetPwd(This);
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };
    }

    public EventHandler<ActionEvent> getOnExportAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Logger.printError("Feature is causing SEGV on ubuntu!!! PICHALJKA");
                // new DlgExport(This);
            }
        };
    }

}
