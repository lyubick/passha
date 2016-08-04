package main;

import main.Exceptions.XC;
import utilities.Utilities;

public final class TestUtilities
{
    /**
     * Use this in Streams, since they only support RuntimeExceptions
     */
    public static Object bytesToObject(byte[] bytes)
    {
        try
        {
            return Utilities.bytesToObject(bytes);
        }
        catch (Exceptions e)
        {
            throw new RuntimeException(XC.OBJECT_DESERIALIZATION_FAILED.toString());
        }
    }
}
