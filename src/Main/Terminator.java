/**
 *
 */
package Main;

import Common.Exceptions;
import Common.Exceptions.XC;
import Logger.Logger;

/**
 * @author lyubick
 *
 */
public class Terminator
{
    static void exit(Exceptions e)
    {
        Logger.loggerOFF();
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

        Logger.printError("TERMINATOR: PREPARING SAFE EXITING...");

        // TODO Create all needed procedures here

        Logger.printError("TERMINATOR: EXITING...");

        exit(e);
    }
}
