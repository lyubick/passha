/**
 *
 */
package Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import Common.Exceptions.XC;
import Common.Exceptions;
import Main.Terminator;

/**
 * @author lyubick
 *
 */
public final class Logger // Static class
{
    public enum LOGLEVELS
    {
        SILENT,
        DEBUG,
        ERROR
    }

    private static int         fileNameWidth   = 20;
    private static int         lineWidth       = 4;
    private static int         methodNameWidth = 20;

    private static boolean     initialized     = false;

    private static PrintWriter writer          = null;

    private static String getTime()
    {
        return Long.toString(System.currentTimeMillis());
    }

    public static void printError(String msg)
    {
        if (!initialized) return;
        prepareAndLog(LOGLEVELS.ERROR, msg);
    }

    public static void printDebug(String msg)
    {
        if (!initialized) return;
        prepareAndLog(LOGLEVELS.DEBUG, msg);
    }

    private static void prepareAndLog(LOGLEVELS lvl, String msg)
    {
        if (!initialized) return;

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];

        String methodName = e.getMethodName();
        String fileName = e.getFileName();

        int line = e.getLineNumber();

        fileNameWidth = Math.max(fileNameWidth, fileName.length());
        lineWidth = Math.max(lineWidth, Integer.toString(line).length());
        methodNameWidth = Math.max(methodNameWidth, methodName.length());

        writeToFileAndToScreen(
                String.format("[%1$s] %2$" + fileNameWidth + "s @ %3$-" + lineWidth + "d %4$-"
                        + methodNameWidth + "s ", getTime(), fileName, line, methodName)
                        + lvl.name() + msg, lvl);
    }

    private static void writeToFileAndToScreen(String log, LOGLEVELS lvl)
    {
        switch (lvl)
        {
            case ERROR:
                System.err.println(log);
                break;
            case DEBUG:
                System.out.println(log);
                break;
            default:
                break;
        }

        writer.println(log);
    }

    public static void loggerON()
    {
        if (!initialized)
        {
            try
            {
                writer = new PrintWriter("bin/" + getTime());
            }
            catch (FileNotFoundException e)
            {
                Terminator.terminate(new Exceptions(XC.INIT_FAILURE));
            }
            initialized = true;
        }
        else
        {
            Terminator.terminate(new Exceptions(XC.INIT_FAILURE));
        }
    }

    public static void loggerOFF()
    {
        if (initialized) writer.close();
    }
}
