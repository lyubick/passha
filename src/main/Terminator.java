/**
 *
 */
package main;

import java.io.PrintWriter;
import java.io.StringWriter;

import logger.Logger;
import main.Exceptions.XC;

/**
 * @author lyubick
 *
 */
public class Terminator
{
    static void exit(Exceptions e)
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

    public static void terminate(Exceptions e)
    {
        if (e.getCode().equals(XC.THE_END)) exit(e); // Normal exit

        Logger.printError("TERMINATOR: FATAL ERROR OCCURED: " + e.getCode().name());

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3)
        {
            StackTraceElement element = stackTrace[2];
            Logger.printError("TERMINATOR: CLASS: " + element.getClassName() + " METHOD: "
                    + element.getMethodName());
        }

        Logger.printError("TERMINATOR: STACK TRACE DUMP");

        StringWriter tracePrint = new StringWriter();
        e.printStackTrace(new PrintWriter(tracePrint));

        Logger.printError(tracePrint.toString());

        exit(e);
    }
}
