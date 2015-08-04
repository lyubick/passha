/**
 *
 */
package ui;

import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions;
import main.Settings;
import main.Terminator;
import ui.elements.Button;
import ui.elements.Button.BUTTON.SIZE;
import ui.elements.EntryField.TEXTFIELD;
import main.Exceptions.XC;
import db.PasswordCollection;
import db.SpecialPassword;
import db.iSpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;

/**
 * @author curious-odd-man
 *
 */
public class FormManagePwd extends AbstractForm
{

    private final class WINDOW
    {
        public static final int width  = 900;
        public static final int height = 500;
    }

    private final int tableMinHeight = WINDOW.height - 300;
    private final int tableMinWidth  = WINDOW.width - 200;

    private TableView<iSpecialPassword> table    = null;
    private TextField                   tf_pass  = null;
    private Button                      b_new    = null;
    private Button                      b_delete = null;
    private Button                      b_reset  = null;
    private Button                      b_copy   = null;
    private Button                      b_export = null;
    private Button                      b_edit   = null;

    static Task<Void> tsk_pwdLifeTime = null;

    private static ProgressIndicator pi_pwdLifeTime = null;

    private Menu     m_file      = null;
    private Menu     m_help      = null;
    private MenuItem mi_about    = null;
    private MenuItem mi_exit     = null;
    private MenuItem mi_settings = null;

