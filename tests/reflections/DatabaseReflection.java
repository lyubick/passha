package reflections;

import java.lang.reflect.Field;

import db.Database;

public class DatabaseReflection
{
    private static DatabaseReflection instance     = null;

    private Field                     vaultFile    = null;
    private Field                     vaultNameKey = null;

    private DatabaseReflection() throws NoSuchFieldException, SecurityException
    {
        vaultFile = Database.class.getDeclaredField("vaultFile");
        vaultFile.setAccessible(true);

        vaultNameKey = Database.class.getDeclaredField("VAULT_NAME_KEY");
        vaultNameKey.setAccessible(true);
    }

    public static DatabaseReflection getInstance()
    {
        try
        {
            if (instance == null) instance = new DatabaseReflection();
        }
        catch (NoSuchFieldException | SecurityException e)
        {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public Field vaultFile()
    {
        return vaultFile;
    }

    public Field VAULT_NAME_KEY()
    {
        return vaultNameKey;
    }
}
