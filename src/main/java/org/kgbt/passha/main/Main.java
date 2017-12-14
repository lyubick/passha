package org.kgbt.passha.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.cryptosystem.Autologin;
import org.kgbt.passha.languages.Local.Texts;
import org.kgbt.passha.logger.Logger;
import org.kgbt.passha.main.Exceptions.XC;
import org.kgbt.passha.ui.FormVaultsManager;
import org.kgbt.passha.ui.TrayAgent;

import java.awt.*;

public class Main extends Application
{
    private static final String ARG_LOGLEVEL = "--loglevel=";
    private static final String ARG_DEBUG    = "--debug";

    public static boolean       DEBUG        = false;

    public static void main(String[] args)
    {
        /* 0. Read incoming arguments */
        String logLevelString = null;

        for (String arg : args)
        {
            if (arg.equals(ARG_DEBUG)) DEBUG = true;
            if (arg.startsWith(ARG_LOGLEVEL)) logLevelString = arg.substring(ARG_LOGLEVEL.length());
        }

        /* 1. Switch ON logs */
        try
        {
            Logger.loggerON(logLevelString);
            Logger.printFatal("Hello!");
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        Texts info = null;

        /* 2. Load settings */
        try
        {
            Settings.init().loadSettings();
        }
        catch (Exceptions e)
        {
            if (e.getCode().equals(XC.DEFAULT_SETTINGS_USED))
            {
                info = Texts.TRAY_MSG_FAILED_LOAD_SETTINGS;
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
            if (info != null) TrayAgent.getInstance().showNotification(Texts.LABEL_ERROR, info, TrayIcon.MessageType.ERROR);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        /* 4. Set up auto login */
        try
        {
            Autologin.init();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        /* 5. Start the application */

        launch();

        Terminator.terminate(new Exceptions(XC.END));
    }

    @Override
    public void start(Stage primaryStage)
    {
        Platform.setImplicitExit(DEBUG);

        VaultManager.init();
        try
        {
            VaultManager.getInstance().autologin();
        }
        catch (Exceptions e1)
        {
            Terminator.terminate(e1);
        }

        try
        {
            new FormVaultsManager();
        }
        catch (Exceptions e)
        {
            Logger.printFatal("Unhandled exception: " + e.getCode());
            Terminator.terminate(e);
        }
    }
}
