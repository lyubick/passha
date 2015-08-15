/**
 *
 */
package db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
    private final String               USER_FILE_PATH = "user/";
    private final String               USER_FILE_EXT  = ".cif";

    private String                     file           = "";
    private volatile static UserFileIO self           = null;

    Database                           db             = null;

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

        File userFolder = new File(USER_FILE_PATH);

        if (!userFolder.exists())
        {
            userFolder.mkdirs();
            Logger.printDebug("Created user folder: " + userFolder.getAbsolutePath());
        }

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

        db = new Database(Utilities.readStringsFromFile(file), true);
    }

    public static UserFileIO init(String filename, boolean isNewUser) throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        self = new UserFileIO(filename, isNewUser);

        return self;
    }

    public static UserFileIO getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return self;
    }

    /**
     * Method will create Task and launch it within new Thread. Task will
     * synchronize contents of local Database with Database located in file.
     */
    public void sync()
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
                    Utilities.writeToFile(file, db.getEncrypted());
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

    public Database getDatabase()
    {
        return db;
    }
}
