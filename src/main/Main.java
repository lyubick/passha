package main;

import core.VaultManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import languages.Local.TextID;
import logger.Logger;
import main.Exceptions.XC;
import ui.FormVaultsManager;

public class Main extends Application
{
    public static boolean DEBUG = false;

    public static void main(String[] args)
    {
        if (args.length > 0 && args[0].equals("--debug")) DEBUG = true;

        /* 0. Linux Xlib problem workaround */
        if (System.getProperty("os.name").equals("Linux")) System.loadLibrary("xx"); // FIXME: normal name

        /* 1. Switch ON logs */
        try
        {
            Logger.loggerON();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        TextID info = null;

        /* 2. Load settings */
        try
        {
            Settings.init().loadSettings();
        }
        catch (Exceptions e)
        {
            if (e.getCode().equals(XC.DEFAULT_SETTINGS_USED))
            {
                info = TextID.TRAY_MSG_FAILED_LOAD_SETTINGS;
            }
            else
            {
                Terminator.terminate(e);
            }
        }

        /* 3. Wake up tray notifications */
        /*
         * try { TrayAgent.init();
         * if (info != null)
         * TrayAgent.getInstance().showNotification(TextID.COMMON_LABEL_ERROR,
         * info, MessageType.ERROR); } catch (Exceptions e) {
         * Terminator.terminate(e); }
         */

        launch();

        Terminator.terminate(new Exceptions(XC.END));
    }

    @Override
    public void start(Stage primaryStage)
    {
        Platform.setImplicitExit(false);

        // During implementation don't run update and autologin
        // new FormUpdate(null);

        VaultManager.init();

        new FormVaultsManager();
    }
}
