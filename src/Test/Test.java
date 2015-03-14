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
import Common.Return;
import Common.Return.RC;
import Common.Utilities;
import CryptoSystem.CryptoSystem;

/**
 * @author lyubick
 *
 */
public class Test
{
    private static int            TestNr     = 0;
    private static Vector<RC> TestStatus = new Vector<RC>();
    private static int            TestOK     = 0;

    public static void launchTest(RC result)
    {
        ++TestNr;

        if (result.equals(RC.OK))
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
    public static RC TestRSA()
    {
        try
        {
            RSA rsa = new RSA("12345", "54321", "6789");
            rsa = new RSA("97531", "13579", "5463");
            rsa = new RSA("15156", "6855", "232232");
        }
        catch (Exceptions e)
        {
            return RC.NOK;
        }

        return RC.OK;
    }

    public static RC TestSHA()
    {
        // SHA() now throws if self-test fails
        try
        {
            SHA test = new SHA();
        }
        catch (Exceptions e)
        {
            return RC.NOK;
        }

        return RC.OK;
    }

    public static RC TestRC()
    {
        Return.check(RC.RC_SECURITY_BREACH);
        Return.check(RC.NOK);

        return Return.check(RC.OK);
    }

    public static RC TestSerialization()
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
            return RC.NOK;
        }

        try
        {
            sp1 = (SpecialPassword) Utilities.bytesToObject(Utilities.objectToBytes(sp));
        }
        catch (Exceptions e)
        {
            return RC.NOK;
        }

        if (sp.equals(sp1) != true) { return RC.NOK; }

        try
        {
            Logger.printDebug(rsa.encrypt(Utilities.objectToBytes(sp)));
            sp2 =
                    (SpecialPassword) Utilities.bytesToObject(rsa.decryptBytes(rsa
                            .encrypt(Utilities.objectToBytes(sp))));
        }
        catch (Exceptions e)
        {
            return RC.NOK;
        }

        if (sp.equals(sp2) != true) { return RC.NOK; }

        return RC.OK;
    }

    public static RC testFileIO()
    {
        String fileName = "bin/Test.txt";
        final int strCount = 5;
        Vector<String> initialStrings = new Vector<String>();
        Vector<String> resultStrings = new Vector<String>();
        int i = 0;

        initialStrings.add("This");
        initialStrings.add("is");
        initialStrings.add("sparta");
        initialStrings.add("you");
        initialStrings.add("motherfucker");

        try
        {
            FileIO ftest = FileIO.init(fileName);
            resultStrings = ftest.readTextFile();

            if (strCount == resultStrings.size())
            {
                for (int j = 0; j < strCount; ++j)
                {
                    if (!resultStrings.elementAt(j).equals(initialStrings.elementAt(j)))
                    {
                        Logger.printError("strings does not match");
                        return Return.check(RC.NOK);
                    }
                }
            }
            else
            {
                return Return.check(RC.NOK);
            }
        }
        catch (Exceptions e)
        {
            return Return.check(RC.NOK);
        }

        return RC.OK;
    }

     static void launchTestSuite(){} // TODO

    public static void main(String[] args)
    {
        String[] parms = Main.Main.readArgs(args);

        Logger.loggerON(parms[0].toString());

        /* 0. */launchTestSuite();

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
                    + (TestStatus.elementAt(i).ordinal() == RC.OK.ordinal() ? "PASSED"
                            : "FAILED"));

        System.out.println("OVERALL: " + (float) (TestOK / TestNr * 100) + "%");
        System.out.println("STATUS: " + (TestOK == TestNr ? "OK" : "NOK"));

        Logger.loggerOFF();
    }
}
