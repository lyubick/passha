/**
 *
 */
package Common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import Common.Exceptions.XC;
import Common.Return.RC;
import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public final class FileIO
{
    private String file = "";
    private String backup = "";

    private static FileIO self = null;

    private FileIO(String filename)
    {
        file = filename.toString();
        backup = filename.toString() + ".bckp";
    }

    public static FileIO init(String filename) throws Exceptions
    {
        if (self != null) System.exit(RC.ABEND.ordinal());
        self = new FileIO(filename);

        try
        {
            BufferedReader test = new BufferedReader(new FileReader(filename));
            test.readLine();
            test.close();
            Logger.printDebug("File exists!");
        }
        catch (IOException e)
        {
            try
            {
                PrintWriter create = new PrintWriter(filename);
                create.println("");
                create.close();
                Logger.printDebug("File created!");
            }
            catch (FileNotFoundException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

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
    public Vector<String> readFromFile() throws Exceptions
    {
        Vector<String> inLines = new Vector<String>();

        try
        {
            Logger.printDebug("Reading: '" + file + "'");

            BufferedReader inFile = new BufferedReader(new FileReader(file));

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
                throw new Exceptions(XC.CLOSE_ERROR);
            }

        }
        catch (FileNotFoundException e)
        {
            throw new Exceptions(XC.FILE_DOES_NOT_EXISTS); // TODO abend
        }

        return inLines;
    }

    /**
     * @brief writes @a outStrings to file @a fileName
     *
     * @param [in] outStrings - String[] to write to file
     *
     * @throws Exceptions
     */
    public RC writeToFile(Vector<String> outStrings) throws Exceptions
    {
        try
        {
            Logger.printDebug("Writing: '" + file + "'; " + outStrings.size() + " lines.");

            PrintWriter writer = new PrintWriter(file);

            for (int i = 0; i < outStrings.size(); ++i)
            {
                writer.println(outStrings.elementAt(i));
            }

            writer.flush();
            writer.close();

        }
        catch (FileNotFoundException e)
        {
            throw new Exceptions(XC.FILE_DOES_NOT_EXISTS); // TODO abend
        }

        return Return.check(RC.OK);
    }
}
