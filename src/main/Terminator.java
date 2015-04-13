/**
 *
 */
package main;

import java.io.PrintWriter;
import java.io.StringWriter;

import ui.Controller;
import ui.Controller.FORMS;
import db.PasswordCollection;
import logger.Logger;
import main.Exceptions.XC;
import main.Watcher;

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
            Watcher.getInstance().die();
        }
        catch (Exceptions e1)
        {
            // TODO
        }

        System.exit(e.getCode().ordinal());
    }

    public static void terminate(Exceptions e)
    {
        if (e.getCode().equals(XC.END))
        {
            try
            {
                if (PasswordCollection.getInstance().isChanged())
                {
                    Controller.getInstance().switchForm(FORMS.SAVE_DB);
                    return;
                }
            }
            catch (Exceptions e1)
            {
                Terminator.terminate(e1);
            }

            exit(e); // Normal exit
        }

        if (e.getCode().equals(XC.END_DISCARD)) exit(e);

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
