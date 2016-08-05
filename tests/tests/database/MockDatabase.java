package tests.database;

import db.Database;
import main.Exceptions;

public class MockDatabase extends Database
{

    public MockDatabase() throws Exceptions
    {
        super(null, null, false, null);
        throw new RuntimeException("Don't use this");
    }

    public static String getKeyVaultName()
    {
        return VAULT_NAME_KEY;
    }
}
