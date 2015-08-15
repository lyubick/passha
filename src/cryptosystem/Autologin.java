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

    public Autologin() throws Exceptions
    {
        sha = new SHA();
        rsa = new RSA(sha.getHashString((getPhysicalAddress() + getUserName() + SALT_P).getBytes()),
                sha.getHashString((getPhysicalAddress() + getUserName() + SALT_Q).getBytes()),
                sha.getHashString((getPhysicalAddress() + getUserName() + SALT_E).getBytes()));
    }

    // REGISTRY ROUTINES
    private void addToRegistry(String data) throws Exceptions
    {
        try
        {
            Process proc = Runtime.getRuntime()
                    .exec("reg add " + REG_PATH + " /f /v AUTOLOGIN /t REG_SZ /d " + data);

            try
            {
                proc.waitFor();
            }
            catch (InterruptedException e)
            {
                // ignore
            }

            if (proc.exitValue() != 0)
            {
                Logger.printError("RC: " + proc.exitValue());
                throw new Exceptions(XC.UNABLE_TO_ADD_ENTRY);
            }
            else
                Logger.printDebug("Entry added successfully. RC: " + proc.exitValue());

        }
        catch (IOException e)
        {
            throw new Exceptions(XC.UNABLE_TO_ADD_ENTRY);
        }
    }

    private void deleteFromRegistry() throws Exceptions
    {
        try
        {
            Process proc = Runtime.getRuntime().exec("reg delete " + REG_PATH + " /f");

            try
            {
                proc.waitFor();
            }
            catch (InterruptedException e)
            {
                // ignore
            }

            if (proc.exitValue() != 0)
            {
                Logger.printError("RC: " + proc.exitValue());
                throw new Exceptions(XC.UNABLE_TO_DELETE_ENTRY);
            }
            else
                Logger.printDebug("Entry deleted successfully. RC: " + proc.exitValue());

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
            Process proc = Runtime.getRuntime().exec("reg query " + REG_PATH);

            try
            {
                proc.waitFor();
            }
            catch (InterruptedException e)
            {
                // ignore
            }

            if (proc.exitValue() != 0)
            {
                Logger.printError("RC: " + proc.exitValue());
                throw new Exceptions(XC.UNABLE_TO_ADD_ENTRY);
            }
            else
                Logger.printDebug("Entry read successfully. RC: " + proc.exitValue());

            byte[] b = new byte[4096];

            proc.getInputStream().read(b);

            StringBuilder sb = new StringBuilder();

            for (int i = 64; i < 4096 && b[i] >= '0' && b[i] <= '9'; i++)
                sb.append((char) b[i]);

            Logger.printDebug(sb.toString());

            return sb.toString();
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
        addToRegistry(rsa.encrypt(CryptoSystem.getInstance().getMasterPass()));
    }

    public void setAutologinOFF() throws Exceptions
    {
        deleteFromRegistry();
    }

    public String getMasterPass() throws Exceptions
    {
        return rsa.decryptToString(readFromRegistry());
    }

}
