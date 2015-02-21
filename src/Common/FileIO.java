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

import Common.Exceptions.CODES;

/**
 * @author curious-odd-man
 *
 */
public final class FileIO
{
    public static String[] readTextFile(String fileName) throws Exceptions
    {
        String outStrings[] = null;

        try
        {
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

            }
            catch (IOException e)
            {
                throw new Exceptions(CODES.READ_ERROR);
            }

            try
            {
                inFile.close();
            }
            catch (IOException e)
            {
                throw new Exceptions(CODES.CLOSE_ERROR);
            }

        }
        catch (FileNotFoundException e)
        {
            throw new Exceptions(CODES.FILE_DOES_NOT_EXISTS);
        }

        return outStrings;
    }


    public static void writeTextFile(String outStrings[], String fileName) throws Exceptions
    {
        try {
            PrintWriter writer = new PrintWriter(fileName);

            for (int i = 0; i < outStrings.length; ++i )
            {
                writer.println(outStrings[i]);
            }

            writer.flush();
            writer.close();

        } catch (FileNotFoundException e) {
            throw new Exceptions(CODES.FILE_DOES_NOT_EXISTS);
        }
    }
}
