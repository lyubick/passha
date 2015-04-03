/**
 *
 */
package Main;

import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Common.Exceptions;
import Common.FileIO;
import Common.Exceptions.XC;
import CryptoSystem.CryptoSystem;
import Logger.Logger;
import Main.iSpecialPassword;

/**
 * @author lyubick
 *
 */
public class PasswordCollection
{
    private Vector<SpecialPassword>   db               = new Vector<SpecialPassword>();
    private static PasswordCollection self             = null;
    private boolean                   changed          = false;
    private SpecialPassword           selectedPassword = null;

    public SpecialPassword getSelected()
    {
        return selectedPassword;
    }

    public void setSelected(SpecialPassword pwd)
    {
        selectedPassword = pwd;
    }

    public boolean isChanged()
    {
        return changed;
    }

    private PasswordCollection()
    {
        self = this;
        load();
    }

    public static PasswordCollection init() throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        self = new PasswordCollection();
        return self;
    }

    public static PasswordCollection getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);
        return self;
    }

    public void replacePasword(SpecialPassword newSp)
    {
        selectedPassword.setShaCycles(newSp.getShaCycles());
        changed = true;
    }

    public void addPassword(SpecialPassword sp) throws Exceptions
    {
        for (SpecialPassword existing : db)
            if (existing.getName().equals(sp.getName()))
                throw new Exceptions(XC.PASSWORD_ALREADY_EXISTS);

        db.addElement(sp);

        changed = true;
    }

    public ObservableList<iSpecialPassword> getIface()
    {
        ObservableList<iSpecialPassword> pSet = FXCollections.observableArrayList();

        for (SpecialPassword sp : db)
        {
            pSet.add(new iSpecialPassword(sp));
        }

        return pSet;
    }

    public void dump()
    {
        Logger.printDebug("Dumping PasswordCollection... START");

        Logger.printDebug("Collection: ");
        for (SpecialPassword sp : db)
            sp.dump();

        Logger.printDebug("Dumping PasswordCollection... DONE!");
    }

    public void save()
    {
        CryptoSystem cs = null;
        Vector<String> cryptSP = new Vector<String>();
        FileIO writer = null;

        try
        {
            cs = CryptoSystem.getInstance();
            writer = FileIO.getInstance();

            for (SpecialPassword sp : db)
            {
                cryptSP.add(cs.encryptPassword(sp));
            }

            writer.writeToFile(cryptSP);
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }

        changed = false;
    }

    private void load()
    {
        CryptoSystem cs = null;
        Vector<String> cryptSP = new Vector<String>();
        FileIO reader = null;

        try
        {
            db.clear();
            cs = CryptoSystem.getInstance();
            reader = FileIO.getInstance();
            cryptSP = reader.readFromFile();
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }

        for (int i = 0; i < cryptSP.size(); ++i)
        {
            SpecialPassword tmp = cs.decryptPassword(cryptSP.elementAt(i));
            db.addElement(tmp);
        }
    }

    public void reload()
    {
        load();
        changed = false;
    }

    public void removePassword(SpecialPassword sp)
    {
        db.remove(sp);
        changed = true;
    }
}
