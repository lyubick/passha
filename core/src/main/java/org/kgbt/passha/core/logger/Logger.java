package org.kgbt.passha.core.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;

public final class Logger
{
    private static Logger self = null;

    private final static Output DEFAULT_LOG_OUTPUT = new Output()
    {
        private final String LOG_PATH = "logs/";
        private final String LOG_NAME = "PASSHA";
        private final String LOG_EXT = ".log";

        private PrintWriter writer = null;

        @Override
        public void init() throws Exceptions
        {
            File logsFolder = new File(LOG_PATH);
            if (!logsFolder.exists())
                logsFolder.mkdirs();

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

        @Override
        public void log(LOGLEVELS lvl, String log)
        {
            switch (lvl)
            {
                case FATAL:
                case ERROR:
                    System.err.println(log);
                    break;

                case TRACE:
                case DEBUG:
                    System.out.println(log);
                    break;

                default:
                    break;
            }

            writer.println(log);
        }

        @Override
        public void terminate()
        {
            writer.close();
        }
    };

    private int fileNameWidth   = 20;
    private int lineWidth       = 4;
    private int methodNameWidth = 20;

    private LOGLEVELS logLevelThreshold = LOGLEVELS.TRACE;

    private Output logOutput;

    public enum LOGLEVELS
    {
        TRACE,      // Mostly for checkpoints in code
        DEBUG,      // Debug information ( Data dumps e.t.c )
        ERROR,      // Error notifications
        FATAL,      // Messages about abnormal termination of the program
        SILENT      // Disable logging
    }

    private String getTime()
    {
        return Long.toString(System.currentTimeMillis());
    }

    public static void printFatal(String msg)
    {
        try
        {
            Logger.getInstance().prepareAndLog(LOGLEVELS.FATAL, msg);
        }
        catch (Exceptions ignored)
        {
        }
    }

    public static void printError(String msg)
    {
        try
        {
            Logger.getInstance().prepareAndLog(LOGLEVELS.ERROR, msg);
        }
        catch (Exceptions ignored)
        {
        }
    }

    public static void printDebug(String msg)
    {
        try
        {
            Logger.getInstance().prepareAndLog(LOGLEVELS.DEBUG, msg);
        }
        catch (Exceptions ignored)
        {
        }
    }

    public static void printTrace(String msg)
    {
        try
        {
            Logger.getInstance().prepareAndLog(LOGLEVELS.TRACE, msg);
        }
        catch (Exceptions ignored)
        {
        }
    }

    private void prepareAndLog(LOGLEVELS lvl, String msg)
    {
        if (lvl.compareTo(logLevelThreshold) < 0)
            return;

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];

        String methodName = e.getMethodName();
        String fileName = "(" + e.getFileName();

        String line = "" + e.getLineNumber() + ")";

        fileNameWidth = Math.max(fileNameWidth, fileName.length());
        lineWidth = Math.max(lineWidth, line.length());
        methodNameWidth = Math.max(methodNameWidth, methodName.length());

        logOutput.log(lvl, String
            .format("[%1$s] %2$" + fileNameWidth + "s:%3$-" + lineWidth + "s %4$-" + methodNameWidth + "s ", getTime(),
                fileName, line, methodName) + lvl.name() + ": " + msg);
    }

    private Logger(String lvlString, Output customLogOutput) throws Exceptions
    {
        logOutput = customLogOutput;
        logOutput.init();

        // Treat missing cmd line argument as default value
        if (lvlString == null)
            lvlString = "";

        switch (lvlString.toUpperCase())
        {
            default:       // by default print all logs
            case "ALL":
            case "TRACE":
                logLevelThreshold = LOGLEVELS.TRACE;
                break;

            case "DEBUG":
                logLevelThreshold = LOGLEVELS.DEBUG;
                break;

            case "ERROR":
                logLevelThreshold = LOGLEVELS.ERROR;
                break;

            case "FATAL":
                logLevelThreshold = LOGLEVELS.FATAL;
                break;

            case "SILENT":
            case "OFF":
                logLevelThreshold = LOGLEVELS.SILENT;
                break;
        }
    }

    public static void loggerON(String lvlString) throws Exceptions
    {
        if (self != null)
            throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        self = new Logger(lvlString, DEFAULT_LOG_OUTPUT);
    }

    public static void loggerON(String lvlString, Output customLogOutput) throws Exceptions
    {
        if (self != null)
            throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        self = new Logger(lvlString, customLogOutput);
    }

    private void end()
    {
        logOutput.terminate();
        self = null;
    }

    public void loggerOFF()
    {
        try
        {
            Logger.getInstance().end();
        }
        catch (Exceptions ignored)
        {
        }
    }

    public static Logger getInstance() throws Exceptions
    {
        if (self == null)
            throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }
}
