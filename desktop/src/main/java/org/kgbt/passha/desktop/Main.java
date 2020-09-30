package org.kgbt.passha.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.kgbt.passha.core.GenericUI;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.cfg.Settings;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.desktop.languages.Local;
import org.kgbt.passha.desktop.ui.FormVaultsManager;
import org.kgbt.passha.desktop.ui.TrayAgent;

import java.awt.*;

public class Main extends Application
{
    private static final String ARG_LOGLEVEL = "--loglevel=";
    private static final String ARG_DEBUG    = "--debug";

    public static boolean DEBUG = false;

    public static void main(String[] args)
    {
        /* 0. Read incoming arguments */
        String logLevelString = null;

        for (String arg : args)
        {
            if (arg.equals(ARG_DEBUG))
                DEBUG = true;
            if (arg.startsWith(ARG_LOGLEVEL))
                logLevelString = arg.substring(ARG_LOGLEVEL.length());
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

        Local.Texts info = null;

        /* 2. Load settings */
        try
        {
            Settings.init().loadSettings();
        }
        catch (Exceptions e)
        {
            if (e.getCode().equals(Exceptions.XC.DEFAULT_SETTINGS_USED))
            {
                info = Local.Texts.TRAY_MSG_FAILED_LOAD_SETTINGS;
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
            if (info != null)
                TrayAgent.getInstance().showNotification(Local.Texts.LABEL_ERROR, info, TrayIcon.MessageType.ERROR);
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

        Terminator.terminate(new Exceptions(Exceptions.XC.END));
    }

    @Override
    public void start(Stage primaryStage)
    {
        Platform.setImplicitExit(DEBUG);

        GenericUI.init(CoreUiInterface::new);
        VaultManager.init();

        try
        {
            for (byte[] hash : Autologin.getInstance().getVaults())
            {
                try
                {
                    VaultManager.getInstance().addVault(hash, false ,"");
                }
                catch (Exceptions e)
                {
                    if (e.getCode() != Exceptions.XC.FILE_DOES_NOT_EXIST
                        && e.getCode() != Exceptions.XC.DIR_DOES_NOT_EXIST)
                        throw e;

                    Autologin.getInstance().setAutologinOFF(hash);
                }
            }
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
