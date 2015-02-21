/**
 *
 */
package Test;

// todo: write test text (name or/and description) in addition to its index number and result
// explicit variable initialization missing
// functions and file missing headers

import javafx.application.Application;
import javafx.stage.Stage;
import Logger.Logger;
import Logger.LOGLEVELS;
import Main.SpecialPassword;
import RSA.RSA;
import SHA.SHA;
import UI.LoginForm;
import UI.SpecialPasswordForm;
import Common.Exceptions;
import Common.RC;
import Common.ReturnCodes;
import Common.Utilities;
import CryptoSystem.CryptoSystem;



/**
 * @author lyubick
 *
 */
public class Test extends Application
{
    private static int TestNr = 0;

    public static void launchTest(ReturnCodes result)
    {
        ++TestNr; // srsly? increment before use??? // it kind of breaking logic.

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
            rsa = new RSA("97531", "13579", "5463");
            rsa = new RSA("15156", "6855", "232232");
        }
        catch (Exceptions e)
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

        String expectedSha =
                "8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909";

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
        }
        catch (Exceptions e1)
        {
            return ReturnCodes.RC_NOK;
        }

        try
        {
            sp1 = (SpecialPassword) Utilities.bytesToObject(Utilities.objectToBytes(sp));
        }
        catch (Exceptions e)
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
            sp2 = (SpecialPassword)Utilities.bytesToObject(rsa.decryptBytes(rsa.encrypt(Utilities.objectToBytes(sp))));
        }
        catch (Exceptions e)
        {
            return ReturnCodes.RC_NOK;
        }

        if (sp.equals(sp2) != true)
        {
            return ReturnCodes.RC_NOK;
        }

        return ReturnCodes.RC_OK;
    }








    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            /* TODO for curious, u need it then think of format :) */
            Logger.loggerON(LOGLEVELS.SILENT);
        }
        else
        {
            Logger.loggerON(LOGLEVELS.DEBUG);
        }



        /*1. */ launchTest(TestRSA());
        /*2. */ launchTest(TestSHA());
        /*3. */ launchTest(CryptoSystem.initCryptoSystem("qwerty123"));
        /*4. */ launchTest(TestRC());
        /*5. */ launchTest(TestSerialization());



        // launches GUI.
        launch(args);


        Logger.loggerOFF();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        SpecialPasswordForm.drawUIForm(primaryStage);
        LoginForm.draw(primaryStage);
    }

}
