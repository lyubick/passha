/**
 *
 */
package db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

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

    private String            file           = "";
    private static UserFileIO self           = null;

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
                    Terminator.terminate(new Exceptions(XC.WRITE_ERROR));
                }
            }
            else
                throw new Exceptions(XC.FILE_DOES_NOT_EXISTS);
        }

        file = filename.toString();
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

    public Vector<String> readFromUserFile()
    {
        try
        {
            return Utilities.readFromFile(file);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(new Exceptions(XC.READ_ERROR));
        }
        return null;
    }

    public void writeToUserFile(Vector<String> outStrings) throws Exceptions
    {
        try
        {
            Utilities.writeToFile(file, outStrings);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }
}
