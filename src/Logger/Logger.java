/**
 *
 */
package Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import Logger.LOGLEVELS;

import passha.ReturnCodes;

/**
 * @author lyubick
 *
 */
public final class Logger // Static class
{
    private static LOGLEVELS logLevel;

    private static String LOG_ADDS = "";

    private static long time;

    private static boolean initialized;

    private static PrintWriter writer;

    private static String getTime()
    {
        time = System.currentTimeMillis();
        return Long.toString(time);
    }

    public static void print(LOGLEVELS lvl, String msg)
    {
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

        System.out.println(LOG_ADDS + msg + " [" + getTime() + "]");
        writer.println(LOG_ADDS + msg + " [" + getTime() + "]");
    }

    public static ReturnCodes loggerON(LOGLEVELS lvl)
    {
        logLevel = lvl;

        if (!initialized)
        {
            try {
                writer = new PrintWriter(getTime());
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
