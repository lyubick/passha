/**
 *
 */
package ui;

import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions;
import main.Settings;
import main.Terminator;
import main.Exceptions.XC;
import ui.Controller.FORMS;
import db.PasswordCollection;
import db.iSpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author curious-odd-man
 *
 */
public class ManagePasswordsForm extends AbstractForm
{

    private final class WINDOW
    {
        public static final int width  = 900;
        public static final int height = 500;
    }

    private final int                   tableMinHeight  = WINDOW.height - 300;
    private final int                   tableMinWidth   = WINDOW.width - 200;

    private TableView<iSpecialPassword> table           = null;
    private TextField                   tf_pass         = null;
    private Button                      b_Save          = null;
    private Button                      b_Discard       = null;
    private Button                      b_New           = null;
    private Button                      b_Delete        = null;
    private Button                      b_Reset         = null;
    private Button                      b_Copy          = null;
    private Button                      b_Export        = null;

    Task<Void>                          tsk_PWDLifeTime = null;

    private ProgressIndicator           pi_PWDLifeTime  = null;

    private MenuBar                     mb_Main         = null;
    private Menu                        m_File          = null;
    private MenuItem                    mi_Exit         = null;
    private MenuItem                    mi_Settings     = null;
    private ProgressBar                 pb_Progress     = null;
    private Label                       l_Progress      = null;

    private boolean                     firstTime       = true;

    private Stage                       parrent         = null;

    private void setStatusBarShow(boolean v)
    {
        pb_Progress.setVisible(v);
        l_Progress.setVisible(v);
    }

    private void setButtonsEnabled()
    {
        handleButtons();
        b_New.setDisable(false);
        b_Delete.setDisable(false);
        b_Reset.setDisable(false);
        b_Copy.setDisable(false);
        b_Export.setDisable(false);
    }

    private void setButtonsDisabled()
    {
        b_Save.setDisable(true);
        b_Discard.setDisable(true);
        b_New.setDisable(true);
        b_Delete.setDisable(true);
        b_Reset.setDisable(true);
        b_Copy.setDisable(true);
        b_Export.setDisable(true);
    }

