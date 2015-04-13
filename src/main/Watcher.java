package main;

import java.util.*;
import java.io.*;

import utilities.Utilities;

public abstract class Watcher extends TimerTask
{
    private static Watcher self;
    private long           timeStamp;
    private File           file;

    public Watcher()
    {
        this.file = new File("~passha.running");
        this.timeStamp = file.lastModified();
        self = this;

        if (file.exists())
        {
            try
            {
                Utilities.writeToFile(file.getPath(), "P");
            }
            catch (Exceptions e)
            {
                System.exit(0);
            }

            System.exit(0);
        }
        else
        {
            try
            {
                file.createNewFile();
                Utilities.writeToFile(file.getPath(), "R");
            }
            catch (IOException e)
            {
                System.exit(0);
            }
            catch (Exceptions e)
            {
                System.exit(0);
            }
        }

        self = this;
    }

    public static Watcher getInstance()
    {
        return self;
    }

    public void die()
    {
        file.delete();
    }

    public final void run()
    {
        if (this.timeStamp < file.lastModified())
        {
            try
            {
                if (Utilities.readFromFile(file.getPath()).get(0).equals("P")) onChange();
                Utilities.writeToFile(file.getPath(), "R");
                this.timeStamp = file.lastModified();
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }

        }
    }

    protected abstract void onChange();
}
