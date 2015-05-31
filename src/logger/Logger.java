/**
 *
 */
package logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import main.Exceptions;
import main.Exceptions.XC;

/**
 * @author lyubick
 *
 */
public final class Logger
{
    private final String  LOG_PATH        = "logs/";
    private final String  LOG_NAME        = "PASSHA";
    private final String  LOG_EXT         = ".log";

    private static Logger self            = null;

    private PrintWriter   writer          = null;

    private int           fileNameWidth   = 20;
    private int           lineWidth       = 4;
    private int           methodNameWidth = 20;

    public enum LOGLEVELS
    {
        SILENT,
        DEBUG,
        ERROR
    }

    private String getTime()
    {
        return Long.toString(System.currentTimeMillis());
    }

    public static void printError(String msg)
    {
        try
        {
            Logger.getInstance().prepareAndLog(LOGLEVELS.ERROR, msg);
        }
        catch (Exceptions e)
        {
            return;
        }
    }

    public static void printDebug(String msg)
    {
        try
        {
            Logger.getInstance().prepareAndLog(LOGLEVELS.DEBUG, msg);
        }
        catch (Exceptions e)
        {
            return;
        }
    }

    private void prepareAndLog(LOGLEVELS lvl, String msg)
    {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];

        String methodName = e.getMethodName();
        String fileName = "(" + e.getFileName();

        String line = "" + e.getLineNumber() + ")";

        fileNameWidth = Math.max(fileNameWidth, fileName.length());
        lineWidth = Math.max(lineWidth, line.length());
        methodNameWidth = Math.max(methodNameWidth, methodName.length());

        writeToFileAndToScreen(
                String.format("[%1$s] %2$" + fileNameWidth + "s:%3$-" + lineWidth + "s %4$-" + methodNameWidth + "s ",
                        getTime(), fileName, line, methodName) + lvl.name() + ": " + msg, lvl);
    }

    private void writeToFileAndToScreen(String log, LOGLEVELS lvl)
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

    private Logger() throws Exceptions
    {
        try
        {
            int id = 0;
            while (Files.exists(Paths.get(LOG_PATH + LOG_NAME + "_" + id + LOG_EXT)))
                id++;

            writer = new PrintWriter(LOG_PATH + LOG_NAME + "_" + id + LOG_EXT);
        }
        catch (FileNotFoundException e)
        {
            throw new Exceptions(XC.INIT_FAILURE);
        }
    }

    public static void loggerON() throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        self = new Logger();
    }

    public void loggerOFF()
    {
        writer.close();
        self = null;
    }

    public static Logger getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }
}
