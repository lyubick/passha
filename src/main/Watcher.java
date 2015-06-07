package main;

import java.util.*;
import java.io.*;

import logger.Logger;

public abstract class Watcher extends TimerTask
{
    private static Watcher self;
    private File           file;

    public Watcher()
    {
        this.file = new File("~passha.running");
        self = this;

        if (file.exists())
        {
            Logger.printDebug("Instance already launched.");
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
            Logger.printDebug("Test if any other instance is running...");
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

        self = this;
    }

    public static Watcher getInstance()
    {
        return self;
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
