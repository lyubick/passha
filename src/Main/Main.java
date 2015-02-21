/**
 *
 */
package Main;


import Logger.LOGLEVELS;
import Logger.Logger;
import UI.LoginForm;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author lyubick
 *
 */
public class Main extends Application
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            /* TODO for curious, u need it then think of format :) */
            Logger.loggerON(LOGLEVELS.SILENT);
        }
        else
        {
            Logger.loggerON(LOGLEVELS.DEBUG);
        }

        // launches GUI.
        launch(args);

        Logger.loggerOFF();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        LoginForm.draw(primaryStage);
    }
}
