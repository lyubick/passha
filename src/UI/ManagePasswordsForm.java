/**
 *
 */
package UI;

import java.awt.List;

import Common.Defines;
import Languages.Texts.TextID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class ManagePasswordsForm
{
    public static void draw(Stage stage)
    {
        ObservableList<String> personData = FXCollections.observableArrayList();
        personData.add("asdfasd");


        Button b_New = new Button(TextID.NEW.toString());
        Button b_Delete = new Button(TextID.DELETE.toString());
        Button b_Export = new Button(TextID.EXPORT.toString());

        GridPane grid = new GridPane();
        GridPane buttonsGrid = new GridPane();

        TableColumn[] columns = new TableColumn[]
        {
                new TableColumn<String, String>(TextID.PWD_NAME.toString()),
                new TableColumn<String, String>(TextID.PWD.toString()),
                new TableColumn<String, String>(TextID.COMMENT.toString()),
                new TableColumn<String, String>(TextID.SHORTCUT.toString()),
                new TableColumn<String, String>(TextID.ENABLED.toString()),
        };

        TableView<String> table = new TableView<String>();

        table.getColumns().setAll(columns);
        table.setItems(personData);


        grid.setAlignment(Pos.CENTER);
        buttonsGrid.setAlignment(Pos.CENTER);

        grid.setHgap(Defines.HGAP);
        grid.setVgap(Defines.VGAP);
        grid.setPadding(new Insets(Defines.PADDING.top, Defines.PADDING.right,
                Defines.PADDING.bottom, Defines.PADDING.left));

        buttonsGrid.setHgap(Defines.HGAP);
        buttonsGrid.setVgap(Defines.VGAP);
        buttonsGrid.setPadding(new Insets(Defines.PADDING.top, Defines.PADDING.right,
                Defines.PADDING.bottom, Defines.PADDING.left));

        if (Defines.DEBUG == true)
        {
            grid.setGridLinesVisible(true); // TODO
            buttonsGrid.setGridLinesVisible(true); // TODO
        }


        grid.add(buttonsGrid, 0, 1);
        grid.add(table, 0, 0);
        buttonsGrid.add(b_Export, 0, 0);
        buttonsGrid.add(b_New, 19, 0);
        buttonsGrid.add(b_Delete, 20, 0);


        Scene scene = new Scene(grid, 440, 300);
        stage.setScene(scene);

        stage.show();

    }
}