    private static AbstractForm This = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnEditBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    if (PasswordCollection.getInstance().getSelected() == null) return;
                    new FormEditPwd(This);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnCopyToClipboardBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                minimize();
                copyToClipboard();
            }
        };
    }

    private EventHandler<ActionEvent> getOnNewBtnAction()
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

    private EventHandler<ActionEvent> getOnDeleteBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    if (PasswordCollection.getInstance().getSelected() == null) return;
                    new FormDeletePwd(This);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnResetBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    if (PasswordCollection.getInstance().getSelected() == null) return;
                    new FormResetPwd(This);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnExportBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                new DlgExport(This);
            }
        };
    }

    private ChangeListener<Object> getSelectedItemPropertyListener()
    {
        return new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue,
                    Object newValue)
            {
                try
                {
                    if (newValue == null)
                    {
                        PasswordCollection.getInstance().setSelected(null);
                        return;
                    }

                    PasswordCollection.getInstance()
                            .setSelected(table.getSelectionModel().getSelectedItem().getOrigin());
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                b_copy.setDisable(true);
                tf_pass.setText(table.getSelectionModel().getSelectedItem().getPassword());
                b_copy.setDisable(false);
            }
        };
    }

    private ChangeListener<Boolean> getFocusedPropertyListener()
    {
        return new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                if (newValue) refresh();
            }
        };
    }

    private EventHandler<WindowEvent> getOnFormShowing()
    {
        return new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                refresh();
            }
        };
    }

    /* PUBLIC ROUTINE */
    // FIXME: move maybe?
    public static synchronized void copyToClipboard()
    {
        SpecialPassword sp = null;
        try
        {
            sp = PasswordCollection.getInstance().getSelected();
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
                    TrayAgent.getInstance().showNotification(
                            TextID.TRAY_MSG_PWD_COPIED_TO_CLIPBOARD.toString(),
                            TextID.TRAY_MSG_TIME_LEFT.toString() + ": "
                                    + Settings.getInstance().getClipboardLiveTime() / 1000 + " "
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

        pi_pwdLifeTime.progressProperty().bind(tsk_pwdLifeTime.progressProperty());

        tsk_pwdLifeTime.setOnSucceeded(EventHandler -> {
            pi_pwdLifeTime.progressProperty().unbind();
            Logger.printDebug("PWDCLIP -> Successfully finished.");
            pi_pwdLifeTime.setVisible(false);
            try
            {
                TrayAgent.getInstance().showNotification(
                        TextID.TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD.toString(), "",
                        MessageType.INFO);
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }
            try
            {
                if (pwd.equals(clipboard.getData(DataFlavor.stringFlavor)))
                    clipboard.setContents(new StringSelection(""), null);
            }
            catch (UnsupportedFlavorException | IOException e)
            {
                Terminator.terminate(new Exceptions(XC.ERROR));
            }
        });

        tsk_pwdLifeTime.setOnCancelled(EventHandler -> {
            Logger.printDebug("PWDCLIP -> Cancelled finished");
            pi_pwdLifeTime.setVisible(false);
        });
        Thread calculatePasswordThread = new Thread(tsk_pwdLifeTime);
        calculatePasswordThread.setDaemon(false);
        calculatePasswordThread.start();

        pi_pwdLifeTime.setVisible(true);
    }

    public static FormManagePwd getInstance() throws Exceptions
    {
        if (This == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return (FormManagePwd) This;
    }

    public static void reload()
    {
        if (This != null)
        {
            ((FormManagePwd) This).close();
        }
        new FormManagePwd();
    }

    public FormManagePwd()
    {
        super(null, TextID.FORM_MANAGEPWD_NAME.toString()); // No parents it
                                                            // is a main Form

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);
        stage.setMinHeight(WINDOW.height);
        stage.setMinWidth(WINDOW.width);

        This = this;

        // ========== CSS ========== //
        scene.getStylesheets().add("resources/progress.css");

        // ========== MENU ========== //
        // TODO: create class for menu
        mb_main = new MenuBar();
        m_file = new Menu(TextID.MENU_LABEL_FILE.toString());
        m_help = new Menu(TextID.MENU_LABEL_HELP.toString());

        mi_settings = new MenuItem(TextID.FORM_SETTINGS_NAME.toString());
        mi_exit = new MenuItem(TextID.MENU_LABEL_EXIT.toString());
        mi_about = new MenuItem(TextID.MENU_LABEL_ABOUT.toString());

        m_file.getItems().addAll(mi_settings, mi_exit);
        m_help.getItems().addAll(mi_about);
        mb_main.getMenus().addAll(m_file, m_help);

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
        group.getChildren().addAll(mb_main, grid);

        // ========== HRENJ ========== //
        pi_pwdLifeTime = new ProgressIndicator(0);
        pi_pwdLifeTime.setId("pi_css");
        pi_pwdLifeTime.setMinSize(50, 50);
        pi_pwdLifeTime.setMaxSize(50, 50);
        pi_pwdLifeTime.setVisible(false);

        GridPane.setMargin(pi_pwdLifeTime, new Insets(20, 0, 0, 210));

        // ========== BUTTONS ========== //
        // FIXME LANG shortcuts
        b_new = new Button("_" + TextID.COMMON_LABEL_NEW.toString(), SIZE.L);
        b_delete = new Button("_" + TextID.FORM_CREATEPWD_MANAGER_LABEL_DELETE.toString(), SIZE.L);
        b_copy = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD.toString(), SIZE.L);
        b_export = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_EXPORT.toString(), SIZE.L);
        b_reset = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_RESET.toString(), SIZE.L);
        b_edit = new Button("_" + TextID.FORM_MANAGEWD_LABEL_EDIT.toString(), SIZE.L);

        // ========== TABLE ========== //
        table = new TableView<iSpecialPassword>();

        TableColumn<iSpecialPassword, String> cName = new TableColumn<iSpecialPassword, String>(
                TextID.FORM_MANAGEPWD_LABEL_PWD_NAME.toString());
        TableColumn<iSpecialPassword, String> cComment = new TableColumn<iSpecialPassword, String>(
                TextID.FORM_CREATEPWD_LABEL_COMMENT.toString());
        TableColumn<iSpecialPassword, String> cUrl = new TableColumn<iSpecialPassword, String>(
                TextID.FORM_CREATEPWD_LABEL_URL.toString());

        TableColumn<iSpecialPassword, String> cShortcut = new TableColumn<iSpecialPassword, String>(
                TextID.FORM_EDITPWD_LABEL_SHORTCUT.toString());

        table.getColumns().add(cName);
        table.getColumns().add(cComment);
        table.getColumns().add(cUrl);
        table.getColumns().add(cShortcut);

        cName.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("name"));
        cComment.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("comment"));
        cUrl.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("url"));
        cShortcut.setCellValueFactory(
                new PropertyValueFactory<iSpecialPassword, String>("shortcut"));

        table.setMinHeight(tableMinHeight);
        table.setMinWidth(tableMinWidth);

        b_export.setDisable(false);
        b_copy.setDisable(true);
        b_reset.setDisable(false);

        GridPane.setValignment(b_new, VPos.TOP);
        GridPane.setValignment(b_delete, VPos.TOP);
        GridPane.setHalignment(b_copy, HPos.LEFT);
        GridPane.setValignment(b_export, VPos.BOTTOM);
        GridPane.setValignment(b_edit, VPos.TOP);

        GridPane.setMargin(b_edit, new Insets(40, 0, 0, 0));
        GridPane.setMargin(b_delete, new Insets(80, 0, 0, 0));
        GridPane.setMargin(b_copy, new Insets(0, 0, 0, 270));

        // ========== TEXT FIELD ========== //
        tf_pass = new TextField();
        tf_pass.setMaxWidth(TEXTFIELD.WIDTH.L);
        tf_pass.setEditable(false);

        // ========== GRID ========== //
        grid.add(table, 0, 0);

        grid.add(pi_pwdLifeTime, 0, 1);
        grid.add(tf_pass, 0, 1);
        grid.add(b_copy, 0, 1);

        grid.add(b_export, 1, 0);
        grid.add(b_new, 1, 0);
        grid.add(b_edit, 1, 0);
        grid.add(b_delete, 1, 0);
        grid.add(b_reset, 1, 0);

        try
        {
            Button.setButtonShortcut(b_new,
                    new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
            Button.setButtonShortcut(b_delete,
                    new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));
            Button.setButtonShortcut(b_copy,
                    new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
            Button.setButtonShortcut(b_export,
                    new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
            Button.setButtonShortcut(b_reset,
                    new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        b_new.setOnAction(getOnNewBtnAction());
        b_edit.setOnAction(getOnEditBtnAction());
        b_delete.setOnAction(getOnDeleteBtnAction());
        b_reset.setOnAction(getOnResetBtnAction());
        b_export.setOnAction(getOnExportBtnAction());
        b_copy.setOnAction(getOnCopyToClipboardBtnAction());

        table.getSelectionModel().selectedItemProperty()
                .addListener(getSelectedItemPropertyListener());

        stage.setOnShowing(getOnFormShowing());

        stage.focusedProperty().addListener(getFocusedPropertyListener());

        open();
    }

    public void refresh()
    {
        b_copy.setDisable(true);
        tf_pass.clear();
        table.getSelectionModel().clearSelection();
        try
        {
            table.setItems(PasswordCollection.getInstance().getIface());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }

    @Override
    public void onUserCloseRequest()
    {
        minimize();
    }

}
