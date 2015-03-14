/**
 *
 */
package UI;

import Common.Exceptions;
import Common.Return.RC;
import Languages.Texts.TextID;
import Logger.Logger;
import Main.PasswordCollection;
import Main.iSpecialPassword;
import UI.Controller.FORMS;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class ManagePasswordsForm extends AbstractForm
{
    private final TableView<iSpecialPassword> table = new TableView<iSpecialPassword>();
    private PasswordCollection                pc    = null;
    private final iSpecialPassword            lastShown = null;

    public ManagePasswordsForm()
    {
        try
        {
            pc = PasswordCollection.getInstance();
        }
        catch (Exceptions e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

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
                        new TableColumn(TextID.URL.toString()),
                        new TableColumn(TextID.PWD.toString()), };
        table.getColumns().setAll(columns);

        // TODO column numbers to enumerator
        columns[0].setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("name"));
        columns[1]
                .setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("comment"));
        columns[2].setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>("url"));
        columns[3].setCellValueFactory(new PropertyValueFactory<iSpecialPassword, String>(
                "password"));

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
                ctrl.switchForm(FORMS.NEW_PWD);
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
                    pc.removePassword(pwd.getOrigin());
                }
            }

        });

//        table.setOnMouseClicked(new EventHandler<MouseEvent>()
//        {
//            @Override
//            public void handle(MouseEvent event)
//            {
//                // TODO Auto-generated method stub
//                if (lastShown != null)
//                {
//                    lastShown.showPassword(false);
//                }
//
//                lastShown = table.getSelectionModel().getSelectedItem();
//                lastShown.showPassword(true);
//            }
//        });
    }

    @Override
    public void draw(Stage stage)
    {
        table.setItems(pc.getIface());

        stage.setScene(scene);
        stage.show();
    }
}
