/**
 *
 */
package Test;

// todo: write test text (name or/and description) in addition to its index number and result
// explicit variable initialization missing
// functions and file missing headers

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import Logger.Logger;
import Logger.LOGLEVELS;
import RSA.RSA;
import SHA.SHA;
import UI.SpecialPasswordForm;
import Common.Exceptions;
import Common.RC;
import Common.ReturnCodes;
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
       // why compare with PASS and negate the result, if can compare to FAIL?
       // actually we don.t need any getAutorizationStatus() as constructor throws in case of error
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

    /**
     * @param args
     */
    public static void main(String[] args)
    {
       //maybe receive log level from args as well??
        Logger.loggerON(LOGLEVELS.DEBUG);

        /*1. */ launchTest(TestRSA());
        /*2. */ launchTest(TestSHA());
        /*3. */ launchTest(CryptoSystem.initCryptoSystem("qwerty123"));
        /*4. */ launchTest(TestRC());


        Logger.loggerOFF();

        // launches GUI.
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        SpecialPasswordForm.drawUIForm(primaryStage);
    }

}
