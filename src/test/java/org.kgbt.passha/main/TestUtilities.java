package org.kgbt.passha.main;

import org.kgbt.passha.utilities.Utilities;

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

    public static class SpecialPassword
    {

        public static boolean cmpAllFields(org.kgbt.passha.db.SpecialPassword sp, String name, String comment, String url, int length,
            int shaCycles)
        {
            return (name == null || sp.getName().equals(name)) && (comment == null || sp.getComment().equals(comment))
                && (url == null || sp.getUrl().equals(url)) && (length == -1 || sp.getLength() == length)
                && (shaCycles == -1 || sp.getShaCycles() == shaCycles);
        }

        public static boolean cmpAllFields(org.kgbt.passha.db.SpecialPassword sp, String name, String comment, String url,
            String length, String shaCycles)
        {
            return cmpAllFields(sp, name, comment, url, Integer.parseInt(length), Integer.parseInt(shaCycles));
        }

        public static boolean cmpAllFields(org.kgbt.passha.db.SpecialPassword sp, org.kgbt.passha.db.SpecialPassword another)
        {
            return cmpAllFields(sp, another.getName(), another.getComment(), another.getUrl(), another.getLength(),
                (int) another.getShaCycles());
        }
    }
}
