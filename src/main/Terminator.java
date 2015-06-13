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

import logger.Logger;
import main.Exceptions.XC;

/**
 * @author lyubick
 *
 */
public class Terminator
{
    private static void exit(Exceptions e)
    {
        Logger.printDebug("Bye!");

        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions e1)
        {
        }

        System.exit(e.getCode().ordinal());
    }

    public static void restart()
    {
        try
        {
            File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            final ArrayList<String> command = new ArrayList<String>();
            command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
            command.add("-jar");
            command.add(currentJar.getPath());
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        }
        catch (URISyntaxException | IOException e)
        {
            Terminator.terminate(new Exceptions(XC.RESTART_FAILED));
        }

        Terminator.exit(new Exceptions(XC.END));
    }

    public static void terminate(Exceptions e)
    {
        if (e.getCode().equals(XC.RESTART)) restart();

        if (e.getCode().equals(XC.END)) exit(e);

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
