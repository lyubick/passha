/**
 *
 */
package Main;

import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Common.Exceptions;
import Common.FileIO;
import Common.Return;
import Common.Exceptions.XC;
import Common.Return.RC;
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
    private static PasswordCollection self           = null;
    private static Vector<Long>            shaCycles = readSHACycles();
    
    private static Vector<Long> readSHACycles()
    {
        Vector<Long> retV = new Vector<Long>();

        for (SpecialPassword existing : db)
            retV.add(new Long(existing.getShaCycles()));

        return retV;
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

    private static boolean isUnique(Long ex)
    {
        return (shaCycles.lastIndexOf(ex) != -1) ? false : true;
    }

    public RC addPassword(SpecialPassword sp)
    {
        CryptoSystem cs = null;

        try
        {
            cs = CryptoSystem.getInstance();
        }
        catch (Exceptions e)
        {
            System.exit(500); // TODO abend
        }

        for (SpecialPassword existing : db)
            if (existing.getName().equals(sp.getName())) return Return.check(RC.NOK);

        long sc = 0;
        while (!isUnique(new Long(sc = cs.randSHACycles())));
        sp.setShaCycles(sc);

        db.addElement(sp);

        return Return.check(RC.OK);
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

    public RC save()
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
            System.exit(500); // TODO abend
        }

        return Return.check(RC.OK);
    }

    private static RC load()
    {
        CryptoSystem cs = null;
        Vector<String> cryptSP = new Vector<String>();
        FileIO reader = null;

        try
        {
            cs = CryptoSystem.getInstance();
            reader = FileIO.getInstance();
            cryptSP = reader.readFromFile();
        }
        catch (Exceptions e)
        {
            System.exit(500); // TODO abend; parse exception
        }

        for (int i = 0; i < cryptSP.size(); ++i)
        {
            db.addElement(cs.decryptPassword(cryptSP.elementAt(i)));
        }
        return Return.check(RC.OK);
    }

    public RC removePassword(SpecialPassword sp)
    {
        db.remove(sp);
        return Return.check(RC.OK);
    }
}
