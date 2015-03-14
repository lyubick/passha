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
import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public final class FileIO
{
    /**
     * @brief gets String[] from @a fileName (reads all file)
     *
     * @param [in] fileName - name of file to read from
     *
     * @throws Exceptions
     *
     * @return String[] - one element, one line read from file
     */
    public static String[] readTextFile(String fileName) throws Exceptions
    {
        String outStrings[] = null;

        try
        {
            Logger.printDebug("reading: " + fileName);
            BufferedReader inFile = new BufferedReader(new FileReader(fileName));
            Vector<String> inLines = new Vector<String>();

            try
            {
                String tmpString = null;

                while ((tmpString = inFile.readLine()) != null)
                {
                    inLines.add(tmpString);
                }

                outStrings = inLines.toArray(new String[inLines.size()]);
                Logger.printDebug("got " + outStrings.length + " lines");

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
            throw new Exceptions(XC.FILE_DOES_NOT_EXISTS);
        }

        return outStrings;
    }

    /**
     * @brief writes @a outStrings to file @a fileName
     *
     * @param [in] outStrings - String[] to write to file
     * @param [in] fileName - name of file to read from
     *
     * @throws Exceptions
     */
    public static void writeTextFile(String outStrings[], String fileName) throws Exceptions
    {
        try
        {
            Logger.printDebug("writing to: " + fileName + "; " + outStrings.length + " lines");
            PrintWriter writer = new PrintWriter(fileName);

            for (int i = 0; i < outStrings.length; ++i)
            {
                writer.println(outStrings[i]);
            }

            writer.flush();
            writer.close();

        }
        catch (FileNotFoundException e)
        {
            throw new Exceptions(XC.FILE_DOES_NOT_EXISTS);
        }
    }
}
