/**
 *
 */
package CryptoSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javafx.concurrent.Task;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import SHA.SHA;
import RSA.RSA;
import Common.Exceptions;
import Common.FileIO;
import Common.Exceptions.XC;
import Common.Utilities;
import Logger.Logger;
import Main.ABEND;
import Main.SpecialPassword;

/**
 * @author lyubick
 *
 */
public final class CryptoSystem
{
    private static final int    RSA_NUMBER_HASH_OFFSET     = 8;

    private static final int    RSA_NUMBER_HASH_MODIFIER_P = 1;
    private static final int    RSA_NUMBER_HASH_MODIFIER_Q = 4;
    private static final int    RSA_NUMBER_HASH_MODIFIER_E = 7;

    private static final int    SHA_ITERATION_MIN_COUNT    = 100;
    private static final int    SHA_ITERATION_MAX_COUNT    = 1000000 - SHA_ITERATION_MIN_COUNT;

    private static Random       randomizer                 = null;

    private static SHA          sha                        = null;
    private static RSA          rsa                        = null;

    private static byte[]       masterHash                 = null;
    private static byte[]       keyHash                    = null;

    static Clipboard            clipboard                  = null;
    static ClipboardContent     content                    = null;

    private static CryptoSystem self                       = null;

    // ================================================================================================
    // ========== PRIVATE:
    // ===========================================================================
    // ================================================================================================

    private CryptoSystem()
    {
        self = this;
    };

    private static String getKey(int mod)
    {
        return Long.toString(Math.abs(Utilities.load64(keyHash, RSA_NUMBER_HASH_OFFSET * mod)));
    }

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
        if (self == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);
        return self;
    }

    public String encryptPassword(SpecialPassword sp)
    {
        try
        {
            return rsa.encrypt(Utilities.objectToBytes(sp));
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }

        return "Error";
    }

    public SpecialPassword decryptPassword(String s)
    {
        try
        {
            return (SpecialPassword) Utilities.bytesToObject(rsa.decryptBytes(s));
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }

        return null;
    }

    public static void init(String masterPassword, boolean isNewUser) throws Exceptions
    {
        Logger.printDebug("CryptoSystem init STARTS...");

        if (self != null)
        {
            Logger.printError("CryptoSystem already initialized... exiting...");
            ABEND.terminate(new Exceptions(XC.SECURITY_BREACH));
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
            ABEND.terminate(e);
        }

        Logger.printDebug("SHA init DONE!");
        // ========== SHA initialization END:
        // =============================================================

        masterHash = sha.getBytesSHA512(masterPassword.getBytes());
        keyHash = sha.getBytesSHA512(masterHash);

        // ========== RSA initialization START:
        // ===========================================================
        Logger.printDebug("RSA init STARTS...");
        try
        {
            rsa =
                    new RSA(getKey(RSA_NUMBER_HASH_MODIFIER_P), getKey(RSA_NUMBER_HASH_MODIFIER_Q),
                            getKey(RSA_NUMBER_HASH_MODIFIER_E));
        }
        catch (Common.Exceptions e)
        {
            Logger.printError("RSA initialization failed... exiting...");
            ABEND.terminate(e);
        }
        Logger.printDebug("RSA init DONE!");
        // ========== RSA initialization END:
        // =============================================================

        // ========== FILE I/O initialization START:
        // ======================================================
        Logger.printDebug("File I/O init STARTS...");
        try
        {
            Logger.printDebug(sha.getStringSHA512((Arrays.toString(masterHash) + "FILENAME")
                    .getBytes()));
            FileIO.init(sha.getStringSHA512((Arrays.toString(masterHash) + "FILENAME").getBytes()),
                    isNewUser);
        }
        catch (Exceptions e)
        {
            if (e.getCode() == XC.FILE_DOES_NOT_EXISTS)
            {
                Logger.printInfo("No User file found. New user?");
                throw new Exceptions(XC.UNKNOWN_USER);
            }
            else
            {
                ABEND.terminate(e);
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
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Logger.printDebug("master hash is " + SHA.bytesToHex(tmp) + "cycles " + cycles);

        while (cycles-- > 0)
        {
            if (passwordCalculation != null && passwordCalculation.isCancelled())
            {
                Logger.printDebug("calculation interrupted");
                return "";
            }
            tmp = sha.getBytesSHA512(tmp);
        }

        return SHA.bytesToHex(tmp);
    }
}
