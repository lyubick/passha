/**
 * test
 */
package Test;

import Logger.Logger;
import Logger.LOGLEVELS;
import RSA.RSA;
import SHA.SHA;
import Common.Exceptions;
import Common.ReturnCodes;
import CryptoSystem.CryptoSystem;

/**
 * @author lyubick
 *
 */
public class Test
{
    private static int TestNr = 0;

    public static void launchTest(ReturnCodes result)
    {
        ++TestNr;

        if (result.equals(ReturnCodes.RC_OK))
        {
            System.out.println(TestNr + ": PASSED!");
        }
        else
        {
            System.out.println(TestNr + ": FAILED!");
        }
    }

/* Test set */
    public static ReturnCodes TestRSA()
    {
        try
        {
            RSA rsa = new RSA("12345", "54321", "6789");

            if (!rsa.getAuthorizationStatus().equals("PASS"))
                    return ReturnCodes.RC_NOK;

            rsa = new RSA("97531", "13579", "5463");

            if (!rsa.getAuthorizationStatus().equals("PASS"))
                    return ReturnCodes.RC_NOK;

            rsa = new RSA("15156", "6855", "232232");

            if (!rsa.getAuthorizationStatus().equals("PASS"))
                    return ReturnCodes.RC_NOK;
        }
        catch (Exceptions e)
        {
            return ReturnCodes.RC_NOK;
        }

        return ReturnCodes.RC_OK;
    }

    public static ReturnCodes TestSHA()
    {
        SHA test = new SHA();
        String testStr = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu";
        byte[] testInput = testStr.getBytes();

        byte[] result = test.getBytesSHA512(testInput);

        String actualSha = SHA.bytesToHex(result);

        String expectedSha =
                "8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909";

        if (actualSha.compareTo(expectedSha) == 0)
        {
            return ReturnCodes.RC_OK;
        }

        return ReturnCodes.RC_NOK;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Logger.loggerON(LOGLEVELS.DEBUG);

        /*1. */ launchTest(TestRSA());
        /*2. */ launchTest(TestSHA());
        /*3. */ launchTest(CryptoSystem.initCryptoSystem("qwerty123"));

        Logger.loggerOFF();
    }

}
