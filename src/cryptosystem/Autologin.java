package cryptosystem;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Collectors;

import core.Vault;
import javafx.beans.property.SimpleBooleanProperty;
import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
import main.Terminator;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;

public class Autologin
{
    static private Autologin      self         = null;

    private final String          SALT_P       = "P";
    private final String          SALT_Q       = "Q";
    private final String          SALT_E       = "E";

    private final String          RECORD_START = "START";
    private final String          RECORD_END   = "END";

    private boolean               enabled      = false;
    private RSA                   rsa          = null;

    private SimpleBooleanProperty ON           = null;

    private final String          REG_PATH     = "HKCU\\Software\\pasSHA\\Vault";

    public static Autologin getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    static public void init() throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        self = new Autologin();
    }

    private Autologin()
    {
        try
        {
            rsa = new RSA(SHA.getHashString((executeCommand("wmic baseboard get serialnumber") + SALT_P).getBytes()),
                SHA.getHashString((executeCommand("wmic diskdrive get serialnumber") + SALT_Q).getBytes()),
                SHA.getHashString(SALT_E.getBytes()));
            enabled = true;

            ON = new SimpleBooleanProperty(false);

            executeCommand("reg add " + REG_PATH + " /f");
        }
        catch (Exceptions e)
        {
            Logger.printError("Autologin init error: " + e.getCode().toString());
            enabled = false;
        }
    }

    public void setAutologinON(Vault vault)
    {
        if (vault == null || enabled == false) return;

        try
        {
            addToRegistry(vault.getMasterHash(rsa));
        }
        catch (Exceptions e)
        {
            Logger
                .printError("Failed to setup auto login for vault: " + vault.getName() + "; " + e.getCode().toString());
        }
        ON.set(true);
    }

    public void setAutologinOFF(byte[] hash)
    {
        if (hash == null || enabled == false) return;

        try
        {
            for (String cipher : readFromRegistry())
                if (Arrays.equals(hash, rsa.decrypt(cipher))) deleteFromRegistry(cipher);
        }
        catch (Exceptions e)
        {
            Logger.printError("Failed to delete auto login with hash: " + Utilities.bytesToHex(hash));
        }
    }

    public void setAutologinOFF(Vault vault)
    {
        if (vault == null || enabled == false) return;

        try
        {
            for (String cipher : readFromRegistry())
                if (vault.initializedFrom(rsa.decrypt(cipher))) deleteFromRegistry(cipher);
        }
        catch (Exceptions e)
        {
            Logger.printError(
                "Failed to delete auto login for vault: " + vault.getName() + "; " + e.getCode().toString());
        }
        ON.set(false);
    }

    public void check(Vault vault)
    {
        if (vault == null || enabled == false) return;

        try
        {
            ON.set(true);
            for (String cipher : readFromRegistry())
                if (vault.initializedFrom(rsa.decrypt(cipher))) return;

            ON.set(false);
        }
        catch (Exceptions e)
        {
            Logger.printError(
                "Failed to delete auto login for vault: " + vault.getName() + "; " + e.getCode().toString());
        }
    }

    public Vector<byte[]> getVaults()
    {
        try
        {
            return readFromRegistry().stream().map(val -> rsa.decrypt(val))
                .collect(Collectors.toCollection(() -> new Vector<byte[]>()));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        return null;
    }

    public SimpleBooleanProperty onProperty()
    {
        return ON;
    }

    private String executeCommand(String command) throws Exceptions
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

            for (int i = 0; i < Utilities.DEFAULT_BUFFER_SIZE; i++)
                if ((Character.isLetterOrDigit((char) b[i]))) output.append((char) b[i]);

            Logger.printDebug("output: '" + output.toString() + "'");

        }
        catch (IOException | InterruptedException e)
        {
            Logger.printError("Failed runtime exec: " + e.getMessage());
            throw new Exceptions(XC.UNABLE_TO_EDIT_REGISTRY);
        }

        return output.toString();
    }

    private void addToRegistry(String data) throws Exceptions
    {
        try
        {
            executeCommand("reg add " + REG_PATH + " /v " + RECORD_START + data + RECORD_END + " /f");
        }
        catch (Exceptions e)
        {
            throw new Exceptions(XC.UNABLE_TO_ADD_ENTRY);
        }
    }

    private void deleteFromRegistry(String data) throws Exceptions
    {
        try
        {
            executeCommand("reg delete " + REG_PATH + " /v " + RECORD_START + data + RECORD_END + " /f");
        }
        catch (Exceptions e)
        {
            throw new Exceptions(XC.UNABLE_TO_DELETE_ENTRY);
        }
    }

    private Vector<String> readFromRegistry() throws Exceptions
    {
        try
        {
            Vector<String> output = new Vector<String>();

            for (String str : executeCommand("reg query " + REG_PATH).split(RECORD_END))
            {
                Logger.printDebug("Parsed string from registry: " + str);
                final int recordStartIndex = str.indexOf(RECORD_START);
                if (recordStartIndex == -1) continue;
                output.add(str.substring(recordStartIndex + RECORD_START.length()));
            }

            return output;
        }
        catch (Exceptions e)
        {
            throw new Exceptions(XC.UNABLE_TO_RETRIEVE_ENTRY);
        }
    }
}
