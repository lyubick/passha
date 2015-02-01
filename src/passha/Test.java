/**
 *
 */
package passha;

import passha.RSA;
import passha.ReturnCodes;

import Logger.Logger;
import Logger.LOGLEVELS;

/**
 * @author lyubick
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Logger.loggerON(LOGLEVELS.DEBUG);

        RSA rsa = new RSA("12345", "54321", "6789");

        System.out.println(rsa.getAuthorizationStatus());

        Logger.loggerOFF();
    }

}
