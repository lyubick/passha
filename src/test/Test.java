/**
 *
 */
package test;

// TODO: write test text (name or/and description) in addition to its index number and result
// explicit variable initialization missing
// functions and file missing headers

import java.util.BitSet;
import java.util.HashMap;
import java.util.Vector;

import cryptosystem.CryptoSystem;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;
import db.SpecialPassword;
import db.SpecialPassword.PARAMS_MASK_BITS;

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
            new RSA(
                    "929872838cb9cfe6578e11f0a323438aee5ae7f61d41412d62db72b25dac52019de2d6a355eb2d033336fb70e73f0ec0afeca3ef36dd8a90d83f998fee23b78d",
                    "2e96772232487fb3a058d58f2c310023e07e4017c94d56cc5fae4b54b44605f42a75b0b1f358991f8c6cbe9b68b64e5b2a09d0ad23fcac07ee9a9198a745e1d5",
                    "87c568e037a5fa50b1bc911e8ee19a77c4dd3c22bce9932f86fdd8a216afe1681c89737fada6859e91047eece711ec16da62d6ccb9fd0de2c51f132347350d8c");
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
        new SHA(); // FIXME

        return true;
    }

    public static boolean testHashMapSerialization()
    {
        SpecialPassword sp = null;
        try
        {
            BitSet paramsMask = new BitSet(PARAMS_MASK_BITS.TOTAL_COUNT.ordinal());
            paramsMask.set(0, PARAMS_MASK_BITS.TOTAL_COUNT.ordinal());

            sp = new SpecialPassword("name", "comment", "url", 16, paramsMask, "@!=-%#");
        }
        catch (Exceptions e1)
        {
            return false;
        }
        SpecialPassword sp1 = null;
        SpecialPassword sp2 = null;

        RSA rsa;
        try
        {
            rsa =
                    new RSA(
                            "5a919be1e0a88963bcf0e6ff0210e804f61e87934ee39d03691fadad8488b222e0577fc4e26e147d4f16afbe5bd83dd08703c6ee7a3e24ebdccf0cd00b92742e",
                            "8dabbada384a200469cb1ef9b8e3758a5a6375ee69073d0a722055f3c9e16b8d670afebfde2d8798f7440a94202daace92cd4068937ad6cd53dbe4d4fd9de4b2",
                            "a79d096bef8bb8fbff8f26dc5dadfb7696b6ee487108c0e11a397e22d35262760e3b9f109f9b49c1439716aacd63b7427715d0eecb1023b0ca0a4b3249733e6c");
        }
        catch (Exceptions e)
        {
            return false;
        }

        try
        {
            sp1 =
                    new SpecialPassword((HashMap<String, String>) Utilities.bytesToObject(Utilities.objectToBytes(sp
                            .getMap())));
        }
        catch (Exceptions e)
        {
            return false;
        }

        if (sp.equals(sp1) != true) return false;

        try
        {
            sp2 =
                    new SpecialPassword((HashMap<String, String>) Utilities.bytesToObject(rsa.decrypt(rsa
                            .encrypt(Utilities.objectToBytes(sp.getMap())))));
        }
        catch (Exceptions e)
        {
            return false;
        }

        return sp.equals(sp2);
    }

    public static void main(String[] args)
    {
        try
        {
            Logger.loggerON();
            CryptoSystem.init("qwerty123", true);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
        /* 1. */launchTest(TestRSA());
        /* 2. */launchTest(TestSHA());
        /* 3. */launchTest(testHashMapSerialization());

        /* Summary */
        System.out.println("\n-= SUMMARY =-");
        for (int i = 0; i < TestStatus.size(); ++i)
            System.out.println("" + (i + 1) + ": " + (TestStatus.elementAt(i) ? "PASSED" : "FAILED"));

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
