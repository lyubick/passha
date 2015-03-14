/**
 *
 */
package Test;

// TODO: write test text (name or/and description) in addition to its index number and result
// explicit variable initialization missing
// functions and file missing headers

import java.util.Vector;

import Logger.Logger;
import Main.SpecialPassword;
import RSA.RSA;
import SHA.SHA;
import Common.Exceptions;
import Common.FileIO;
import Common.RC;
import Common.RC.RCODES;
import Common.Utilities;
import CryptoSystem.CryptoSystem;

/**
 * @author lyubick
 *
 */
public class Test
{
    private static int            TestNr     = 0;
    private static Vector<RCODES> TestStatus = new Vector<RCODES>();
    private static int            TestOK     = 0;

    public static void launchTest(RCODES result)
    {
        ++TestNr;

        if (result.equals(RCODES.OK))
        {
            System.out.println(TestNr + ": PASSED!");
            ++TestOK;
        }
        else
        {
            System.out.println(TestNr + ": FAILED!");
        }

        TestStatus.addElement(result);
    }

    /* Test set */
    public static RCODES TestRSA()
    {
        try
        {
            RSA rsa = new RSA("12345", "54321", "6789");
            rsa = new RSA("97531", "13579", "5463");
            rsa = new RSA("15156", "6855", "232232");
        }
        catch (Exceptions e)
        {
            return RCODES.NOK;
        }

        return RCODES.OK;
    }

    public static RCODES TestSHA()
    {
        // SHA() now throws if self-test fails
        try
        {
            SHA test = new SHA();
        }
        catch (Exceptions e)
        {
            return RCODES.NOK;
        }

        return RCODES.OK;
    }

    public static RCODES TestRC()
    {
        RC.check(RCODES.RC_SECURITY_BREACH);
        RC.check(RCODES.NOK);

        return RC.check(RCODES.OK);
    }

    public static RCODES TestSerialization()
    {
        SpecialPassword sp = new SpecialPassword();
        SpecialPassword sp1 = null;
        SpecialPassword sp2 = null;

        RSA rsa;
        try
        {
            rsa = new RSA("12345", "54321", "6789");
        }
        catch (Exceptions e1)
        {
            return RCODES.NOK;
        }

        try
        {
            sp1 = (SpecialPassword) Utilities.bytesToObject(Utilities.objectToBytes(sp));
        }
        catch (Exceptions e)
        {
            return RCODES.NOK;
        }

        if (sp.equals(sp1) != true) { return RCODES.NOK; }

        try
        {
            Logger.printDebug(rsa.encrypt(Utilities.objectToBytes(sp)));
            sp2 =
                    (SpecialPassword) Utilities.bytesToObject(rsa.decryptBytes(rsa
                            .encrypt(Utilities.objectToBytes(sp))));
        }
        catch (Exceptions e)
        {
            return RCODES.NOK;
        }

        if (sp.equals(sp2) != true) { return RCODES.NOK; }

        return RCODES.OK;
    }

    public static RCODES testFileIO()
    {
        String fileName = "bin/Test.txt";
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
                        return RC.check(RCODES.NOK);
                    }
                }
            }
            else
            {
                return RC.check(RCODES.NOK);
            }
        }
        catch (Exceptions e)
        {
            return RC.check(RCODES.NOK);
        }

        return RCODES.OK;
    }

    public static void main(String[] args)
    {
        String[] parms = Main.Main.readArgs(args);

        Logger.loggerON(parms[0].toString());

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
                    + (TestStatus.elementAt(i).ordinal() == RCODES.OK.ordinal() ? "PASSED"
                            : "FAILED"));

        System.out.println("OVERALL: " + (float) (TestOK / TestNr * 100) + "%");
        System.out.println("STATUS: " + (TestOK == TestNr ? "OK" : "NOK"));

        Logger.loggerOFF();
    }
}
