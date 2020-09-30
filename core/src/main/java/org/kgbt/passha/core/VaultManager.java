package org.kgbt.passha.core;

import java.util.Vector;
import java.util.function.BiConsumer;

import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.compatibility.UserFileMigration;
import org.kgbt.passha.core.db.Vault;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.db.Database.Status;
import org.kgbt.passha.core.db.SpecialPassword;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.sha.SHA;

public class VaultManager
{
    static private VaultManager self = null;

    private Vector<Vault> vaults = null;

    private Vault                    activeVault;
    private BiConsumer<Vault, Vault> onActiveVaultChanged;

    private VaultManager()
    {
        vaults = new Vector<>();
        self = this;
        activeVault = null;
    }

    public void setOnActiveVaultChanged(BiConsumer<Vault, Vault> onActiveVaultChanged)
    {
        this.onActiveVaultChanged = onActiveVaultChanged;
    }

    static public void init()
    {
        new VaultManager();
    }

    static public VaultManager getInstance() throws Exceptions
    {
        if (self == null)
            throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    public Vault addVault(String password, boolean isNewUser, String root) throws Exceptions
    {
        try
        {
            return addVault(SHA.getHashBytes(password.getBytes()), isNewUser, root);
        }
        catch (Exceptions e)
        {
            if (e.getCode() != XC.FILE_DOES_NOT_EXIST)
                throw e;

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

    public Vault addVault(byte[] hash, boolean isNewUser, String root) throws Exceptions
    {
        if (vaults.stream().anyMatch(vault -> vault.initializedFrom(hash)))
            throw new Exceptions(XC.VAULT_OPENED);

        Vault newVault = new Vault(hash, isNewUser, root);
        vaults.addElement(newVault);
        return newVault;
    }

    static public SpecialPassword getSelectedPassword() throws Exceptions
    {
        return VaultManager.getInstance().getActiveVault().getSelected();
    }

    public void removeVault()
    {
        Vault active = activeVault;
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
        if (onActiveVaultChanged != null)
            onActiveVaultChanged.accept(activeVault, vault);
        activeVault = vault;
        return vault;
    }

    public Vault activateVault(String vaultName) throws Throwable
    {
        return activateVault(vaults.stream().filter(vault -> vault.getName().equals(vaultName)).findFirst()
            .orElseThrow(() -> new Exceptions(XC.NO_SUCH_VAULT)));
    }

    public Vault getActiveVault()
    {
        return activeVault;
    }

    public void deactivateVault()
    {
        activateVault((Vault) null);
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
     * @throws Exceptions VAULTS_NOT_FOUND
     */
    public Vault activateNextVault() throws Exceptions
    {
        if (vaults.isEmpty())
            throw new Exceptions(XC.VAULTS_NOT_FOUND);

        Vault active = activeVault;

        if (active == null)
            active = activateVault(vaults.firstElement());
        else
        {
            Logger.printDebug("Index of active vault: " + vaults.indexOf(active) + "; Vault name: " + active.getName());
            activateVault(vaults.get((vaults.indexOf(active) + 1) % vaults.size()));
        }

        return active;
    }

    public boolean isReadyToExit()
    {
        return vaults.stream().allMatch(vault -> vault.getDBStatus() == Status.SYNCHRONIZED);
    }
}
