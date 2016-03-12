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
    private final int    REGEDIT_READ_OUT_VALUE_OFFSET = 64;

    private final String SALT_P                        = "P";
    private final String SALT_Q                        = "Q";
    private final String SALT_E                        = "E";

    private final String REG_PATH                      = "HKCU\\Software\\pasSHA";

    private RSA          rsa                           = null;
    private SHA          sha                           = null;

    public Autologin() throws Exceptions
    {
        sha = new SHA();
        rsa = new RSA(sha.getHashString((getPhysicalAddress() + getUserName() + SALT_P).getBytes()),
                sha.getHashString((getPhysicalAddress() + getUserName() + SALT_Q).getBytes()),
                sha.getHashString((getPhysicalAddress() + getUserName() + SALT_E).getBytes()));
    }

    private String regedit(String command) throws Exceptions
    {
        StringBuilder output;
        try
        {
            Logger.printDebug("Exec command: " + command);

            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();

            if (proc.exitValue() != 0)
            {
                Logger.printError("RC: " + proc.exitValue());
                throw new Exceptions(XC.UNABLE_TO_EDIT_REGISTRY);
            }
            else
                Logger.printDebug("Entry added successfully. RC: " + proc.exitValue());

            byte[] b = new byte[Utilities.DEFAULT_BUFFER_SIZE];

            proc.getInputStream().read(b);

            output = new StringBuilder();

            for (int i = REGEDIT_READ_OUT_VALUE_OFFSET; i < Utilities.DEFAULT_BUFFER_SIZE
                    && Character.isDigit((char) b[i]); i++)
                output.append((char) b[i]);

            Logger.printDebug("output: " + output.toString());

        }
        catch (IOException | InterruptedException e)
        {
            Logger.printError("Failed runtime exec: " + e.getMessage());
            throw new Exceptions(XC.UNABLE_TO_EDIT_REGISTRY);
        }

        return output.toString();
    }

    // REGISTRY ROUTINES
    private void addToRegistry(String data) throws Exceptions
    {
        try
        {
            regedit("reg add " + REG_PATH + " /f /v AUTOLOGIN /d " + data);
        }
        catch (Exceptions e)
        {
            throw new Exceptions(XC.UNABLE_TO_ADD_ENTRY);
        }

    }

    private void deleteFromRegistry() throws Exceptions
    {
        try
        {
            regedit("reg delete " + REG_PATH + " /f");
        }
        catch (Exceptions e)
        {
            throw new Exceptions(XC.UNABLE_TO_DELETE_ENTRY);
        }
    }

    private String readFromRegistry() throws Exceptions
    {
        try
        {
            return regedit("reg query " + REG_PATH);
        }
        catch (Exceptions e)
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
        // addToRegistry(rsa.encrypt(CryptoSystem.getInstance().getMasterPass()));
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
