package org.kgbt.passha.reflections;

import java.lang.reflect.Field;

import org.kgbt.passha.core.VaultManager;

public class VaultManagerReflection
{
    private static VaultManagerReflection instance = null;

    private Field                         self     = null;
    private Field                         vaults   = null;

    private VaultManagerReflection() throws NoSuchFieldException, SecurityException
    {
        self = VaultManager.class.getDeclaredField("self");
        self.setAccessible(true);

        vaults = VaultManager.class.getDeclaredField("vaults");
        vaults.setAccessible(true);
    }

    public static VaultManagerReflection getInstance()
    {
        try
        {
            if (instance == null) instance = new VaultManagerReflection();
        }
        catch (NoSuchFieldException | SecurityException e)
        {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public Field self()
    {
        return self;
    }

    public Field vaults()
    {
        return vaults;
    }
}
