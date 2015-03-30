/**
 *
 */
package Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import Common.Exceptions;
import Common.Exceptions.XC;
import Logger.Logger;
import UI.Controller;
import UI.Controller.FORMS;
import UI.HotKeyAgent;
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
        String[] parms = readArgs(args);

        Logger.loggerON(parms[0].toString());

        launch(); // launches GUI.

        Logger.loggerOFF();
    }

    public static String[] readArgs(String[] args)
    {
        final int ARGS = 1;

        final String argLog = "-l=";

        String[] parms = new String[ARGS];

        /**
         * INIT default argument values, to avoid crash on launching without
         * arguments
         */
        parms[0] = "SILENT";
        /* END OF INIT */

        if (args.length == 0)
        {
            System.out.println("Welcome!\n" + "Program should be launched with:\n"
                    + "-l=[level] where [level]=DEBUG,ERROR,WARNING,INFO,SILENT");

            ABEND.terminate(new Exceptions(XC.INIT_FAILURE));
        }

        for (String arg : args)
        {
            parms[0] =
                    (arg.indexOf("-l=") != -1) ? arg.substring(
                            arg.indexOf(argLog) + argLog.length(), arg.length()) : parms[0];
        }

        return parms;
    }

    @Override
    public void start(Stage primaryStage)
    {
        Platform.setImplicitExit(false);

        HotKeyAgent.init();

        HotKeyAgent.getInstance().register();

        try
        {
            TrayAgent.addTray(primaryStage);
            Controller.init(primaryStage).switchForm(FORMS.LOGIN);
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }
    }
}
