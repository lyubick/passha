package core;

import java.util.Vector;

import compatibility.UserFileMigration;
import cryptosystem.Autologin;
import db.Database.Status;
import db.SpecialPassword;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
import sha.SHA;
import main.Properties;

public class VaultManager
{
    static private VaultManager         self                = null;

    private Vector<Vault>               vaults              = null;

    private SimpleObjectProperty<Vault> activeVaultProperty = null;

    private VaultManager()
    {
        vaults = new Vector<>();
        self = this;
        activeVaultProperty = new SimpleObjectProperty<Vault>(null);
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
        if (activeVaultProperty.get() != null)
        {
            vaults.remove(activeVaultProperty.get());
            deactivateVault();
        }
        else
            Logger.printError("Attempt to close null-vault...");
    }

    public void activateVault(Vault vault)
    {
        activeVaultProperty.set(vault);
    }

    public Vault getActiveVault()
    {
        return activeVaultProperty.get();
    }

    public ObjectProperty<Vault> getActiveVaultProperty()
    {
        return activeVaultProperty;
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

        if (activeVaultProperty.get() == null)
            activateVault(vaults.firstElement());
        else
        {
            Logger.printDebug("Index of active vault: " + vaults.indexOf(activeVaultProperty.get()) + "; Vault name: "
                + activeVaultProperty.get().getName());
            activateVault(vaults.get((vaults.indexOf(activeVaultProperty.get()) + 1) % vaults.size()));
        }

        return activeVaultProperty.get();
    }

    public void autologin() throws Exceptions
    {
        for (byte[] hash : Autologin.getInstance().getVaults())
        {
            try
            {
                addVault(hash, false);
            }
            catch (Exceptions e)
            {
                if (e.getCode() != XC.FILE_DOES_NOT_EXIST && e.getCode() != XC.DIR_DOES_NOT_EXIST) throw e;

                Autologin.getInstance().setAutologinOFF(hash);
            }
        }
    }

    public boolean isReadyToExit()
    {
        for (Vault vault : vaults)
            if (vault.getDBStatusProperty().getValue() != Status.SYNCHRONIZED) return false;

        return true;
    }
}
