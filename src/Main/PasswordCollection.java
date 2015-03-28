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
    private static Vector<SpecialPassword> db        = new Vector<SpecialPassword>();
    private static PasswordCollection      self      = null;
    private static Vector<Long>            shaCycles = readSHACycles();
    private boolean                        changed   = false;

    public boolean isChanged()
    {
        return changed;
    }

    private static Vector<Long> readSHACycles()
    {
        Vector<Long> shaCycles = new Vector<Long>();

        for (SpecialPassword existing : db)
            shaCycles.add(new Long(existing.getShaCycles()));

        return shaCycles;
    }

    private PasswordCollection()
    {
        self = this;
    }

    public static PasswordCollection init() throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        load();

        self = new PasswordCollection();
        return self;
    }

    public static PasswordCollection getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);
        return self;
    }

    private boolean isUnique(Long ex)
    {
        return (shaCycles.lastIndexOf(ex) != -1) ? false : true;
    }

    public void addPassword(SpecialPassword sp) throws Exceptions
    {
        CryptoSystem cs = null;

        try
        {
            cs = CryptoSystem.getInstance();
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }

        long sc = 0;

        for (SpecialPassword existing : db)
            if (existing.getName().equals(sp.getName()))
                throw new Exceptions(XC.PASSWORD_ALREADY_EXISTS);

        while (!isUnique(new Long(sc = cs.randSHACycles())) || !sp.setShaCycles(sc))
            ;

        shaCycles.add(sc);

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

    private static void load()
    {
        CryptoSystem cs = null;
        Vector<String> cryptSP = new Vector<String>();
        FileIO reader = null;

        try
        {
            shaCycles.clear();
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

            shaCycles.add(tmp.getShaCycles());
        }
    }

    public void reload()
    {
        load();
        changed = false;
    }

    public void removePassword(SpecialPassword sp)
    {
        shaCycles.remove(sp.getShaCycles());
        db.remove(sp);
        changed = true;
    }
}
