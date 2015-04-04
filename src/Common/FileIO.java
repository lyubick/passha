/**
 *
 */
package Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import Common.Exceptions.XC;
import Logger.Logger;
import Main.ABEND;

/**
 * @author curious-odd-man
 *
 */
public final class FileIO
{
    private String        file = "";
    // private String backup = "";

    private static FileIO self = null;

    private FileIO(String filename)
    {
        file = filename.toString();
        // backup = filename.toString() + ".bckp";
    }

    public static FileIO initUserFile(String filename, boolean isNewUser) throws Exceptions
    {
        filename += ".cif";

        if (self != null) ABEND.terminate(new Exceptions(XC.INSTANCE_ALREADY_EXISTS));

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
                    ABEND.terminate(new Exceptions(XC.SECURITY_BREACH));
                }
            }
            else
                throw new Exceptions(XC.FILE_DOES_NOT_EXISTS);
        }

        self = new FileIO(filename);
        return self;
    }

    public static FileIO getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);
        return self;
    }

    /**
     * @brief gets String[] from @a fileName (reads all file)
     *
     * @param [in] fileName - name of file to read from
     *
     * @throws Exceptions
     *
     * @return String[] - one element, one line read from file
     */
    public static Vector<String> readFile(String fileName) throws Exceptions
    {
        Vector<String> inLines = new Vector<String>();

        try
        {
            Logger.printDebug("Reading: '" + fileName + "'");

            BufferedReader inFile = new BufferedReader(new FileReader(fileName));

            try
            {
                String tmpString = null;

                while ((tmpString = inFile.readLine()) != null)
                {
                    inLines.add(tmpString);
                }

            }
            catch (IOException e)
            {
                throw new Exceptions(XC.READ_ERROR);
            }

            try
            {
                inFile.close();
            }
            catch (IOException e)
            {
                ABEND.terminate(new Exceptions(XC.READ_ERROR));
            }

        }
        catch (FileNotFoundException e)
        {
            ABEND.terminate(new Exceptions(XC.READ_ERROR));
        }

        return inLines;
    }

    public Vector<String> readUserFile() throws Exceptions
    {
        return readFile(file);
    }

    /**
     * @brief writes @a outStrings to file @a fileName
     *
     * @param [in] outStrings - String[] to write to file
     *
     * @throws Exceptions
     */
    public void writeToUserFile(Vector<String> outStrings) throws Exceptions
    {
        try
        {
            writeToFile(file, outStrings);
        }
        catch (FileNotFoundException e)
        {
            ABEND.terminate(new Exceptions(XC.WRITE_ERROR));
        }
    }

    public static void writeToFile(String fileName, Vector<String> outStrings)
            throws FileNotFoundException
    {
        Logger.printDebug("Writing: '" + fileName + "'; " + outStrings.size() + " lines.");

        PrintWriter writer = new PrintWriter(fileName);

        for (int i = 0; i < outStrings.size(); ++i)
        {
            writer.println(outStrings.elementAt(i));
        }

        writer.flush();
        writer.close();
    }
}
