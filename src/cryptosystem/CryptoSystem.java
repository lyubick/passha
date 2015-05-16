/**
 *
 */
package cryptosystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import logger.Logger;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;
import db.SpecialPassword;
import db.UserFileIO;
import javafx.concurrent.Task;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * @author lyubick
 *
 */
public final class CryptoSystem
{
    private static final int    SHA_ITERATION_MIN_COUNT = 10;
    private static final int    SHA_ITERATION_MAX_COUNT = 255 - SHA_ITERATION_MIN_COUNT;

    private static Random       randomizer              = null;

    private static SHA          sha                     = null;
    private static RSA          rsa                     = null;

    private static byte[]       masterHash              = null;

    private static byte[]       keyHashP                = null;
    private static byte[]       keyHashQ                = null;
    private static byte[]       keyHashE                = null;

    static Clipboard            clipboard               = null;
    static ClipboardContent     content                 = null;

    private static CryptoSystem self                    = null;

    // ================================================================================================
    // ========== PRIVATE:
    // ===========================================================================
    // ================================================================================================

    private CryptoSystem()
    {
        self = this;
    };

    // ================================================================================================
    // ========== PUBLIC:
    // ============================================================================
    // ================================================================================================

    public long randSHACycles()
    {
        return SHA_ITERATION_MIN_COUNT + randomizer.nextInt(SHA_ITERATION_MAX_COUNT);
    }

    public static CryptoSystem getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return self;
    }

    // todo: rename method
    public String encryptPassword(SpecialPassword sp)
    {
        try
        {
            return rsa.encrypt(Utilities.objectToBytes(sp.getMap()));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        return "Error";
    }

    // todo: rename method
    public HashMap<String, String> decryptPassword(String s)
    {
        try
        {
            return (HashMap<String, String>) Utilities.bytesToObject(rsa.decrypt(s));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        return null;
    }

    public static void init(String masterPassword, boolean isNewUser) throws Exceptions
    {
        Logger.printDebug("CryptoSystem init STARTS...");

        if (self != null)
        {
            Logger.printError("CryptoSystem already initialized... exiting...");
            Terminator.terminate(new Exceptions(XC.INSTANCE_ALREADY_EXISTS));
        }

        // ========== SHA initialization START:
        // ==========================================================
        Logger.printDebug("SHA init STARTS...");

        try
        {
            sha = new SHA();
        }
        catch (Exceptions e)
        {
            Logger.printError("SHA initialization failed... exiting...");
            Terminator.terminate(e);
        }

        Logger.printDebug("SHA init DONE!");
        // ========== SHA initialization END:
        // =============================================================

        masterHash = sha.getBytesSHA512(masterPassword.getBytes());

        keyHashP = sha.getBytesSHA512((masterPassword + "P").getBytes());
        keyHashQ = sha.getBytesSHA512((masterPassword + "Q").getBytes());
        keyHashE = sha.getBytesSHA512((masterPassword + "E").getBytes());

        // ========== RSA initialization START:
        // ===========================================================
        Logger.printDebug("RSA init STARTS...");
        try
        {

            rsa =
                    new RSA(Utilities.bytesToHex(keyHashP), Utilities.bytesToHex(keyHashQ),
                            Utilities.bytesToHex(keyHashE));
        }
        catch (main.Exceptions e)
        {
            Logger.printError("RSA initialization failed... exiting...");
            Terminator.terminate(e);
        }
        Logger.printDebug("RSA init DONE!");
        // ========== RSA initialization END:
        // =============================================================

        // ========== FILE I/O initialization START:
        // ======================================================
        Logger.printDebug("File I/O init STARTS...");
        try
        {
            UserFileIO.init(
                    sha.getStringSHA512((Arrays.toString(masterHash) + "FILENAME").getBytes()),
                    isNewUser);
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
                Terminator.terminate(e);
            }
        }
        Logger.printDebug("File I/O init DONE...");
        // ========== FILE I/O initialization END:
        // ========================================================

        randomizer = new Random(System.currentTimeMillis());

        self = new CryptoSystem();

        Logger.printDebug("CryptoSystem init DONE!");
    }

    public String getPassword(long cycles, String pwdName)
    {
        return getPassword(cycles, pwdName, null);
    }

    public String getPassword(long cycles, String pwdName, Task<Void> passwordCalculation)
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
            if (passwordCalculation != null && passwordCalculation.isCancelled())
            {
                Logger.printDebug("calculation interrupted");
                return "";
            }
            tmp = sha.getBytesSHA512(tmp);
        }

        return Utilities.bytesToHex(tmp);
    }

    public String getHash(String toHash, String salt)
    {
        return sha.getStringSHA512((toHash + salt).getBytes());
    }
}
