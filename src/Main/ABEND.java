/**
 *
 */
package Main;

import Common.Exceptions;
import Logger.Logger;

/**
 * @author lyubick
 *
 */
public class ABEND
{
    static void exit()
    {

    }

    private static void terminate(int exitCode)
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3)
        {
            StackTraceElement element = stackTrace[2];

            Logger.printError("ABEND: CLASS: " + element.getClassName() + " METHOD: "
                    + element.getMethodName());
        }

        Logger.printError("ABEND: PREPARING SAFE EXITING");

        // TODO Create all needed procedures here

        Logger.printError("ABEND: EXITING");
        Logger.loggerOFF();
        System.exit(exitCode);
    }

    public static void terminate(Exceptions e)
    {
        Logger.printError("ABEND: FATAL ERROR OCCURED: " + e.getCode().name());
        terminate(e.getCode().ordinal());
    }

}
