/**
 *
 */
package Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import Common.RC.RETURNCODES;
import Common.RC;

/**
 * @author lyubick
 *
 */
public final class Logger // Static class
{
    public enum LOGLEVELS
    {
        SILENT,
        ERROR,
        WARNING,
        INFO,
        DEBUG
    }

    private static Map<String, LOGLEVELS> argsMap = initArgsMap();

    private static Map<String, LOGLEVELS> initArgsMap()
    {
        Map<String, LOGLEVELS> retMap = new HashMap<String, LOGLEVELS>();

        retMap.put("DEBUG", LOGLEVELS.DEBUG);
        retMap.put("ERROR", LOGLEVELS.ERROR);
        retMap.put("INFO", LOGLEVELS.INFO);
        retMap.put("SILENT", LOGLEVELS.SILENT);
        retMap.put("WARNING", LOGLEVELS.WARNING);

        return retMap;
    }

    private static LOGLEVELS   logLevel;

    private static String      LOG_ADDS    = "";

    private static boolean     initialized = false;

    private static PrintWriter writer      = null;

    private static String getTime()
    {
        return Long.toString(System.currentTimeMillis());
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

    private static void prepareAndLog(LOGLEVELS lvl, String msg)
    {
        if (!initialized)
            return;

        if (lvl.ordinal() > logLevel.ordinal())
            return;

        switch (lvl)
        {
            case SILENT:
                return;

            case ERROR:
                LOG_ADDS = "ERROR : ";
                break;

            case WARNING:
                LOG_ADDS = "WARN  : ";
                break;

            case INFO:
                LOG_ADDS = "INFO  : ";
                break;

            case DEBUG:
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
        writeToFileAndToScreen(String.format("[%1$s] %2$20s @ %3$4d %4$20s ", getTime(), fileName,
                line, methodName) + LOG_ADDS + msg);
    }

    private static void writeToFileAndToScreen(String log)
    {
        System.out.println(log);
        writer.println(log);
    }

    public static RETURNCODES loggerON(String log)
    {
        logLevel = (argsMap.get(log) != null) ? argsMap.get(log) : LOGLEVELS.SILENT;

        if (!initialized)
        {
            try
            {
                writer = new PrintWriter("bin/" + getTime());
            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initialized = true;
        } else
            return RC.check(RETURNCODES.RC_NOK);

        return RETURNCODES.RC_OK;
    }

    public static RETURNCODES loggerOFF()
    {
        if (initialized)
            writer.close();

        return RETURNCODES.RC_OK;
    }

    private Logger(LOGLEVELS lvl)
    {
        logLevel = lvl;
    }

    public static LOGLEVELS getLogLevel()
    {
        return logLevel;
    }
}
