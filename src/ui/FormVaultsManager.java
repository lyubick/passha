package ui;

import core.VaultManager;

import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Optional;

import db.SpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import languages.Local.Texts;
import logger.Logger;

import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import main.Properties;
import main.Settings;

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

    private ContextMenu         cm_vault         = null;
    private ContextMenu         cm_default       = null;

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
    private MenuItem            mi_copy          = null;
    private MenuItem            mi_export        = null;

    private TabPane             tp_vaults        = null;

    static Task<Void>           tsk_pwdLifeTime  = null;

    public static FormVaultsManager getInstance() throws Exceptions
    {
        if (This == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return (FormVaultsManager) This;
    }

    public FormVaultsManager() throws Exceptions
    {
        // No parents it is a main Form
        super(null, Texts.FORM_MANAGEPWD_NAME, WindowPriority.ONLY_ONE_OPENED);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);
        stage.setMinHeight(WINDOW.height);
        stage.setMinWidth(WINDOW.width);
        stage.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValule, Boolean newValue)
            {
                if (newValue) ((TabContent) tp_vaults.getSelectionModel().getSelectedItem().getContent()).reload();
            }
        });

        This = this;

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
        mi_export = new MenuItem(Texts.FORM_MANAGEPWD_LABEL_EXPORT.toString());

        mi_about = new MenuItem(Texts.LABEL_ABOUT.toString());

        m_file.getItems().addAll(mi_settings, new SeparatorMenuItem(), mi_exit);
        m_vault.getItems().addAll(m_password, mi_export);
        m_password.getItems().addAll(mi_new, mi_edit, mi_delete, new SeparatorMenuItem(), mi_reset,
            new SeparatorMenuItem(), mi_copy);
        m_help.getItems().addAll(mi_about);
        menuMain.getMenus().addAll(m_file, m_vault, m_help);

        mi_settings.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    new FormSettings(This);
                }
                catch (Exceptions e)
                {
                    if (e.getCode().equals(XC.FORM_ALREADY_OPEN))
                        ; // Ignore
                    else
                        Logger.printError("Unhandled exception: " + e.getCode());
                }
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
        mi_copy.setOnAction(getOnCopyAction());

        mi_copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));

        mi_about.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    new FormAbout(This);
                }
                catch (Exceptions e)
                {
                    if (e.getCode().equals(XC.FORM_ALREADY_OPEN))
                        ; // Ignore
                    else
                        Logger.printError("Unhandled exception: " + e.getCode());
                }
            }
        });

        cm_vault = new ContextMenu();
        cm_vault.getItems().addAll(m_password.getItems());

        // ========== REFRESH ========== //
        group.getChildren().remove(grid);
        group.getChildren().addAll(menuMain, grid);

        // ========== TAB pane =========== //

        tp_vaults = new TabPane();
        tp_vaults.setMinHeight(tabpaneMinHeight);
        tp_vaults.setMinWidth(tabpaneMinWidth);

        tp_vaults.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue) ((TabContent) tp_vaults.getSelectionModel().getSelectedItem().getContent()).activateTab();
            }
        });

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

        tp_vaults.setOnContextMenuRequested(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                Optional<AbstractForm> form =
                    childs.stream().filter(p -> p.priority == WindowPriority.ALWAYS_ON_TOP).findFirst();

                if (form.isPresent())
                {
                    stage.requestFocus();
                }
            }
        });

        tp_vaults.getTabs().add(t_newTabCreator);

        // ========== GRID ========== //
        grid.add(tp_vaults, 0, 0);

        open();
    }

    public void setVaultControlsDisabled(boolean value)
    {
        m_password.setDisable(value);
        mi_export.setDisable(value);
        if (value)
            tp_vaults.setContextMenu(cm_default);
        else
            tp_vaults.setContextMenu(cm_vault);
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
                if (tab.isSelected())
                {
                    ((TabContent) tab.getContent()).activateTab();
                }
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

    public static void reload() throws Exceptions
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
                    TrayAgent.getInstance().showNotification(Texts.TRAY_MSG_PWD_COPIED_TO_CLIPBOARD,
                        Texts.TRAY_MSG_TIME_LEFT, ": " + Settings.getInstance().getClipboardLiveTime() / 1000 + " "
                            + Texts.LABEL_SECONDS.toString(),
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
                    TrayAgent.getInstance().showNotification(Texts.TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD,
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
                try
                {
                    new FormCreatePwd(This);
                }
                catch (Exceptions e)
                {
                    if (e.getCode().equals(XC.FORM_ALREADY_OPEN))
                        ; // Ignore
                    else
                        Logger.printError("Unhandled exception: " + e.getCode());
                }
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
                    Terminator.terminate(e);
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
                This.minimize();
                copyToClipboard();
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
                    Terminator.terminate(e);
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
                    Terminator.terminate(e);
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
