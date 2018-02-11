package org.kgbt.passha.core;

import java.util.Vector;

import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.compatibility.UserFileMigration;
import org.kgbt.passha.core.db.Vault;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.desktop.Autologin;
import org.kgbt.passha.core.db.Database.Status;
import org.kgbt.passha.core.db.SpecialPassword;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.sha.SHA;

public class VaultManager
{
    static private VaultManager         self                = null;

    private Vector<Vault> vaults = null;

    private SimpleObjectProperty<Vault> activeVaultProperty = null;

    private VaultManager()
    {
        vaults = new Vector<>();
        self = this;
        activeVaultProperty = new SimpleObjectProperty<>(null);
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
        if (vaults.stream().anyMatch(vault -> vault.initializedFrom(hash))) throw new Exceptions(XC.VAULT_OPENED);

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
        Vault active = activeVaultProperty.get();
        if (active != null)
        {
            vaults.remove(active);
            deactivateVault();
        }
        else
            Logger.printError("Attempt to close null-vault...");
    }

    public Vault activateVault(Vault vault)
    {
        activeVaultProperty.set(vault);
        return vault;
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

    /**
     * @throws Exceptions
     *             VAULTS_NOT_FOUND
     */
    public Vault activateNextVault() throws Exceptions
    {
        if (vaults.isEmpty()) throw new Exceptions(XC.VAULTS_NOT_FOUND);

        Vault active = activeVaultProperty.get();

        if (active == null)
            active = activateVault(vaults.firstElement());
        else
        {
            Logger.printDebug("Index of active vault: " + vaults.indexOf(active) + "; Vault name: " + active.getName());
            activateVault(vaults.get((vaults.indexOf(active) + 1) % vaults.size()));
        }

        return active;
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
        return vaults.stream().allMatch(vault -> vault.getDBStatus() == Status.SYNCHRONIZED);
    }
}
