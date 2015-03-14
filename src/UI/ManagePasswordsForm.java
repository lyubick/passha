/**
 *
 */
package UI;

import Common.Exceptions;
import Common.RC.RCODES;
import Languages.Texts.TextID;
import Main.PasswordCollection;
import Main.SpecialPassword;
import Main.iSpecialPassword;
import UI.Controller.FORMS;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class ManagePasswordsForm extends AbstractForm
{
    @Override
    public void draw(Stage stage)
    {
        // TODO
        PasswordCollection.addPassword(new SpecialPassword());
        PasswordCollection.dump();

        TableView<iSpecialPassword> table = new TableView<iSpecialPassword>();

        ObservableList<iSpecialPassword> passwordSet = PasswordCollection.getIface();

        Button b_New = new Button(TextID.NEW.toString());
        Button b_Delete = new Button(TextID.DELETE.toString());
        Button b_Export = new Button(TextID.EXPORT.toString());

        GridPane buttonsGrid = new GridPane();

        // TODO make columns/column namse same way as TextID (to ensure column
        // has correct text)
        TableColumn[] columns =
                new TableColumn[]
                { new TableColumn(TextID.PWD_NAME.toString()),
                        new TableColumn(TextID.COMMENT.toString()),
                        new TableColumn(TextID.URL.toString()), };
        table.getColumns().setAll(columns);

        // TODO column numbers to enumerator
        columns[0].setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("name"));
        columns[1]
                .setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("comment"));
        columns[2].setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("url"));

        table.setItems(passwordSet);

        // TODO use HBOX where only one row is used
        buttonsGrid.setAlignment(Pos.CENTER);
        buttonsGrid.setHgap(HGAP);
        buttonsGrid.setVgap(VGAP);
        buttonsGrid
                .setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));

        // TODO numbers to local constants
        buttonsGrid.add(b_Export, 0, 0);
        buttonsGrid.add(b_New, 19, 0);
        buttonsGrid.add(b_Delete, 20, 0);

        // TODO numbers to local constants
        grid.add(buttonsGrid, 0, 1);
        grid.add(table, 0, 0);

        b_New.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent ae)
            {
                try
                {
                    Controller.getInstance().switchForm(FORMS.NEW_PWD);
                }
                catch (Exceptions e)
                {
                    System.exit(RCODES.ABEND.ordinal());
                }
            }
        });

        stage.setScene(scene);
        stage.show();
    }
}
