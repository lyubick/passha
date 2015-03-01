/**
 *
 */
package UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Languages.Texts.TextID;
import Logger.Logger;

/**
 * @author lyubick
 *
 */
public class LoginForm extends AbstractForm
{
    @Override
    @SuppressWarnings("static-access")
    public void draw(Stage stage)
    {
        Logger.printDebug("LoginForm prepareing");
        GridPane grid = new GridPane();

        Label l_Welcome = new Label(TextID.GREETING.toString());
        TextField tf_Password = new TextField();
        Button b_OK = new Button(TextID.OK.toString());

        grid.setHgap(HGAP);
        grid.setVgap(VGAP);
        grid.setPadding(new Insets(PADDING.top, PADDING.right,
                PADDING.bottom, PADDING.left));

        grid.setAlignment(Pos.CENTER);

        if (Defines.DEBUG == true)
        {
            grid.setGridLinesVisible(true); // TODO
        }

        grid.add(l_Welcome, 0, 0);
        grid.setHalignment(grid.getChildren().get(1), HPos.CENTER);
        grid.add(tf_Password, 0, 5);
        grid.add(b_OK, 0, 6);
        grid.setHalignment(grid.getChildren().get(3), HPos.RIGHT);

        b_OK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("Entered Password: " + tf_Password.getText());
            }
        });

        //todo window sizes move to constants
        Scene scene = new Scene(grid, 250, 200);
        stage.setScene(scene);

        Logger.printDebug("LoginForm displaying");
        stage.show();

    }
}
