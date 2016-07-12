package main;

import java.util.*;
import java.io.*;

import logger.Logger;

public abstract class Watcher extends TimerTask
{
    private File file;

    public Watcher()
    {
        this.file = new File("~passha.running");

        if (file.exists())
        {
            Logger.printTrace("Instance already launched.");
        }
        else
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                Logger.printError("Failed to create launch file [" + e.getMessage() + "]");
                System.exit(0);
            }
        }

        try
        {
            Logger.printTrace("Test if any other instance is running...");
            Thread.sleep(Settings.ENV_VARS.SINGLE_INSTANCE_CHECK_WAIT);
        }
        catch (InterruptedException e)
        {
            Logger.printError("Unexpected interruption of Watcher.");
            System.exit(0);
        }

        if (file.exists())
        {
            Logger.printDebug("No other instance exists.");
            file.delete();
        }
        else
        {
            Logger.printError("Looks like other instance is running.");
            System.exit(0);
        }

    }

    public final void run()
    {
        if (file.exists())
        {
            file.delete();
            onChange();
        }
    }

    protected abstract void onChange();
}
