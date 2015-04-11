/**
 *
 */
package test;

// TODO: write test text (name or/and description) in addition to its index number and result
// explicit variable initialization missing
// functions and file missing headers

import java.util.Vector;

import cryptosystem.CryptoSystem;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;
import db.SpecialPassword;
import db.UserFileIO;

/**
 * @author lyubick
 *
 */
public class Test
{
    private static int             TestNr     = 0;
    private static Vector<Boolean> TestStatus = new Vector<Boolean>();
    private static int             TestOK     = 0;

    public static void launchTest(boolean result)
    {
        ++TestNr;

        if (result)
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
    public static boolean TestRSA()
    {
        try
        {
            new RSA("12345", "54321", "6789");
            new RSA("97531", "13579", "5463");
            new RSA("15156", "6855", "232232");
        }
        catch (Exceptions e)
        {
            return false;
        }

        return true;
    }

    public static boolean TestSHA()
    {
        // SHA() now throws if self-test fails
        try
        {
            new SHA();
        }
        catch (Exceptions e)
        {
            return false;
        }

        return true;
    }

    public static boolean TestSerialization()
    {
        SpecialPassword sp = new SpecialPassword();
        SpecialPassword sp1 = null;
        SpecialPassword sp2 = null;

        RSA rsa;
        try
        {
            rsa = new RSA("12345", "54321", "6789");
        }
        catch (Exceptions e)
        {
            return false;
        }

        try
        {
            sp1 = (SpecialPassword) Utilities.bytesToObject(Utilities.objectToBytes(sp));
        }
        catch (Exceptions e)
        {
            return true;
        }

        if (sp.equals(sp1) != true) { return false; }

        try
        {
            Logger.printDebug(rsa.encrypt(Utilities.objectToBytes(sp)));
            sp2 =
                    (SpecialPassword) Utilities.bytesToObject(rsa.decryptBytes(rsa
                            .encrypt(Utilities.objectToBytes(sp))));
        }
        catch (Exceptions e)
        {
            return false;
        }

        return sp.equals(sp2);
    }

    public static boolean testFileIO()
    {
        final int strCount = 5;
        Vector<String> initialStrings = new Vector<String>();
        Vector<String> resultStrings = new Vector<String>();

        initialStrings.add("This");
        initialStrings.add("is");
        initialStrings.add("sparta");
        initialStrings.add("you");
        initialStrings.add("motherfucker");

        try
        {
            UserFileIO ftest = UserFileIO.getInstance();

            ftest.writeToUserFile(initialStrings);
            resultStrings = ftest.readFromUserFile();

            if (strCount == resultStrings.size())
            {
                for (int j = 0; j < strCount; ++j)
                {
                    if (!resultStrings.elementAt(j).equals(initialStrings.elementAt(j)))
                    {
                        Logger.printError("strings does not match");
                        return false;
                    }
                }
            }
            else
            {
                return false;
            }
        }
        catch (Exceptions e)
        {
            return false;
        }

        return true;
    }

    public static void main(String[] args)
    {
        Logger.loggerON();

        try
        {
            CryptoSystem.init("qwerty123", true);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
        /* 1. */launchTest(TestRSA());
        /* 2. */launchTest(TestSHA());
        /* 3. */launchTest(TestSerialization());
        /* 4. */launchTest(testFileIO());

        /* Summary */
        System.out.println("\n-= SUMMARY =-");
        for (int i = 0; i < TestStatus.size(); ++i)
            System.out.println("" + (i + 1) + ": "
                    + (TestStatus.elementAt(i) ? "PASSED" : "FAILED"));

        System.out.println("OVERALL: " + ((float) TestOK / TestNr * 100) + "%");
        System.out.println("STATUS: " + (TestOK == TestNr ? "OK" : "NOK"));

        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
