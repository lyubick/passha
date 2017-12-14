package org.kgbt.passha.reflections;

import java.lang.reflect.Field;

import org.kgbt.passha.core.Vault;

public class VaultReflection
{
    private static VaultReflection instance     = null;

    private Field                  database     = null;
    private Field                  saltFilename = null;

    private VaultReflection() throws NoSuchFieldException, SecurityException
    {
        database = Vault.class.getDeclaredField("database");
        database.setAccessible(true);

        saltFilename = Vault.class.getDeclaredField("SALT_FILENAME");
        saltFilename.setAccessible(true);
    }

    public static VaultReflection getInstance()
    {
        try
        {
            if (instance == null) instance = new VaultReflection();
        }
        catch (NoSuchFieldException | SecurityException e)
        {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public Field database()
    {
        return database;
    }

    public Field SALT_FILENAME()
    {
        return saltFilename;
    }
}
