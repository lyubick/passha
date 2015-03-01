/**
 *
 */
package Common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Common.Exceptions.CODES;

/**
 * @author lyubick
 *
 */
public final class Utilities
{
    /**
     * @brief converts byte[] to long (8 bytes). Big Endian;
     *
     * @param [in] input - bytes array input
     * @param [in] indexFrom - index to first byte
     * @return long value
     *
     * @note does not validate input array length
     */
    public static long load64(byte[] input, int indexFrom)
    {
        long value = 0;

        for (int i = 0; i < 8; i++)
        {
            value = (value << 8) + (input[indexFrom + i] & 0xff);
        }

        return value;
    }

    /**
     * @brief converts long value to byte[] (8 bytes). Big Endian
     *
     * @param [in] value - long value to be converted
     * @param [out] output - bytes array to be converted to
     * @param [in] indexFrom - start index in bytes array
     *
     * @note does not validate output array length
     */
    public static void store64(long value, byte[] output, int indexFrom)
    {
        for (int i = 7; i >= 0; i--)
        {
            output[indexFrom + i] = (byte) (value & 0xff);
            value = value >> 8;
        }
    }

    /**
     *
     * @param object
     * @return
     * @throws Exceptions
     */
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
            throw new Common.Exceptions(CODES.BLACK_MAGIC);
        }

        return bytes;
    }

    /**
     *
     * @param bytes
     * @return
     * @throws Exceptions
     */
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
            throw new Common.Exceptions(CODES.BLACK_MAGIC);
        }

        return object;
    }

    /* Casts ignoring sign, clang memcpy analog */
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
}
