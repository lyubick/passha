package org.kgbt.passha.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Vector;
import java.util.stream.Collectors;

import org.kgbt.passha.logger.Logger;
import org.kgbt.passha.main.Exceptions;
import org.kgbt.passha.main.Exceptions.XC;

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
        try
        {
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            ObjectOutputStream outObject = new ObjectOutputStream(byteArrayStream);
            outObject.writeObject(object);

            byte[] bytes = byteArrayStream.toByteArray();

            byteArrayStream.close();
            outObject.close();

            return bytes;
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.OBJECT_SERIALIZATION_FAILED);
        }
    }

    public static Object bytesToObject(byte[] bytes) throws Exceptions
    {
        try
        {
            ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(bytes);
            ObjectInputStream inpObject = new ObjectInputStream(byteArrayStream);
            Object object = inpObject.readObject();
            inpObject.close();
            byteArrayStream.close();
            return object;
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new Exceptions(XC.OBJECT_DESERIALIZATION_FAILED);
        }
    }

    public static BitSet getBitSet(String s)
    {
        BitSet b = new BitSet();
        // String format: "{x1, x2, ... xN}"

        if (!s.isEmpty() && s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}' && s.length() > 2)
        {
            Arrays.stream(s.substring(1, s.length() - 1).split(",")).map(String::trim).mapToInt(Integer::parseInt)
                .forEach(b::set);
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
        return (long) in;
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
        writeToFile(fileName, new Vector<>(), strings);
    }

    public static void writeToFile(String fileName, Vector<String> outStrings, String... strings) throws Exceptions
    {
        writeToFile(fileName,
            Arrays.stream(strings).collect(Collectors.toCollection(() -> new Vector<>(outStrings))));
    }

    public static void writeToFile(String fileName, Vector<String> outStrings) throws Exceptions
    {
        Logger.printDebug("Writing to '" + fileName + "'; " + outStrings.size() + " lines.");
        try
        {
            Files.write(Paths.get(fileName), outStrings);
        }
        catch (IOException e)
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
            Files.write(Paths.get(fileName), writeable);
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.FILE_WRITE_ERROR);
        }

        Logger.printDebug("Writing to '" + fileName + "' DONE!");
    }

    public static byte[] readBytesFromFile(String fileName) throws Exceptions
    {
        try {
            Logger.printDebug("Reading bytes from '" + fileName + "'...");
            byte[] data = Files.readAllBytes(Paths.get(fileName));
            Logger.printDebug("Reading bytes from '" + fileName + "'" + "DONE!");
            return data;
        } catch (IOException ignore) {
            throw new Exceptions(XC.FILE_READ_ERROR);
        }
    }

    public static Vector<String> readStringsFromFile(String fileName) throws Exceptions
    {
        Vector<String> inLines = new Vector<>();

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
        BigInteger power = new BigInteger("16");

        int i = hexValue.length() - 1;
        int pwr = 0;

        for (; i >= 0; --i)
        {
            BigInteger curr = new BigInteger(hexValue.substring(i, i + 1), 16);
            out = out.add(curr.multiply(power.pow(pwr++)));
        }

        return out;
    }
}
