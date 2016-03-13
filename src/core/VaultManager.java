package core;

import java.util.Vector;

import db.SpecialPassword;
import main.Exceptions;
import main.Exceptions.XC;
import main.Properties;

public class VaultManager
{
    static private VaultManager self        = null;

    private Vector<Vault>       vaults      = null;

    private Vault               activeVault = null;

    private VaultManager()
    {
        vaults = new Vector<Vault>();
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
        Vault newVault = new Vault(password, isNewUser);
        vaults.addElement(newVault);

        return newVault;
    }

    // TODO: Maybe do the same for all other methods?
    // like addPassword, replacePassword, removePassword???
    static public SpecialPassword getSelectedPassword() throws Exceptions
    {
        return VaultManager.getInstance().getActiveVault().getSelected();
    }

    public void removeVault()
    {
        vaults.remove(activeVault);
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
