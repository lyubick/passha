/**
 *
 */
package Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import Common.Exceptions;
import Logger.Logger;
import UI.Controller;
import UI.Controller.FORMS;
import UI.TrayAgent;

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
        Logger.loggerON(); // Switch logs ON
        Settings.loadSettings();
        launch(); // launches GUI.
    }

    @Override
    public void start(Stage primaryStage)
    {
        Platform.setImplicitExit(false);

        try
        {
            TrayAgent.addTray(primaryStage);
            Controller.init(primaryStage).switchForm(FORMS.LOGIN);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }
}