    private void handleButtons()
    {
        try
        {
            b_Save.setDisable(!PasswordCollection.getInstance().isChanged());
            b_Discard.setDisable(!PasswordCollection.getInstance().isChanged());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }

    public void reloadAllItems()
    {
        l_Progress.setText(TextID.LOADING.toString() + ": ");
        setStatusBarShow(true);
        setButtonsDisabled();

        try
        {
            Task<Void> reloadTask = PasswordCollection.getInstance().reload();

            pb_Progress.progressProperty().bind(reloadTask.progressProperty());

            reloadTask.setOnSucceeded(EventHandler -> {
                pb_Progress.progressProperty().unbind();
                Logger.printDebug("reloadTask -> successfully finished");
                setStatusBarShow(false);
                setButtonsEnabled();
                try
                {
                    table.setItems(PasswordCollection.getInstance().getIface());
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

            Thread reloadTaskThread = new Thread(reloadTask);
            reloadTaskThread.start();

            handleButtons();

        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ManagePasswordsForm()
    {
        // ========== CSS ========== //
        scene.getStylesheets().add("resources/progress.css"); // TODO

        // ========== MENU ========== //
        mb_Main = new MenuBar();
        m_File = new Menu(TextID.FILE.toString());

        mi_Settings = new MenuItem(TextID.SETTINGS.toString());
        mi_Exit = new MenuItem(TextID.EXIT.toString());

        m_File.getItems().addAll(mi_Settings, mi_Exit);
        mb_Main.getMenus().add(m_File);

        mi_Settings.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    Controller.getInstance().switchForm(FORMS.SETTINGS);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        mi_Exit.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {
                Terminator.terminate(new Exceptions(XC.END));
            }
        });

        // ========== STATUS ========== //
        pb_Progress = new ProgressBar();
        l_Progress = new Label(TextID.LOADING.toString() + ": ");

        HBox statusBar = new HBox();

        statusBar.setAlignment(Pos.BOTTOM_RIGHT);

        statusBar.getChildren().addAll(l_Progress, pb_Progress);

        // ========== REFRESH ========== //
        group.getChildren().remove(grid); // TODO
        group.getChildren().addAll(mb_Main, grid, statusBar);

        // ========== HRENJ ========== //
        pi_PWDLifeTime = new ProgressIndicator(0);
        pi_PWDLifeTime.setId("pi_css");
        pi_PWDLifeTime.setMinSize(50, 50);
        pi_PWDLifeTime.setMaxSize(50, 50);
        pi_PWDLifeTime.setVisible(false);

        GridPane.setMargin(pi_PWDLifeTime, new Insets(20, 0, 0, 210));

        // ========== BUTTONS ========== //
        // TODO LANG shortcuts
        b_New = getButton("_" + TextID.NEW.toString());
        b_Delete = getButton("_" + TextID.DELETE.toString());
        b_Copy = getButton("_" + TextID.COPY_CLIPBOARD.toString());
        b_Export = getButton("_" + TextID.EXPORT.toString());
        b_Save = getButton("_" + TextID.SAVE.toString());
        b_Discard = getButton("_" + TextID.DISCARD.toString());
        b_Reset = getButton("_" + TextID.RESET_PASSWORD.toString());

        // ========== TABLE ========== //
        table = new TableView<iSpecialPassword>();

        TableColumn<iSpecialPassword, String> cName =
                new TableColumn<iSpecialPassword, String>(TextID.PWD_NAME.toString());
        TableColumn<iSpecialPassword, String> cComment =
                new TableColumn<iSpecialPassword, String>(TextID.COMMENT.toString());
        TableColumn<iSpecialPassword, String> cUrl = new TableColumn<iSpecialPassword, String>(TextID.URL.toString());

        table.getColumns().add(cName);
        table.getColumns().add(cComment);
        table.getColumns().add(cUrl);

        cName.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("name"));
        cComment.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("comment"));
        cUrl.setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("url"));

        table.setMinHeight(tableMinHeight);
        table.setMinWidth(tableMinWidth);

        b_Export.setDisable(false);
        b_Save.setDisable(true);
        b_Discard.setDisable(true);
        b_Copy.setDisable(true);
        b_Reset.setDisable(false);

        GridPane.setValignment(b_New, VPos.TOP);
        GridPane.setValignment(b_Delete, VPos.TOP);
        GridPane.setHalignment(b_Copy, HPos.LEFT);
        GridPane.setValignment(b_Save, VPos.BOTTOM);
        GridPane.setValignment(b_Discard, VPos.BOTTOM);
        GridPane.setValignment(b_Export, VPos.BOTTOM);

        GridPane.setMargin(b_Delete, new Insets(40, 0, 0, 0));
        GridPane.setMargin(b_Save, new Insets(0, 0, 40, 0));
        GridPane.setMargin(b_Discard, new Insets(0, 0, 80, 0));
        GridPane.setMargin(b_Copy, new Insets(0, 0, 0, 270));

        // ========== TEXT FIELD ========== //
        tf_pass = new TextField();
        tf_pass.setMaxWidth(FIELD_WIDTH_PWD);
        tf_pass.setEditable(false);

        // ========== GRID ========== //
        grid.add(table, 0, 0);

        grid.add(pi_PWDLifeTime, 0, 1);
        grid.add(tf_pass, 0, 1);
        grid.add(b_Copy, 0, 1);

        grid.add(b_Export, 1, 0);
        grid.add(b_New, 1, 0);
        grid.add(b_Delete, 1, 0);
        grid.add(b_Save, 1, 0);
        grid.add(b_Discard, 1, 0);
        grid.add(b_Reset, 1, 0);

        try
        {
            setButtonShortcut(b_New, new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Delete, new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Copy, new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Export, new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Save, new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Discard, new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Reset, new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        b_New.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent ae)
            {
                try
                {
                    Controller.getInstance().switchForm(FORMS.CREATE_PWD);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                handleButtons();
            }
        });

        b_Delete.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                // TODO Confirmation!
                iSpecialPassword pwd = table.getSelectionModel().getSelectedItem();
                if (pwd != null)
                {
                    table.getItems().remove(pwd);
                    try
                    {
                        PasswordCollection.getInstance().removePassword(pwd.getOrigin());
                    }
                    catch (Exceptions e)
                    {
                        Terminator.terminate(e);
                    }
                }

                handleButtons();
            }

        });

        b_Save.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                l_Progress.setText(TextID.SAVING.toString() + ": ");
                setStatusBarShow(true);
                setButtonsDisabled();

                try
                {
                    Task<Void> saveTask = PasswordCollection.getInstance().save();

                    pb_Progress.progressProperty().bind(saveTask.progressProperty());

                    saveTask.setOnSucceeded(EventHandler -> {
                        pb_Progress.progressProperty().unbind();
                        setStatusBarShow(false);
                        setButtonsEnabled();
                        Logger.printDebug("saveTask -> successfully finished");
                    });

                    Thread reloadTaskThread = new Thread(saveTask);
                    reloadTaskThread.start();

                    handleButtons();

                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
                handleButtons();
            }
        });

        b_Discard.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent arg0)
            {
                reloadAllItems();
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>()
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

                    PasswordCollection.getInstance().setSelected(
                            table.getSelectionModel().getSelectedItem().getOrigin());
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                b_Copy.setDisable(true);
                tf_pass.setText(table.getSelectionModel().getSelectedItem().getPassword());
                b_Copy.setDisable(false);
            }
        });

        b_Reset.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    if (PasswordCollection.getInstance().getSelected() == null) return;
                    Controller.getInstance().switchForm(FORMS.CHANGE_PWD);
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        b_Export.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    // TODO if any changes in db - ask user to save or discard
                    // first
                    Controller.getInstance().switchForm(FORMS.EXPORT);
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        b_Copy.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                if (tf_pass.getText().length() == 0) return;

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(tf_pass.getText()), null);

                if (tsk_PWDLifeTime != null) tsk_PWDLifeTime.cancel();

                tsk_PWDLifeTime = new Task<Void>()
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
                        catch (Exceptions e1)
                        {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        for (int i = 0; i <= timeToLive && !isCancelled(); i += 100)
                        {
                            updateProgress(timeToLive - i, timeToLive);
                            Thread.sleep(100);

                            if (i % 1000 == 0)
                            {
                                try
                                {
                                    TrayAgent.getInstance().showNotification(
                                            TextID.PASSWORD_COPIED.toString(),
                                            TextID.TIME_LEFT.toString() + ": "
                                                    + ((Settings.getInstance().getClipboardLiveTime() - i) / 1000)
                                                    + " " + TextID.S.toString(), MessageType.INFO);
                                }
                                catch (Exceptions e)
                                {
                                    Terminator.terminate(e);
                                }
                            }
                        }

                        return null;
                    }
                };

                pi_PWDLifeTime.progressProperty().bind(tsk_PWDLifeTime.progressProperty());

                tsk_PWDLifeTime.setOnSucceeded(EventHandler -> {
                    pi_PWDLifeTime.progressProperty().unbind();
                    Logger.printDebug("PWDCLIP -> Successfully finished.");
                    pi_PWDLifeTime.setVisible(false);
                    try
                    {
                        TrayAgent.getInstance().showNotification(TextID.PASSWORD_REMOVED.toString(), "",
                                MessageType.INFO);
                    }
                    catch (Exceptions e)
                    {
                        Terminator.terminate(e);
                    }
                    try
                    {
                        if (tf_pass.getText().equals(clipboard.getData(DataFlavor.stringFlavor)))
                            clipboard.setContents(new StringSelection(""), null);
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });

                tsk_PWDLifeTime.setOnCancelled(EventHandler -> {
                    tf_pass.textProperty().unbind();
                    Logger.printDebug("PWDCLIP -> Cancelled finished");
                    pi_PWDLifeTime.setVisible(false);
                });

                Thread calculatePasswordThread = new Thread(tsk_PWDLifeTime);
                calculatePasswordThread.setDaemon(false);
                calculatePasswordThread.start();

                pi_PWDLifeTime.setVisible(true);
                parrent.setIconified(true);
            }
        });
    }

    @Override
    public void draw(Stage stage) throws Exceptions
    {
        parrent = stage;

        tf_pass.clear();
        handleButtons();
        table.setItems(PasswordCollection.getInstance().getIface());

        stage.setTitle(TextID.PROGRAM_NAME.toString() + " " + TextID.VERSION.toString());

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);
        stage.setMinHeight(WINDOW.height);
        stage.setMinWidth(WINDOW.width);

        stage.setResizable(false);
        stage.setScene(scene);

        scene.getWindow().centerOnScreen();

        stage.setOnShowing(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                table.getSelectionModel().clearSelection();
            }
        });

        stage.show();

        if (firstTime)
        {
            firstTime = false;
            reloadAllItems();
        }
    }
}
