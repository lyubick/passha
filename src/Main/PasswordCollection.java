/**
 *
 */
package Main;

import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Common.RC;
import Common.RC.RCODES;
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

    private static Vector<Long>            shaCycles = readSHACycles();

    private static Vector<Long> readSHACycles()
    {
        Vector<Long> retV = new Vector<Long>();

        for (SpecialPassword existing : db)
            retV.add(new Long(existing.getShaCycles()));

        return retV;
    }

    private static boolean isUnique(Long ex)
    {
        return (shaCycles.lastIndexOf(ex) != -1) ? false : true;
    }

    public static RCODES addPassword(SpecialPassword sp)
    {
        for (SpecialPassword existing : db)
            if (existing.getName().equals(sp.getName()))
                return RC.check(RCODES.RC_NOK);

        long sc = 0;
        while (!isUnique(new Long(sc = CryptoSystem.randSHACycles())));
        sp.setShaCycles(sc);

        db.addElement(sp);

        return RC.check(RCODES.RC_OK);
    }

    public static ObservableList<iSpecialPassword> getIface()
    {
        ObservableList<iSpecialPassword> pSet = FXCollections.observableArrayList();

        for (SpecialPassword sp : db)
        {
            pSet.add(new iSpecialPassword(sp));
        }

        return pSet;
    }

    public static void dump()
    {
        Logger.printDebug("Dumping PasswordCollection... START");


        Logger.printDebug("Collection: ");
        for (SpecialPassword sp : db)
            sp.dump();

        Logger.printDebug("Dumping PasswordCollection... DONE!");
    }
}
