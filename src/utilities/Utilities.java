/**
 *
 */
package utilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;

/**
 * @author lyubick
 *
 */
public final class Utilities
{
    public static long load64(byte[] input, int indexFrom)
    {
        long value = 0;

        for (int i = 0; i < 8; i++)
        {
            value = (value << 8) + (input[indexFrom + i] & 0xff);
        }

        return value;
    }

    public static void store64(long value, byte[] output, int indexFrom)
    {
        for (int i = 7; i >= 0; i--)
        {
            output[indexFrom + i] = (byte) (value & 0xff);
            value = value >> 8;
        }
    }

    public static byte[] objectToBytes(Object object) throws Exceptions
    {
        byte[] bytes = null;

        try
        {
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            ObjectOutputStream outObject = new ObjectOutputStream(byteArrayStream);
            outObject.writeObject(object);

            bytes = byteArrayStream.toByteArray();

            byteArrayStream.close();
            outObject.close();

        }
        catch (IOException e)
        {
            throw new main.Exceptions(XC.OBJECT_SERIALIZATION_FAILED);
        }

        return bytes;
    }

    public static Object bytesToObject(byte[] bytes) throws Exceptions
    {
        Object object = null;

        try
        {
            ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(bytes);
            ObjectInputStream inpObject = new ObjectInputStream(byteArrayStream);
            object = inpObject.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new main.Exceptions(XC.OBJECT_DESERIALIZATION_FAILED);
        }

        return object;
    }

    public static long toLong(byte in)
    {
        return (long) in & 0xFF;
    }

    public static long toLong(short in)
    {
        return (long) in & 0xFFFF;
    }

    public static long toLong(int in)
    {
        return (long) in & 0xFFFFFFFF;
    }

    public static String bytesToHex(byte[] bytes)
    {
        final char[] hexArray = "0123456789abcdef".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void writeToFile(String fileName, Vector<String> outStrings) throws Exceptions
    {
        Logger.printDebug("Writing to '" + fileName + "'; " + outStrings.size() + " lines.");

        try
        {
            PrintWriter writer = new PrintWriter(fileName);
            for (int i = 0; i < outStrings.size(); ++i)
            {
                writer.println(outStrings.elementAt(i));
            }
            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            throw new Exceptions(XC.WRITE_ERROR);
        }

        Logger.printDebug("Writing to '" + fileName + "' DONE!");
    }

    public static Vector<String> readFromFile(String fileName) throws Exceptions
    {
        Vector<String> inLines = new Vector<String>();

        Logger.printDebug("Reading from '" + fileName + "'");

        try
        {
            BufferedReader inFile = new BufferedReader(new FileReader(fileName));
            String tmpString = null;

            while ((tmpString = inFile.readLine()) != null)
            {
                inLines.add(tmpString);
            }
            inFile.close();

        }
        catch (IOException e)
        {
            throw new Exceptions(XC.READ_ERROR);
        }

        Logger.printDebug("Reading from '" + fileName + "'");

        return inLines;
    }

}