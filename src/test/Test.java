/**
 *
 */
package test;

// TODO: write test text (name or/and description) in addition to its index number and result
// explicit variable initialization missing
// functions and file missing headers

import java.util.BitSet;
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
import db.SpecialPassword.ParamsMaskBits;

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
                    "8e20e64bb23a55cd05c8dd7debcfde261128e391cfd5a9535a565ed8e6f227dab3d1ec71d51a4e6e43afdf928d19b8db1a1f2d544d1e179e12cde72efcf7a877",
                    "49a65a3ea7ff9739a760e1174cd26828b5c10d285aaf8fff74268fdd7f0ec2123bbbde2cbf1fe113f898513a699ce378dbd3e9a21a8376131e92cf7a10e58e16",
                    "b525923d7caddea2cd556c63525e1980245a80f04d14aae8afabc8808e1911558163e3d856383f86eba9e6e23ab9cbd25536e66a496e970a79b28ae606494317");
            new RSA(
                    "c4da6096ae5996da3007b27203abf934a5348636fe0b578dbf82a54be2ecd78b776f2bb21a41e95c91203bdbce4b6ed1e36bd7d953c81844abc3d990e5d13088",
                    "5b7f1ffc3e18ec8a98bb27ea852d5031277e199e7e015d2c179aae20a966985c6f5dbfef5586c638566107e0f64d982d30253d7620faf11d3c974efcc1d9c228",
                    "e55bef40160ad7e899578d7912379211210c26bbf90f5e6151696fc53dd71c9622d21c3ebcf3e532ad5ff55e23cca92998d42ad2bf3f15cdbe00b4861503c99b");
            new RSA(
                    "3c9909afec25354d551dae21590bb26e38d53f2173b8d3dc3eee4c047e7ab1c1eb8b85103e3be7ba613b31bb5c9c36214dc9f14a42fd7a2fdb84856bca5c44c2",
                    "15c669e4b7a7ab65f55341f44884f590c7c1f61025fb090c62f5366c3e4644358ead6622394bf88669fc6b4e60a29a60f65700fcc8f8b33d1f654b20461ba776",
                    "8d8bfcbcc4d4f18dd108203a6c184659aebaffa1f05bede310d0c99f2b0c3c97dafd7d76c8a11fced21a78eeec3ed7d428394d139bf94cd64ac583756ef15f04");
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
        SpecialPassword sp = null;
        try
        {
            BitSet paramsMask = new BitSet(ParamsMaskBits.TOTAL_COUNT.ordinal());
            paramsMask.set(0, ParamsMaskBits.TOTAL_COUNT.ordinal());

            sp = new SpecialPassword("name", "comment", "url", 16, paramsMask, "@!=-%#");
        }
        catch (Exceptions e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
            sp1 = (SpecialPassword) Utilities.bytesToObject(Utilities.objectToBytes(sp));
        }
        catch (Exceptions e)
        {
            return true;
        }

        if (sp.equals(sp1) != true) return false;

        try
        {
            Logger.printDebug(rsa.encrypt(Utilities.objectToBytes(sp)));
            sp2 =
                    (SpecialPassword) Utilities.bytesToObject(rsa.decrypt(rsa.encrypt(Utilities
                            .objectToBytes(sp))));
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
