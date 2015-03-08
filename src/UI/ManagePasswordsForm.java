/**
 *
 */
package UI;

import java.awt.List;

import Languages.Texts.TextID;
import Logger.Logger;
import Main.PasswordCollection;
import Main.SpecialPassword;
import Main.iSpecialPassword;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

        GridPane grid = new GridPane();
        GridPane buttonsGrid = new GridPane();

        TableColumn[] columns = new TableColumn[]
        {
                new TableColumn(TextID.PWD_NAME.toString()),
                new TableColumn(TextID.COMMENT.toString()),
                new TableColumn(TextID.URL.toString()),
        };
        table.getColumns().setAll(columns);

        columns[0].setCellValueFactory(new PropertyValueFactory<iSpecialPassword,String>("name"));
        columns[1].setCellValueFactory(new PropertyValueFactory<iSpecialPassword,String>("comment"));
        columns[2].setCellValueFactory(new PropertyValueFactory<iSpecialPassword,String>("url"));

        table.setItems(passwordSet);

        grid.setAlignment(Pos.CENTER);
        buttonsGrid.setAlignment(Pos.CENTER);

        grid.setHgap(HGAP);
        grid.setVgap(VGAP);
        grid.setPadding(new Insets(PADDING.top, PADDING.right,
                PADDING.bottom, PADDING.left));

        buttonsGrid.setHgap(HGAP);
        buttonsGrid.setVgap(VGAP);
        buttonsGrid.setPadding(new Insets(PADDING.top, PADDING.right,
                PADDING.bottom, PADDING.left));

        if (Defines.DEBUG == true)
        {
            grid.setGridLinesVisible(true); // TODO
            buttonsGrid.setGridLinesVisible(true); // TODO
        }


        //todo numbers to local constants
        grid.add(buttonsGrid, 0, 1);
        grid.add(table, 0, 0);
        buttonsGrid.add(b_Export, 0, 0);
        buttonsGrid.add(b_New, 19, 0);
        buttonsGrid.add(b_Delete, 20, 0);


        //todo window sizes move to (global?) constants
        Scene scene = new Scene(grid, 440, 300);
        stage.setScene(scene);

        stage.show();

    }
}
