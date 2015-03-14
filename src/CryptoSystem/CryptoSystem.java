/**
 *
 */
package CryptoSystem;

import java.util.Random;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import SHA.SHA;
import RSA.RSA;
import Common.Exceptions;
import Common.Return;
import Common.Return.RC;
import Common.Utilities;
import Logger.Logger;

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

    private static Random    randomizer                 = new Random(System.currentTimeMillis());

    private static SHA       sha                        = null;
    private static RSA       rsa                        = null;

    private static byte[]    masterHash                 = null;
    private static byte[]    keyHash                    = null;

    static Clipboard         clipboard                  = null;
    static ClipboardContent  content                    = null;

    private static boolean   isInitialized              = false;

    private CryptoSystem()
    {
    }; // Not used...

    public static long randSHACycles()
    {
        return SHA_ITERATION_MIN_COUNT + randomizer.nextInt(SHA_ITERATION_MAX_COUNT);
    }

    private static String getKey(int mod)
    {
        return Long.toString(Math.abs(Utilities.load64(keyHash, RSA_NUMBER_HASH_OFFSET * mod)));
    }

    public static RC initCryptoSystem(String masterPassword)
    {
        if (isInitialized)
        {
            Logger.printError("CryptoSystem already initialized... potential Security Breach... exiting...");
            System.exit(RC.RC_SECURITY_BREACH.ordinal()); // Fatal
                                                              // error...
        }

        try
        {
            sha = new SHA();
        }
        catch (Exceptions e1)
        {
            Logger.printError("SHA initialization failed... exiting...");
            System.exit(RC.RC_SECURITY_FAILURE.ordinal()); // Fatal
                                                               // error...
        }

        // wouldn't it be better to name it setMasterHash(); and make assignment
        // inside?
        masterHash = sha.getBytesSHA512(masterPassword.getBytes());
        keyHash = sha.getBytesSHA512(masterHash);

        try
        {
            rsa =
                    new RSA(getKey(RSA_NUMBER_HASH_MODIFIER_P), getKey(RSA_NUMBER_HASH_MODIFIER_Q),
                            getKey(RSA_NUMBER_HASH_MODIFIER_E));
        }
        catch (Common.Exceptions e)
        {
            Logger.printError("RSA initialization failed... exiting...");
            System.exit(RC.RC_SECURITY_FAILURE.ordinal()); // Fatal
                                                               // error...
        }

        // clipboard = Clipboard.getSystemClipboard();
        // content = new ClipboardContent();

        isInitialized = true;

        return Return.check(RC.OK);
    }

    // private void putTextToClipboard(String pwd)
    // {
    // content.clear();
    // content.putString(pwd);
    // clipboard.setContent(content);
    // }
}
