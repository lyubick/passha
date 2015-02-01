/**
 *
 */
package Test;

import Logger.Logger;
import Logger.LOGLEVELS;

import RSA.RSA;

import Common.ReturnCodes;

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
            System.out.println(TestNr + ": FAILED!");
        }
        else
        {
            System.out.println(TestNr + ": PASSED!");
        }
    }

/* Test set */
    public static ReturnCodes TestRSA()
    {
        RSA rsa = new RSA("12345", "54321", "6789");

        if (!rsa.getAuthorizationStatus().equals("PASS))"))
                return ReturnCodes.RC_NOK;

        rsa = new RSA("97531", "13579", "5463");

        if (!rsa.getAuthorizationStatus().equals("PASS))"))
                return ReturnCodes.RC_NOK;

        rsa = new RSA("15156", "6855", "232232");

        if (!rsa.getAuthorizationStatus().equals("PASS))"))
                return ReturnCodes.RC_NOK;

        return ReturnCodes.RC_OK;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Logger.loggerON(LOGLEVELS.SILENT);

        /*1. */ launchTest(TestRSA());

        Logger.loggerOFF();
    }

}
