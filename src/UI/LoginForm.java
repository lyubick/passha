/**
 *
 */
package UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Common.Exceptions;
import CryptoSystem.CryptoSystem;
import Languages.Texts.TextID;
import Logger.Logger;
import UI.Controller.FORMS;

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

        Label l_Welcome = new Label(TextID.GREETING.toString());

        PasswordField pf_Password = new PasswordField();

        Button b_OK = new Button(TextID.OK.toString());

        grid.setAlignment(Pos.CENTER);

        // TODO where only one columnt is used - use VBOX instead of grid
        // TODO move row numbers to constants
        grid.add(l_Welcome, 0, 0);
        grid.setHalignment(grid.getChildren().get(grid.getChildren().indexOf(l_Welcome)),
                HPos.CENTER);
        grid.add(pf_Password, 0, 5);
        grid.add(b_OK, 0, 6);
        grid.setHalignment(grid.getChildren().get(grid.getChildren().indexOf(b_OK)), HPos.RIGHT);

        pf_Password.setPromptText("Password...");

        b_OK.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("Entered Password: " + pf_Password.getText());

                if (pf_Password.getText().length() != 0)
                    CryptoSystem.init(pf_Password.getText().toString());

                try
                {
                    Controller.getInstance().switchForm(FORMS.MAN_PWD);
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        stage.setScene(scene);

        Logger.printDebug("LoginForm displaying");
        stage.show();
    }
}
