/**
 *
 */
package db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

import javafx.concurrent.Task;
import utilities.Utilities;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;

/**
 * @author curious-odd-man
 *
 */
public final class UserFileIO
{
    private final String      USER_FILE_PATH = "user/";
    private final String      USER_FILE_EXT  = ".cif";

    private Vector<String>    encryptedDB    = null;

    private String            file           = "";
    private static UserFileIO self           = null;

    public enum Status
    {
        SAVED,
        FAILED,
        IN_PROGRESS,

        UNKNOWN
    };

    volatile Status status = Status.UNKNOWN;

    public Status getStatus()
    {
        return status;
    }

    private UserFileIO(String filename, boolean isNewUser) throws Exceptions
    {
        filename = USER_FILE_PATH + filename + USER_FILE_EXT;

        File test = new File(filename);

        if (!test.exists())
        {
            if (isNewUser)
            {
                PrintWriter create;
                try
                {
                    create = new PrintWriter(filename);
                    create.close();
                    Logger.printDebug("File created!");
                }
                catch (FileNotFoundException e)
                {
                    Terminator.terminate(new Exceptions(XC.FILE_WRITE_ERROR));
                }
            }
            else
                throw new Exceptions(XC.FILE_DOES_NOT_EXISTS);
        }

        file = filename.toString();

        // Load a copy of DB from File into the memory
        encryptedDB = Utilities.readStringsFromFile(file);
    }

    public static UserFileIO init(String filename, boolean isNewUser) throws Exceptions
    {
        if (self != null) Terminator.terminate(new Exceptions(XC.INSTANCE_ALREADY_EXISTS));

        self = new UserFileIO(filename, isNewUser);

        return self;
    }

    public static UserFileIO getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return self;
    }

    public Vector<String> read()
    {
        return encryptedDB;
    }

    /**
     * Method will create Task and launch it within new Thread. Task will
     * synchronize contents of local Database with Database located in file.
     */
    private void sync()
    {
        status = Status.IN_PROGRESS;

        Logger.printDebug("Database sync between File and Memory STARTS...");

        Task<Void> task = new Task<Void>()
        {

            @Override
            protected Void call() throws Exception
            {
                try
                {
                    Utilities.writeToFile(file, encryptedDB);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                return null;
            }
        };

        task.setOnSucceeded(EventHandler -> {
            status = Status.SAVED;
            Logger.printDebug("Database sync between File and Memory END");
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Method receives encrypted String (Special Password), will add it into
     * encryptedDB and sync.
     *
     * @param entry
     */
    public void add(String entry)
    {
        Logger.printError(entry);

        encryptedDB.addElement(entry);
        sync();
    }

    /**
     * Method receives encrypted String (Special Password), will delete it form
     * encryptedDB and sync.
     *
     * @param entry
     */
    public void delete(String entry)
    {
        Logger.printError(entry);
        for (String in : encryptedDB)
        {
            Logger.printError(in);
            if (in.equals(entry))
            {
                encryptedDB.remove(encryptedDB.indexOf(in));
                break;
            }
        }
        sync();
    }

    /**
     * Method receives two encrypted Strings (Special Password), will substitute
     * Old Entry with New Entry.
     *
     * @param newEntry
     * @param oldEntry
     */
    public void replace(String newEntry, String oldEntry)
    {
        Logger.printError(oldEntry);
        Logger.printError(newEntry);
        for (String in : encryptedDB)
        {
            Logger.printError(in);
            if (in.equals(oldEntry))
            {
                encryptedDB.add(encryptedDB.indexOf(in), newEntry);
                encryptedDB.remove(encryptedDB.indexOf(in) + 1);
                break;
            }
        }

        sync();
    }

}
