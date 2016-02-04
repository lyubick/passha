package core;

import db.Database;
import main.Exceptions;
import rsa.RSA;
import sha.SHA;

public class Vault
{
    private final String SALT_FILENAME = "FILENAME";
    private final String SALT_P        = "P";
    private final String SALT_Q        = "Q";
    private final String SALT_E        = "E";

    String               masterHash    = null;
    Database             database      = null;

    public Vault(String password, boolean isNewUser) throws Exceptions
    {
        // Generate master hash
        masterHash = SHA.getHashString(password);

        // Initialize RSA
        RSA rsa = new RSA(SHA.getHashString(masterHash + SALT_P), SHA.getHashString(masterHash + SALT_Q),
                SHA.getHashString(masterHash + SALT_E));

        // Initialize Database
        database = new Database(rsa, SHA.getHashString(masterHash + SALT_FILENAME), isNewUser);

        // FIXME Call garbage collector on finish
    }
}
