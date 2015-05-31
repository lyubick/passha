package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import logger.Logger;
import main.Exceptions.XC;
import ui.Controller;
import ui.TrayAgent;
import ui.Controller.FORMS;

public class Main extends Application
{

    public static void main(String[] args)
    {
        try
        {
            Logger.loggerON();
            Settings.init().loadSettings();
        }
        catch (Exceptions e)
        {
            if (e.getCode().equals(XC.DEFAULT_SETTINGS_USED))
            {
                // TODO inform user
            }
            else
            {
                Terminator.terminate(e);
            }
        }

        launch();

        Terminator.terminate(new Exceptions(XC.END));
    }

    @Override
    public void start(Stage primaryStage)
    {
        Platform.setImplicitExit(false);

        try
        {
            TrayAgent.initTrayAgent(primaryStage);
            Controller.init(primaryStage).switchForm(FORMS.LOGIN);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }
}
