package tests.vault;

import core.Vault;
import db.Database;
import main.Exceptions;

/**
 * Class is used for Vault testing - it provides access to Vault class members
 *
 */
public class TestVault extends Vault
{

    public TestVault(byte[] hash, boolean isNewUser) throws Exceptions
    {
        super(hash, isNewUser);
    }

    Database getDatabase()
    {
        return database;
    }

    String getSaltFilename()
    {
        return SALT_FILENAME;
    }
}
