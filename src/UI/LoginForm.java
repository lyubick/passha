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
import Common.Defines;
import Logger.Logger;

/**
 * @author lyubick
 *
 */
public class LoginForm
{

    @SuppressWarnings("static-access")
    public static void draw(Stage stage)
    {
        GridPane grid = new GridPane();

        Label l_Welcome = new Label(Defines.WELCOME);
        TextField tf_Password = new TextField();
        Button b_OK = new Button(Defines.OK);

        grid.setHgap(Defines.HGAP);
        grid.setVgap(Defines.VGAP);
        grid.setPadding(new Insets(Defines.PADDING.top, Defines.PADDING.right,
                Defines.PADDING.bottom, Defines.PADDING.left));

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

        Scene scene = new Scene(grid, 250, 200);
        stage.setScene(scene);

        stage.show();

    }
}
