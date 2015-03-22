/**
 *
 */
package UI;

import Common.Exceptions;
import Common.Exceptions.XC;
import Languages.Texts.TextID;
import Main.ABEND;
import Main.PasswordCollection;
import Main.iSpecialPassword;
import UI.Controller.FORMS;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author curious-odd-man
 *
 */
public class ManagePasswordsForm extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 1000;
        public static final int height = 600;
    }

    private final TableView<iSpecialPassword> table     = new TableView<iSpecialPassword>();
    private TextField                         tf_pass   = null;
    private Button                            b_Save    = null;
    private Button                            b_Discard = null;
    private Button                            b_New     = null;
    private Button                            b_Delete  = null;
    private Button                            b_Copy    = null;
    private Button                            b_Export  = null;

    private void handleButtons()
    {
        try
        {
            b_Save.setDisable(!PasswordCollection.getInstance().isChanged());
            b_Discard.setDisable(!PasswordCollection.getInstance().isChanged());
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }
    }

    private void setButtonShortcut(final Button btn, KeyCodeCombination cmb) throws Exceptions
    {
        if (btn.getScene() == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);
        btn.getScene().getAccelerators().put(cmb, new Runnable()
        {
            @Override
            public void run()
            {
                // Do it with stile - show animation
                btn.arm();
                PauseTransition pt = new PauseTransition(Duration.millis(300));
                pt.setOnFinished(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        btn.fire();
                        btn.disarm();
                    }
                });
                pt.play();
            }
        });
    }

    public ManagePasswordsForm()
    {
        int currentGridLine = 0;
        tf_pass = new TextField();

        b_New = getButton("_" + TextID.NEW.toString());
        b_Delete = getButton("_" + TextID.DELETE.toString());
        b_Copy = getButton("_" + TextID.COPY_CLIPBOARD.toString());
        b_Export = getButton("_" + TextID.EXPORT.toString());
        b_Save = getButton("_" + TextID.SAVE.toString());
        b_Discard = getButton("_" + TextID.DISCARD.toString());

        table.setMinHeight(WINDOW.height - 300);
        table.setMinWidth(WINDOW.width - 200);

        tf_pass.setMaxWidth(200);
        tf_pass.setEditable(false);

        b_Export.setDisable(true);
        b_Save.setDisable(true);
        b_Discard.setDisable(true);

        // TODO make columns/column names same way as TextID (to ensure column
        // has correct text)
        TableColumn[] columns =
                new TableColumn[]
                { new TableColumn(TextID.PWD_NAME.toString()),
                        new TableColumn(TextID.COMMENT.toString()),
                        new TableColumn(TextID.URL.toString()), };
        table.getColumns().setAll(columns);

        // columns[0].prefWidthProperty().bind(table.widthProperty().divide(4));
        // columns[1].prefWidthProperty().bind(table.widthProperty().divide((float)
        // 8 / 3));
        // columns[2].prefWidthProperty().bind(table.widthProperty().divide((float)
        // 8 / 3));

        // TODO column numbers to enumerator
        columns[0].setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("name"));
        columns[1]
                .setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("comment"));
        columns[2].setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("url"));

        GridPane.setValignment(b_New, VPos.TOP);
        GridPane.setValignment(b_Delete, VPos.TOP);
        GridPane.setHalignment(b_Copy, HPos.LEFT);
        GridPane.setValignment(b_Save, VPos.BOTTOM);
        GridPane.setValignment(b_Discard, VPos.BOTTOM);
        GridPane.setValignment(b_Export, VPos.BOTTOM);

        // TODO numbers to local constants
        grid.add(table, 0, currentGridLine++);
        grid.add(tf_pass, 0, currentGridLine);
        grid.add(b_Copy, 0, currentGridLine++);
        grid.add(b_Export, 1, 0);
        grid.add(b_New, 1, 0);
        grid.add(b_Delete, 1, 0);
        grid.add(b_Save, 1, 0);
        grid.add(b_Discard, 1, 0);

        GridPane.setMargin(b_Delete, new Insets(40, 0, 0, 0));
        GridPane.setMargin(b_Save, new Insets(0, 0, 40, 0));
        GridPane.setMargin(b_Discard, new Insets(0, 0, 80, 0));
        GridPane.setMargin(b_Copy, new Insets(0, buttonWidth, 0, 210));

        try
        {
            setButtonShortcut(b_New,
                    new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Delete, new KeyCodeCombination(KeyCode.D,
                    KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Copy, new KeyCodeCombination(KeyCode.C,
                    KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Export, new KeyCodeCombination(KeyCode.E,
                    KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Save, new KeyCodeCombination(KeyCode.S,
                    KeyCombination.SHORTCUT_DOWN));
            setButtonShortcut(b_Discard, new KeyCodeCombination(KeyCode.Z,
                    KeyCombination.SHORTCUT_DOWN));

        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }

        b_New.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent ae)
            {
                try
                {
                    ctrl.switchForm(FORMS.CREATE_PWD);
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
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
                        ABEND.terminate(e);
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
                try
                {
                    PasswordCollection.getInstance().save();
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
                }
                handleButtons();
            }
        });

        b_Discard.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    PasswordCollection.getInstance().reload();
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
                }
                handleButtons();
                try
                {
                    table.setItems(PasswordCollection.getInstance().getIface());
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
                }
            }
        });

        table.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (table.getSelectionModel().getSelectedItem() != null)
                    tf_pass.setText(table.getSelectionModel().getSelectedItem().getPassword());
            }
        });
    }

    @Override
    public void draw(Stage stage) throws Exceptions
    {
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

        stage.show();
    }

}
