package org.kgbt.passha.core.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;

import org.kgbt.passha.core.GenericUI;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions.XC;

public class Terminator
{
    private static boolean isExitAllowed()
    {
        try
        {
            return VaultManager.getInstance().isReadyToExit() || GenericUI.getInstance().confirmUnsafeExit();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
        return false;
    }

    private static void exit(Exceptions e)
    {
        Logger.printFatal("Bye!");

        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions ignored)
        {
        }

        System.exit(e.getCode().ordinal());
    }

    public static void restart()
    {
        try
        {
            GenericUI.getInstance().restart();
        }
        catch (URISyntaxException | IOException | Exceptions e)
        {
            Terminator.terminate(new Exceptions(XC.RESTART_FAILED));
        }

        Terminator.exit(new Exceptions(XC.END));
    }

    public static void terminate(Exceptions e)
    {
        if (e.getCode().equals(XC.RESTART) || e.getCode().equals(XC.END))
        {
            if (!isExitAllowed())
                return;

            if (e.getCode().equals(XC.RESTART))
                restart();

            if (e.getCode().equals(XC.END))
                exit(e);
        }

        Logger.printFatal("TERMINATOR: FATAL ERROR OCCURED: " + e.getCode().name());

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3)
        {
            StackTraceElement element = stackTrace[2];
            Logger.printFatal("TERMINATOR: CLASS: " + element.getClassName() + " METHOD: " + element.getMethodName());
        }

        Logger.printFatal("TERMINATOR: STACK TRACE DUMP");

        StringWriter tracePrint = new StringWriter();
        e.printStackTrace(new PrintWriter(tracePrint));

        Logger.printFatal(tracePrint.toString());

        exit(e);
    }
}
