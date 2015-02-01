/**
 *
 */
package Test;

import Logger.Logger;
import Logger.LOGLEVELS;
import RSA.RSA;

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
