/**
 *
 */
package CryptoSystem;

import java.util.Arrays;
import java.util.Random;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import SHA.SHA;
import RSA.RSA;
import Common.Exceptions;
import Common.FileIO;
import Common.Return;
import Common.Exceptions.XC;
import Common.Return.RC;
import Common.Utilities;
import Logger.Logger;
import Main.PasswordCollection;
import Main.SpecialPassword;

/**
 * @author lyubick
 *
 */
public final class CryptoSystem
{
    private static final int RSA_NUMBER_HASH_OFFSET     = 8;

    private static final int RSA_NUMBER_HASH_MODIFIER_P = 1;
    private static final int RSA_NUMBER_HASH_MODIFIER_Q = 4;
    private static final int RSA_NUMBER_HASH_MODIFIER_E = 7;

    private static final int SHA_ITERATION_MIN_COUNT    = 100;
    private static final int SHA_ITERATION_MAX_COUNT    = 1000000 - SHA_ITERATION_MIN_COUNT;

    private static Random    randomizer                 = null;

    private static SHA       sha                        = null;
    private static RSA       rsa                        = null;

    private static byte[]    masterHash                 = null;
    private static byte[]    keyHash                    = null;

    static Clipboard         clipboard                  = null;
    static ClipboardContent  content                    = null;

    private static CryptoSystem self = null;

//================================================================================================
//==========  PRIVATE: ===========================================================================
//================================================================================================

    private CryptoSystem()
    {
        self = this;
    };

    private static String getKey(int mod)
    {
        return Long.toString(Math.abs(Utilities.load64(keyHash, RSA_NUMBER_HASH_OFFSET * mod)));
    }



//================================================================================================
//==========  PUBLIC: ============================================================================
//================================================================================================

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
            // TODO Auto-generated catch block
            e.printStackTrace(); // TODO black magic
        }

        return "Error";
    }

    public SpecialPassword decryptPassword(String s)
    {
        try
        {
            return (SpecialPassword)Utilities.bytesToObject(rsa.decrypt(s).getBytes());
        }
        catch (Exceptions e)
        {
            System.exit(500); // TODO abend
        }

        return null;
    }


    public static RC init(String masterPassword)
    {
        Logger.printDebug("CryptoSystem init STARTS...");

        if (self != null)
        {
            Logger.printError("CryptoSystem already initialized... exiting...");
            System.exit(RC.SECURITY_BREACH.ordinal()); // TODO abend
        }

//==========  SHA initialization START: ==========================================================
        Logger.printDebug("SHA init STARTS...");

        try
        {
            sha = new SHA();
        }
        catch (Exceptions e)
        {
            Logger.printError("SHA initialization failed... exiting...");
            System.exit(RC.SECURITY_FAILURE.ordinal()); // TODO abend
        }

        Logger.printDebug("SHA init DONE!");
//==========  SHA initialization END: =============================================================


        masterHash = sha.getBytesSHA512(masterPassword.getBytes());
        keyHash = sha.getBytesSHA512(masterHash);


//==========  RSA initialization START: ===========================================================
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
            System.exit(RC.SECURITY_FAILURE.ordinal()); // Fatal
                                                               // error...
        }
        Logger.printDebug("RSA init DONE!");
//==========  RSA initialization END: =============================================================


//==========  FILE I/O initialization START: ======================================================
        Logger.printDebug("File I/O init STARTS...");
        try
        {
            Logger.printDebug(sha.getStringSHA512((Arrays.toString(masterHash) + "FILENAME").getBytes()));
            FileIO.init(sha.getStringSHA512((Arrays.toString(masterHash) + "FILENAME").getBytes()));
        }
        catch (Exceptions e)
        {
            Logger.printError("SHA initialization failed... exiting...");
            System.exit(RC.SECURITY_FAILURE.ordinal()); // TODO abend
        }
        Logger.printDebug("File I/O init DONE...");
//==========  FILE I/O initialization END: ========================================================


        randomizer = new Random(System.currentTimeMillis());

        self = new CryptoSystem();

//==========  Database activation START: ==========================================================
        try
        {
            PasswordCollection.init();
        }
        catch (Exceptions e)
        {
            System.exit(RC.SECURITY_FAILURE.ordinal()); // TODO abend
        }
//==========  Database activation END: ============================================================

        Logger.printDebug("CryptoSystem init DONE!");
        return Return.check(RC.OK);
    }

}
