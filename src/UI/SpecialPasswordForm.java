/**
 *
 */
package UI;

import Logger.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class SpecialPasswordForm extends AbstractForm
{
    @Override
    public void draw(Stage primaryStage)
    {
        Logger.printDebug("SpecialPasswordForm prepareing");

        final String FORM_NAME = "Add Special Password";
        Button b_OK = new Button("Create");
        Button b_cancel = new Button("Cancel");
        Button b_incLength = new Button("+");
        Button b_decLength = new Button("-");

        Label l_name = new Label("Name");
        Label l_comment = new Label("Comment");
        Label l_url = new Label("URL");
        Label l_generatedPwd = new Label("Generated password: ");
        Label lLength = new Label("Length");

        CheckBox cb_specialChars = new CheckBox("Must contain special characters");
        CheckBox cb_upperCaseChar = new CheckBox("Must have UPPER case character");
        TextField tf_name = new TextField();
        TextField tf_comment = new TextField();
        TextField tf_url = new TextField();
        TextField tf_generatedPassword = new TextField();
        TextField tf_length = new TextField();
        tf_length.setMaxWidth(40);

        //todo: use global constants or create global/local constants
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(l_name, 0, 0);
        grid.add(tf_name, 1, 0);

        grid.add(l_comment, 0, 1);
        grid.add(tf_comment, 1, 1);

        grid.add(l_url, 0, 2);
        grid.add(tf_url, 1, 2);

        grid.add(lLength, 0, 3);
        grid.add(tf_length, 1, 3);

        grid.add(l_generatedPwd, 0, 4);
        grid.add(tf_generatedPassword, 1, 4);

        GridPane grid1 = new GridPane();
        grid1.setHgap(10);
        grid1.setVgap(10);

        grid1.add(b_OK, 0, 0);
        grid1.add(b_cancel, 1, 0);
        grid.add(grid1, 1, 5);

        b_OK.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("Fields: " + tf_name.getText() + tf_comment.getText()
                        + tf_url.getText() + tf_length.getText() + tf_generatedPassword.getText());

            }

        });


        //todo: window size to (global?) constants
        Scene scene = new Scene(grid, 440, 300);
        primaryStage.setScene(scene);

        Logger.printDebug("SpecialPasswordForm displaying");

        primaryStage.show();

    }
}
