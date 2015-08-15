package cryptosystem;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;

public class Autologin
{
    private final String SALT_P = "P";
    private final String SALT_Q = "Q";
    private final String SALT_E = "E";

    private final String REG_PATH = "HKCU\\Software\\pasSHA";

    private RSA rsa = null;
    private SHA sha = null;

    public Autologin()
    {
        sha = new SHA();
    }

    // REGISTRY ROUTINES
    private void addToRegistry(String data) throws Exceptions
    {
        try
        {
            String out = Runtime.getRuntime()
                    .exec("reg add " + REG_PATH + " /v AUTOLOGIN /t REG_SZ /d " + data)
                    .getOutputStream().toString();

            Logger.printDebug(out);
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.UNABLE_TO_ADD_ENTRY);
        }
    }

    private String deleteFromRegistry(String data) throws Exceptions
    {
        try
        {
            String out = Runtime.getRuntime().exec("reg delete " + REG_PATH + " /f")
                    .getOutputStream().toString();

            Logger.printDebug(out);

            return out;
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.UNABLE_TO_ADD_ENTRY);
        }
    }

    private String readFromRegistry() throws Exceptions
    {
        try
        {
            String out =
                    Runtime.getRuntime().exec("reg query " + REG_PATH).getOutputStream().toString();

            Logger.printDebug("Entry: " + out + "<END");

            if (out.contains("ERROR")) throw new Exceptions(XC.ENTRY_NOT_FOUND);

            return out;
        }
        catch (IOException e)
        {
            throw new Exceptions(XC.UNABLE_TO_RETRIEVE_ENTRY);
        }
    }

    private String getPhysicalAddress() throws Exceptions
    {
        try
        {
            return Utilities.bytesToHex(NetworkInterface
                    .getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress());
        }
        catch (SocketException e)
        {
            throw new Exceptions(XC.UNABLE_TO_GET_PHYSICAL_ADDR);
        }
        catch (UnknownHostException e)
        {
            throw new Exceptions(XC.UNABLE_TO_GET_PHYSICAL_ADDR);
        }
    }

    private String getUserName() throws Exceptions
    {
        String username = System.getProperty("user.name");

        if (username.isEmpty()) throw new Exceptions(XC.UNABLE_TO_GET_USERNAME);

        return username;
    }

    public void setAutologinON() throws Exceptions
    {
        rsa = new RSA(sha.getHashString((getPhysicalAddress() + getUserName() + SALT_P).getBytes()),
                sha.getHashString((getPhysicalAddress() + getUserName() + SALT_Q).getBytes()),
                sha.getHashString((getPhysicalAddress() + getUserName() + SALT_E).getBytes()));

        addToRegistry(rsa.encrypt(CryptoSystem.getInstance().getMasterPass()));
    }

    public void setAutologinOFF() throws Exceptions
    {

    }

    public String getMasterPass() throws Exceptions
    {
        return rsa.decryptToString(readFromRegistry());
    }

}
