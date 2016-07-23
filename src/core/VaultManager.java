package core;

import java.util.Vector;

import compatibility.UserFileMigration;
import cryptosystem.Autologin;
import db.SpecialPassword;
import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
import sha.SHA;
import main.Properties;

public class VaultManager
{
    static private VaultManager self        = null;

    private Vector<Vault>       vaults      = null;

    private Vault               activeVault = null;

    private VaultManager()
    {
        vaults = new Vector<>();
        self = this;
    }

    static public void init()
    {
        new VaultManager();
    }

    static public VaultManager getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    public Vault addVault(String password, boolean isNewUser) throws Exceptions
    {
        try
        {
            return addVault(SHA.getHashBytes(password.getBytes()), isNewUser);
        }
        catch (Exceptions e)
        {
            if (e.getCode() != XC.FILE_DOES_NOT_EXIST) throw e;

            try
            {
                return UserFileMigration.tryMigrate(password);
            }
            catch (Exceptions ignore)
            {
                throw e;
            }
        }
    }

    public Vault addVault(byte[] hash, boolean isNewUser) throws Exceptions
    {
        for (Vault vault : vaults)
            if (vault.initializedFrom(hash)) throw new Exceptions(XC.VAULT_OPENED);

        Vault newVault = new Vault(hash, isNewUser);
        vaults.addElement(newVault);
        return newVault;
    }

    static public SpecialPassword getSelectedPassword() throws Exceptions
    {
        return VaultManager.getInstance().getActiveVault().getSelected();
    }

    public void removeVault()
    {
        if (activeVault != null)
            vaults.remove(activeVault);
        else
            Logger.printError("Attempt to close null-vault...");
    }

    public void activateVault(Vault vault)
    {
        activeVault = vault;
    }

    public Vault getActiveVault()
    {
        return activeVault;
    }

    public void deactivateVault()
    {
        activateVault(null);
    }

    public boolean isFull()
    {
        return !(vaults.size() < Properties.CORE.VAULT.MAX_COUNT);
    }

    public int size()
    {
        return vaults.size();
    }

    public Vault activateNextVault() throws Exceptions
    {
        if (vaults.isEmpty()) throw new Exceptions(XC.VAULTS_NOT_FOUND);

        if (activeVault == null)
            activateVault(vaults.firstElement());
        else
        {
            Logger.printDebug(
                "Index of active vault: " + vaults.indexOf(activeVault) + "; Vault name: " + activeVault.getName());
            activateVault(vaults.get((vaults.indexOf(activeVault) + 1) % vaults.size()));
        }

        return activeVault;
    }

    public void autologin() throws Exceptions
    {
        // FIXME: Crash if file with autologin entry is deleted
        for (byte[] hash : Autologin.getInstance().getVaults())
            addVault(hash, false);
    }
}
