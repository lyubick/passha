package main;

import java.awt.TrayIcon.MessageType;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions.XC;
import ui.LoginForm;
import ui.TrayAgent;

public class Main extends Application
{

    public static void main(String[] args)
    {
        String info = "";
        /* 1. Switch ON logs */
        try
        {
            Logger.loggerON();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        /* 2. Load settings */
        try
        {
            Settings.init().loadSettings();
        }
        catch (Exceptions e)
        {
            if (e.getCode().equals(XC.DEFAULT_SETTINGS_USED))
            {
                info = TextID.TRAY_MESSAGE_ERROR_FAILED_LOAD_SETTINGS.toString();
            }
            else
            {
                Terminator.terminate(e);
            }
        }

        /* 3. Wake up tray notifications */
        try
        {
            TrayAgent.init();

            if (info.length() > 0)
                TrayAgent.getInstance().showNotification(TextID.COMMON_LABEL_ERROR.toString(), info, MessageType.ERROR);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
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
            new LoginForm().show();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }
}
