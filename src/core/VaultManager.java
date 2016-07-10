package core;

import java.util.Vector;

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
        byte[] newVaultHash = SHA.getHashBytes(password.getBytes());

        for (Vault vault : vaults)
            if (vault.initializedFrom(newVaultHash)) throw new Exceptions(XC.VAULT_ALREADY_OPEN);

        Vault newVault = new Vault(newVaultHash, isNewUser);

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
}
