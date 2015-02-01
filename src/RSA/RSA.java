/**
 *
 */
package RSA;


import java.math.BigInteger;

import Common.ReturnCodes;
import Logger.Logger;
import Logger.LOGLEVELS;

/**
 * @author lyubick
 *
 */
public final class RSA {
    private BigInteger p, q, n, f, e, d;

    private String authorization;

    public String getAuthorizationStatus()
    {
        return this.authorization;
    }

    public String encrypt(String msg)
    {
        String cipher  = "";
        String ASCII   = "";
        String fCipher = "";

        for (int i = 0; i < msg.length(); ++i)
        {
            ASCII = Integer.toString(msg.codePointAt(i));
            fCipher = new BigInteger(ASCII).modPow(e, n).toString();

            while (fCipher.length() < 10)
                fCipher = "0" + fCipher;

            cipher += fCipher.toString();
        }

        return cipher;
    }

    public String decrypt(String msg) {
        String decipher = "";
        String ASCII    = "";

        for (int i = 0; i < msg.length(); i += 10)
        {
            ASCII = new BigInteger(msg.substring(i, i + 10)).modPow(d, n).toString();
            decipher += Character.toString((char)Integer.parseInt(ASCII));
        }

        return decipher;
    }

    private ReturnCodes test()
    {
        String alphabet = "qwertyuiopasdfghjklzxcvbnm1234567890";
        String cipher = "";

        cipher = encrypt(alphabet);
        Logger.print(LOGLEVELS.DEBUG, cipher);

        cipher = decrypt(cipher);
        Logger.print(LOGLEVELS.DEBUG, cipher);

        if (cipher.equals(alphabet))
            return ReturnCodes.RC_OK;
        else
            return ReturnCodes.RC_NOK;
    }

    private ReturnCodes init() {

        while (!p.isProbablePrime(100))
            p = p.add(BigInteger.ONE);

        Logger.print(LOGLEVELS.DEBUG, p.toString());

        while (!q.isProbablePrime(100))
            q = q.add(BigInteger.ONE);

        Logger.print(LOGLEVELS.DEBUG, q.toString());

        while (!e.isProbablePrime(100))
            e = e.add(BigInteger.ONE);

        Logger.print(LOGLEVELS.DEBUG, e.toString());

        n = p.multiply(q);

        Logger.print(LOGLEVELS.DEBUG, n.toString());

        f = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        Logger.print(LOGLEVELS.DEBUG, f.toString());

        while (f.gcd(e).intValue() > 1)
            while (!e.isProbablePrime(1))
                e = e.add(BigInteger.ONE);

        d = e.modInverse(f);

        Logger.print(LOGLEVELS.DEBUG, d.toString());

        return test();
    }

    public RSA(String p, String q, String e) {
        this.p = new BigInteger(p);
        this.q = new BigInteger(q);
        this.n = new BigInteger("0");
        this.f = new BigInteger("0");
        this.e = new BigInteger(e);
        this.d = new BigInteger("0");

        if (ReturnCodes.RC_NOK == init())
        {
            authorization = "FAIL";
        }
        else
        {
            authorization = "PASS";
        }
    }
}
