package cryptosystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;
import db.UserFileIO;

public final class CryptoSystem
{
    private static final int    SHA_ITERATION_MIN_COUNT = 10;
    private static final int    SHA_ITERATION_MAX_COUNT = 255 - SHA_ITERATION_MIN_COUNT;

    private static Random       randomizer              = null;

    private SHA                 sha                     = null;
    private RSA                 rsa                     = null;

    private static byte[]       masterHash              = null;

    static Clipboard            clipboard               = null;
    static ClipboardContent     content                 = null;

    private static CryptoSystem self                    = null;

    private static final String SALT_FILENAME           = "FILENAME";
    private static final String SALT_P                  = "P";
    private static final String SALT_Q                  = "Q";
    private static final String SALT_E                  = "E";

    // ========== PRIVATE:

    private CryptoSystem(String masterPassword, boolean isNewUser) throws Exceptions
    {
        Logger.printDebug("CryptoSystem constructor STARTS...");

        // ========== SHA initialization START:
        Logger.printDebug("SHA init STARTS...");

        sha = new SHA();

        Logger.printDebug("SHA init DONE!");
        // ========== SHA initialization END:

        masterHash = sha.getHashBytes(masterPassword.getBytes());

        // ========== RSA initialization START:
        Logger.printDebug("RSA init STARTS...");

        rsa =
                new RSA(Utilities.bytesToHex(sha.getHashBytes((masterPassword + SALT_P).getBytes())),
                        Utilities.bytesToHex(sha.getHashBytes((masterPassword + SALT_Q).getBytes())),
                        Utilities.bytesToHex(sha.getHashBytes((masterPassword + SALT_E).getBytes())));

        Logger.printDebug("RSA init DONE!");
        // ========== RSA initialization END:

        // ========== FILE I/O initialization START:
        Logger.printDebug("File I/O init STARTS...");
        try
        {
            UserFileIO.init(sha.getHashString((Arrays.toString(masterHash) + SALT_FILENAME).getBytes()), isNewUser);
        }
        catch (Exceptions e)
        {
            if (e.getCode() == XC.FILE_DOES_NOT_EXISTS)
            {
                Logger.printError("No User file found. New user?");
                throw new Exceptions(XC.USER_UNKNOWN);
            }
            else
            {
                throw e;
            }
        }
        Logger.printDebug("File I/O init DONE...");
        // ========== FILE I/O initialization END:

        // ========== Randomizer initialization START:
        randomizer = new Random(System.currentTimeMillis());
        // ========== Randomizer initialization END:

        self = this;
    };

    // ========== PUBLIC STATICS :
    public static void init(String masterPassword, boolean isNewUser) throws Exceptions
    {
        Logger.printDebug("CryptoSystem init STARTS...");

        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        self = new CryptoSystem(masterPassword, isNewUser);

        Logger.printDebug("CryptoSystem init DONE!");
    }

    public static CryptoSystem getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return self;
    }

    // ========== PUBLIC:

    public long randSHACycles()
    {
        return SHA_ITERATION_MIN_COUNT + randomizer.nextInt(SHA_ITERATION_MAX_COUNT);
    }

    public String rsaEncrypt(byte[] message)
    {
        return rsa.encrypt(message);
    }

    public byte[] rsaDecrypt(String message)
    {
        return rsa.decrypt(message);
    }

    public String getPassword(long cycles, String pwdName)
    {
        byte[] tmp = null;

        try
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(masterHash.clone());
            outputStream.write((pwdName + cycles).getBytes());
            tmp = outputStream.toByteArray();
            Logger.printDebug("master hash is " + Utilities.bytesToHex(tmp) + "cycles " + cycles);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (cycles-- > 0)
        {
            tmp = sha.getHashBytes(tmp);
        }

        return Utilities.bytesToHex(tmp);
    }

    public String getHash(String toHash, String salt)
    {
        return sha.getHashString((toHash + salt).getBytes());
    }
}
