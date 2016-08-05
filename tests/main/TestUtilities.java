package main;

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
            throw new RuntimeException(e.getCode().toString());
        }
    }
}
