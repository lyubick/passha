package tests.database;

import db.SpecialPassword;

/**
 * This class provides access to members of protected enum MapKeys for tests
 *
 */
public class MockSpecialPassword extends SpecialPassword
{
    public MockSpecialPassword()
    {
        super(null, null);
        throw new RuntimeException("Dont use this !!!!");
    }

    public static String getMapKeyShaCycles()
    {
        return MapKeys.SHA_CYCLES.toString();
    }

    public static String getMapKeyComment()
    {
        return MapKeys.COMMENT.toString();
    }

    public static String getMapKeyLength()
    {
        return MapKeys.LENGTH.toString();
    }

    public static String getMapKeyName()
    {
        return MapKeys.NAME.toString();
    }

    public static String getMapKeyParamsMask()
    {
        return MapKeys.PARAMS_MASK.toString();
    }

    public static String getMapKeyShortcut()
    {
        return MapKeys.SHORTCUT.toString();
    }

    public static String getMapKeySpecialChars()
    {
        return MapKeys.SPECIAL_CHARS.toString();
    }

    public static String getMapKeyUrl()
    {
        return MapKeys.URL.toString();
    }

    public static boolean cmpAllFields(SpecialPassword sp, String name, String comment, String url, int length,
        int shaCycles)
    {
        return (name == null || sp.getName().equals(name)) && (comment == null || sp.getComment().equals(comment))
            && (url == null || sp.getUrl().equals(url)) && (length == -1 || sp.getLength() == length)
            && (shaCycles == -1 || sp.getShaCycles() == shaCycles);
    }

    public static boolean cmpAllFields(SpecialPassword sp, String name, String comment, String url, String length,
        String shaCycles)
    {
        return cmpAllFields(sp, name, comment, url, Integer.parseInt(length), Integer.parseInt(shaCycles));
    }

    public static boolean cmpAllFields(SpecialPassword sp, SpecialPassword another)
    {
        return cmpAllFields(sp, another.getName(), another.getComment(), another.getUrl(), another.getLength(),
            (int) another.getShaCycles());
    }
}
