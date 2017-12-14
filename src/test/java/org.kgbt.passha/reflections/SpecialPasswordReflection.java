package org.kgbt.passha.reflections;

import java.lang.reflect.Field;
import org.kgbt.passha.db.SpecialPassword;

public class SpecialPasswordReflection
{
    private static SpecialPasswordReflection instance        = null;

    private Field                            keyShaCycles    = null;
    private Field                            keyComment      = null;
    private Field                            keyLength       = null;
    private Field                            keyName         = null;
    private Field                            keyParamsMask   = null;
    private Field                            keyShortcut     = null;
    private Field                            keySpecialChars = null;
    private Field                            keyUrl          = null;

    private SpecialPasswordReflection() throws NoSuchFieldException, SecurityException
    {
        Class<?> enumClass = null;
        for (Class<?> cls : SpecialPassword.class.getDeclaredClasses())
        {
            if (cls.getName().endsWith("MapKeys"))
            {
                enumClass = cls;
                break;
            }
        }

        keyShaCycles = enumClass.getDeclaredField("SHA_CYCLES");
        keyComment = enumClass.getDeclaredField("COMMENT");
        keyLength = enumClass.getDeclaredField("LENGTH");
        keyName = enumClass.getDeclaredField("NAME");
        keyParamsMask = enumClass.getDeclaredField("PARAMS_MASK");
        keyShortcut = enumClass.getDeclaredField("SHORTCUT");
        keySpecialChars = enumClass.getDeclaredField("SPECIAL_CHARS");
        keyUrl = enumClass.getDeclaredField("URL");

        keyShaCycles.setAccessible(true);
        keyComment.setAccessible(true);
        keyLength.setAccessible(true);
        keyName.setAccessible(true);
        keyParamsMask.setAccessible(true);
        keyShortcut.setAccessible(true);
        keySpecialChars.setAccessible(true);
        keyUrl.setAccessible(true);
    }

    public static SpecialPasswordReflection getInstance()
    {
        try
        {
            if (instance == null) instance = new SpecialPasswordReflection();
        }
        catch (NoSuchFieldException | SecurityException e)
        {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public Field SHA_CYCLES()
    {
        return keyShaCycles;
    }

    public Field COMMENT()
    {
        return keyComment;
    }

    public Field LENGTH()
    {
        return keyLength;
    }

    public Field NAME()
    {
        return keyName;
    }

    public Field PARAMS_MASK()
    {
        return keyParamsMask;
    }

    public Field SHORTCUT()
    {
        return keyShortcut;
    }

    public Field SPECIAL_CHARS()
    {
        return keySpecialChars;
    }

    public Field URL()
    {
        return keyUrl;
    }
}
