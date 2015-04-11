/**
 *
 */
package main;

import logger.Logger;
import main.Exceptions.XC;
import ui.Controller;
import ui.TrayAgent;
import ui.Controller.FORMS;
import javafx.application.Application;
import javafx.application.Platform;
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
        Logger.loggerON(); // Switch logs ON
        Settings.loadSettings();
        launch(); // launches GUI.

        Terminator.terminate(new Exceptions(XC.THE_END));
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
