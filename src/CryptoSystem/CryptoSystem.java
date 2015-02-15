/**
 *
 */
package CryptoSystem;

import SHA.SHA;
import RSA.RSA;
import Common.RC;
import Common.ReturnCodes;
import Common.Utilities;
import Logger.Logger;
import Logger.LOGLEVELS;

/**
 * @author lyubick
 *
 */
public final class CryptoSystem
{
    private static final int RSA_NUMBER_HASH_OFFSET = 8;

    private static final int RSA_NUMBER_HASH_MODIFIER_P = 1;
    private static final int RSA_NUMBER_HASH_MODIFIER_Q = 4;
    private static final int RSA_NUMBER_HASH_MODIFIER_E = 7;



    private static SHA sha = null;
    private static RSA rsa = null;

    private static byte[] masterHash = null;
    private static byte[] keyHash    = null;

    private static boolean isInitialized = false;

    private CryptoSystem() {}; // Not used...

    private static String getKey(int mod)
    {
        return Long.toString(Math.abs(Utilities.load64(keyHash, RSA_NUMBER_HASH_OFFSET * mod)));
    }

    public static ReturnCodes initCryptoSystem(String masterPassword)
    {
        if (isInitialized)
        {
            Logger.print(LOGLEVELS.ERROR, "CryptoSystem already initialized... potential Security Breach... exiting...");
            System.exit(ReturnCodes.RC_SECURITY_BREACH.ordinal()); // Fatal error...
        }

        sha = new SHA();

        // wouldn't it be better to name it setMasterHash(); and make assignment inside?
        masterHash = sha.getBytesSHA512(masterPassword.getBytes());
        keyHash    = sha.getBytesSHA512(masterHash);

        try
        {
            rsa = new RSA(getKey(RSA_NUMBER_HASH_MODIFIER_P),
                          getKey(RSA_NUMBER_HASH_MODIFIER_Q),
                          getKey(RSA_NUMBER_HASH_MODIFIER_E));
        }
        catch (Common.Exceptions e)
        {
            Logger.print(LOGLEVELS.ERROR, "RSA initialization failed... exiting...");
            System.exit(ReturnCodes.RC_SECURITY_FAILURE.ordinal()); // Fatal error...
        }

        isInitialized = true;

        return RC.check(ReturnCodes.RC_OK);
    }
}
