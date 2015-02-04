/**
 *
 */
package Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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

    // probably it will be easier to create print for each log level in addition,
    // like printError(String msg), printWarning(String msg) etc.. it will be easier to use from code
    
    public static void print(LOGLEVELS lvl, String msg)
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
        
        // we can use this to find method that called log function and add this info to log as well
        /*
StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
//// The last element of the array represents the bottom of the stack, which is the least recent method invocation in the sequence.
StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
String methodName = e.getMethodName();
        */

        // maybe create getFormattedTimeForLog() for ("[" + getTime() + "]")
        // or writeToFileAndToScreen(String log) - event better
        System.out.println("[" + getTime() + "]" + LOG_ADDS + msg);
        writer.println("[" + getTime() + "]" + LOG_ADDS + msg);
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
