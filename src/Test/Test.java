/**
 *
 */
package Test;

// todo: write test text (name or/and description) in addition to its index number and result
// explicit variable initialization missing
// functions and file missing headers

import java.util.Vector;

import Logger.Logger;
import Logger.LOGLEVELS;
import Main.SpecialPassword;
import RSA.RSA;
import SHA.SHA;
import Common.Exceptions;
import Common.FileIO;
import Common.RC;
import Common.ReturnCodes;
import Common.Utilities;
import CryptoSystem.CryptoSystem;

/**
 * @author lyubick
 *
 */
public class Test
{
    private static int                 TestNr     = 0;
    private static Vector<ReturnCodes> TestStatus = new Vector<ReturnCodes>();
    private static int                 TestOK     = 0;

    public static void launchTest(ReturnCodes result)
    {
        ++TestNr;

        if (result.equals(ReturnCodes.RC_OK))
        {
            System.out.println(TestNr + ": PASSED!");
            ++TestOK;
        } else
        {
            System.out.println(TestNr + ": FAILED!");
        }

        TestStatus.addElement(result);
    }

    /* Test set */
    public static ReturnCodes TestRSA()
    {
        try
        {
            RSA rsa = new RSA("12345", "54321", "6789");
            rsa = new RSA("97531", "13579", "5463");
            rsa = new RSA("15156", "6855", "232232");
        } catch (Exceptions e)
        {
            return ReturnCodes.RC_NOK;
        }

        return ReturnCodes.RC_OK;
    }

    public static ReturnCodes TestSHA()
    {
        // maybe groupt initializations???
        SHA test = new SHA();
        String testStr = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu";
        byte[] testInput = testStr.getBytes();

        byte[] result = test.getBytesSHA512(testInput);

        String actualSha = SHA.bytesToHex(result);

        String expectedSha = "8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909";

        if (actualSha.compareTo(expectedSha) == 0)
        {
            return ReturnCodes.RC_OK;
        }

        return ReturnCodes.RC_NOK;
    }

    public static ReturnCodes TestRC()
    {
        RC.check(ReturnCodes.RC_SECURITY_BREACH);
        RC.check(ReturnCodes.RC_NOK);

        return RC.check(ReturnCodes.RC_OK);
    }

    public static ReturnCodes TestSerialization()
    {
        SpecialPassword sp = new SpecialPassword();
        SpecialPassword sp1 = null;
        SpecialPassword sp2 = null;

        RSA rsa;
        try
        {
            rsa = new RSA("12345", "54321", "6789");
        } catch (Exceptions e1)
        {
            return ReturnCodes.RC_NOK;
        }

        try
        {
            sp1 = (SpecialPassword) Utilities.bytesToObject(Utilities.objectToBytes(sp));
        } catch (Exceptions e)
        {
            return ReturnCodes.RC_NOK;
        }

        if (sp.equals(sp1) != true)
        {
            return ReturnCodes.RC_NOK;
        }

        try
        {
            Logger.printDebug(rsa.encrypt(Utilities.objectToBytes(sp)));
            sp2 = (SpecialPassword) Utilities.bytesToObject(rsa.decryptBytes(rsa.encrypt(Utilities
                    .objectToBytes(sp))));
        } catch (Exceptions e)
        {
            return ReturnCodes.RC_NOK;
        }

        if (sp.equals(sp2) != true)
        {
            return ReturnCodes.RC_NOK;
        }

        return ReturnCodes.RC_OK;
    }

    public static ReturnCodes testFileIO()
    {
        String fileName = "Test.txt";
        final int strCount = 5;
        String initialStrings[] = new String[strCount];
        String resultStrings[] = null;
        int i = 0;

        initialStrings[i++] = "This";
        initialStrings[i++] = "is";
        initialStrings[i++] = "sparta";
        initialStrings[i++] = "you";
        initialStrings[i++] = "motherfucker";

        try
        {
            FileIO.writeTextFile(initialStrings, fileName);
            resultStrings = FileIO.readTextFile(fileName);

            if (strCount == resultStrings.length)
            {
                for (int j = 0; j < strCount; ++j)
                {
                    if (!resultStrings[j].equals(initialStrings[j]))
                    {
                        Logger.printError("strings does not match");
                        return RC.check(ReturnCodes.RC_NOK);
                    }
                }
            } else
            {
                return RC.check(ReturnCodes.RC_NOK);
            }
        } catch (Exceptions e)
        {
            return RC.check(ReturnCodes.RC_NOK);
        }

        return ReturnCodes.RC_OK;
    }

    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            /* TODO for curious, u need it then think of format :) */
            Logger.loggerON(LOGLEVELS.SILENT);
        } else
        {
            Logger.loggerON(LOGLEVELS.DEBUG);
        }

        /* 1. */launchTest(TestRSA());
        /* 2. */launchTest(TestSHA());
        /* 3. */launchTest(CryptoSystem.initCryptoSystem("qwerty123"));
        /* 4. */launchTest(TestRC());
        /* 5. */launchTest(TestSerialization());
        /* 6. */launchTest(testFileIO());

        /* Summary */
        System.out.println("\n-= SUMMARY =-");
        for (int i = 0; i < TestStatus.size(); ++i)
            System.out.println(""
                    + (i + 1)
                    + ": "
                    + (TestStatus.elementAt(i).ordinal() == ReturnCodes.RC_OK.ordinal() ? "PASSED"
                            : "FAILED"));

        System.out.println("OVERALL: " + (float)(TestOK / TestNr * 100) + "%");
        System.out.println("STATUS: " + (TestOK == TestNr ? "OK":"NOK"));


        Logger.loggerOFF();
    }
}
