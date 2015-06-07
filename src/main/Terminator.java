/**
 *
 */
package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;

import ui.Controller;
import ui.Controller.FORMS;
import db.PasswordCollection;
import logger.Logger;
import main.Exceptions.XC;

/**
 * @author lyubick
 *
 */
public class Terminator
{
    private static boolean restartPending = false;

    private static void exit(Exceptions e)
    {
        Logger.printDebug("Bye!");

        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions e1)
        {
            // TODO
        }

        System.exit(e.getCode().ordinal());
    }

    public static void restart()
    {
        File currentJar = null;

        try
        {
            currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        }
        catch (URISyntaxException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // if (!currentJar.getName().endsWith(".jar")) return;

        final ArrayList<String> command = new ArrayList<String>();
        command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        try
        {
            builder.start();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void terminate(Exceptions e)
    {
        if (e.getCode().equals(XC.END) || e.getCode().equals(XC.RESTART))
        {
            try
            {
                if (PasswordCollection.getInstance().isChanged())
                {
                    Controller.getInstance().switchForm(FORMS.SAVE_DB);
                    if (e.getCode().equals(XC.RESTART)) restartPending = true;
                    return;
                }
            }
            catch (Exceptions e1)
            {
                Terminator.terminate(e1);
            }
        }

        if (e.getCode().equals(XC.END) || e.getCode().equals(XC.END_DISCARD) || e.getCode().equals(XC.RESTART))
        {
            if (restartPending || e.getCode().equals(XC.RESTART)) restart();
            exit(e);
        }

        Logger.printError("TERMINATOR: FATAL ERROR OCCURED: " + e.getCode().name());

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3)
        {
            StackTraceElement element = stackTrace[2];
            Logger.printError("TERMINATOR: CLASS: " + element.getClassName() + " METHOD: " + element.getMethodName());
        }

        Logger.printError("TERMINATOR: STACK TRACE DUMP");

        StringWriter tracePrint = new StringWriter();
        e.printStackTrace(new PrintWriter(tracePrint));

        Logger.printError(tracePrint.toString());

        exit(e);
    }
}
