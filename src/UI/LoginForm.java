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
import javafx.stage.Stage;
import Common.Exceptions;
import Common.Exceptions.XC;
import Common.Return.RC;
import CryptoSystem.CryptoSystem;
import Languages.Texts.TextID;
import Logger.Logger;
import Main.PasswordCollection;
import UI.Controller.FORMS;

/**
 * @author lyubick
 *
 */
public class LoginForm extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 300;
        public static final int height = 200;
    }

    @Override
    @SuppressWarnings("static-access")
    public void draw(Stage stage)
    {
        Logger.printDebug("LoginForm prepareing");

        stage.setTitle("pasSHA");
        stage.setResizable(false);
        stage.setMaximized(false);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        Label l_Welcome = new Label(TextID.GREETING.toString());

        PasswordField pf_Password = new PasswordField();

        pf_Password.setMinWidth(WINDOW.width - 50);

        Button b_OK = new Button(TextID.OK.toString());
        Button b_NEW = new Button(TextID.NEW.toString());
        b_NEW.setVisible(false);

        grid.setAlignment(Pos.CENTER);

        // TODO where only one columnt is used - use VBOX instead of grid
        // TODO move row numbers to constants
        grid.add(l_Welcome, 0, 0);
        grid.setHalignment(grid.getChildren().get(grid.getChildren().indexOf(l_Welcome)),
                HPos.CENTER);
        grid.add(pf_Password, 0, 2);
        grid.add(b_NEW, 0, 4);
        grid.add(b_OK, 0, 4);
        grid.setHalignment(grid.getChildren().get(grid.getChildren().indexOf(b_OK)), HPos.RIGHT);

        pf_Password.setPromptText("Password...");

        b_OK.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("Entered Password: " + pf_Password.getText());

                if (pf_Password.getText().length() != 0)
                {
                    try
                    {
                        init(pf_Password.getText().toString(), false);
                    }
                    catch (Exceptions e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        b_NEW.setVisible(true);
                    }
                }
            }
        });

        b_NEW.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("b_NEW button pressed");

                try
                {
                    init(pf_Password.getText().toString(), true);
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


    private void init(String password, boolean isNewUser) throws Exceptions
    {

        CryptoSystem.init(password, isNewUser);

        // ========== Database activation START:
        try
        {
            PasswordCollection.init();
        }
        catch (Exceptions e)
        {
            System.exit(RC.SECURITY_FAILURE.ordinal()); // TODO
                                                        // abend
        }
        // ========== Database activation END:

        try
        {
            ctrl.switchForm(FORMS.MANAGE_PWDS);
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
