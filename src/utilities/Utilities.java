package utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Vector;
import java.util.stream.Collectors;

import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;

public final class Utilities
{
    public static final int DEFAULT_BUFFER_SIZE = 4096;

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

    public static BitSet getBitSet(String s)
    {
        BitSet b = new BitSet();
        // String format: "{x1, x2, ... xN}"

        if (s.length() > 0 && s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}')
        {
            Arrays.stream(s.substring(1, s.length() - 1).split(",")).map(String::trim).mapToInt(Integer::parseInt)
                .forEach((int e) -> b.set(e));
        }

        return b;
    }

    public static byte toByte(short in)
    {
        return (byte) ((byte) in & 0xFF);
    }

    public static byte toByte(int in)
    {
        return (byte) ((byte) in & 0xFF);
    }

    public static byte toByte(long in)
    {
        return (byte) ((byte) in & 0xFF);
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
        if (bytes == null) return null;

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

    public static void writeToFile(String fileName, String... strings) throws Exceptions
    {
        writeToFile(fileName, new Vector<String>(), strings);
    }

    public static void writeToFile(String fileName, Vector<String> outStrings, String... strings) throws Exceptions
    {
        writeToFile(fileName,
            Arrays.stream(strings).collect(Collectors.toCollection(() -> new Vector<String>(outStrings))));
    }

    public static void writeToFile(String fileName, Vector<String> outStrings) throws Exceptions
    {
        Logger.printDebug("Writing to '" + fileName + "'; " + outStrings.size() + " lines.");

        try
        {
            PrintWriter writer = new PrintWriter(fileName);

            for (String s : outStrings)
                writer.println(s);

            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            throw new Exceptions(XC.FILE_WRITE_ERROR);
        }

        Logger.printDebug("Writing to '" + fileName + "' DONE!");
    }

    public static void writeToFile(String fileName, byte[] writeable) throws Exceptions
    {
        Logger.printDebug("Writing to '" + fileName + "'; " + writeable.length + " bytes.");

        try
        {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(writeable);
            fos.close();
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.FILE_WRITE_ERROR);
        }

        Logger.printDebug("Writing to '" + fileName + "' DONE!");
    }

    public static byte[] readBytesFromFile(String fileName) throws Exceptions
    {
        Logger.printDebug("Reading bytes from '" + fileName + "'...");

        byte[] readable = new byte[(int) new File(fileName).length()];

        try
        {
            FileInputStream fis = new FileInputStream(fileName);
            fis.read(readable);
            fis.close();
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.FILE_READ_ERROR);
        }
        Logger.printDebug("Reading bytes from '" + fileName + "'" + "DONE!");

        return readable;
    }

    public static Vector<String> readStringsFromFile(String fileName) throws Exceptions
    {
        Vector<String> inLines = new Vector<String>();

        Logger.printDebug("Reading from '" + fileName + "'");

        try
        {
            Files.lines(Paths.get(fileName)).collect(Collectors.toCollection(() -> inLines));
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.FILE_READ_ERROR);
        }

        Logger.printDebug("Reading from '" + fileName + "'" + "DONE!");

        return inLines;
    }

    public static BigInteger hexToInt(String hexValue)
    {
        BigInteger out = new BigInteger("0");
        BigInteger curr = null;
        BigInteger power = new BigInteger("16");

        int i = hexValue.length() - 1;
        int pwr = 0;

        for (; i >= 0; --i)
        {
            curr = new BigInteger(hexValue.substring(i, i + 1), 16);
            out = out.add(curr.multiply(power.pow(pwr++)));
        }

        return out;
    }
}
