/**
 *
 */
package Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import Common.ReturnCodes;
import Logger.LOGLEVELS;

/**
 * @author lyubick
 *
 */
public final class Logger // Static class
{
    private static LOGLEVELS logLevel;

    private static String LOG_ADDS = "";

    private static long time; // wby this variable is needed??

    private static boolean initialized = false;

    private static PrintWriter writer = null;

    private static String getTime()
    {
        time = System.currentTimeMillis();
        return Long.toString(time);
    }

    public static void printError(String msg)
    {
        prepareAndLog(LOGLEVELS.ERROR, msg);
    }

    public static void printWarning(String msg)
    {
        prepareAndLog(LOGLEVELS.WARNING, msg);
    }

    public static void printInfo(String msg)
    {
        prepareAndLog(LOGLEVELS.INFO, msg);
    }

    public static void printDebug(String msg)
    {
        prepareAndLog(LOGLEVELS.DEBUG, msg);
    }

    // left for backwards compability
    public static void print(LOGLEVELS lvl, String msg)
    {
        // all content moved inside to align caller method position in stack with print<something> methods
        prepareAndLog(lvl, msg);
    }

    private static void prepareAndLog(LOGLEVELS lvl, String msg)
    {
        /// don't we need to check initialized variable here??
        if (lvl.ordinal() > logLevel.ordinal())
            return;

        switch (lvl)
        {
            case SILENT  : return;

            case ERROR   :
                LOG_ADDS = "ERROR : ";
                break;

            case WARNING :
                LOG_ADDS = "WARN  : ";
                break;

            case INFO    :
                LOG_ADDS = "INFO  : ";
                break;

            case DEBUG  :
                LOG_ADDS = "DEBUG : ";
                break;
        }

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        // The last element of the array represents the bottom of the stack,
        // which is the least recent method invocation in the sequence.
        StackTraceElement e = stacktrace[3];// maybe this number needs to be
                                            // corrected
        String methodName = e.getMethodName();
        String fileName = e.getFileName();
        int line = e.getLineNumber();
        writeToFileAndToScreen(String.format("[%1$s] %2$20s @ %3$4d %4$20s ", getTime(), fileName, line, methodName) + LOG_ADDS + msg);
    }

    private static void writeToFileAndToScreen(String log)
    {
        System.out.println(log);
        writer.println(log);
    }

    public static ReturnCodes loggerON(LOGLEVELS lvl)
    {
        logLevel = lvl;

        if (!initialized)
        {
            try {
                writer = new PrintWriter("bin/" + getTime());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initialized = true;
        }

        return ReturnCodes.RC_OK;
    }

    public static ReturnCodes loggerOFF()
    {
        if (initialized)
            writer.close();

        return ReturnCodes.RC_OK;
    }

    private Logger(LOGLEVELS lvl)
    {
        logLevel = lvl;
    }
}
