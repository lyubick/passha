/**
 *
 */
package db;

import java.util.HashMap;
import java.util.Vector;

import utilities.Utilities;
import cryptosystem.CryptoSystem;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author lyubick
 *
 */
public class PasswordCollection
{
    private Vector<SpecialPassword>   db               = new Vector<SpecialPassword>();
    private static PasswordCollection self             = null;
    private SpecialPassword           selectedPassword = null;

    // ==========

    private PasswordCollection()
    {
        self = this;
    }

    public static PasswordCollection init() throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);
        self = new PasswordCollection();
        return self;
    }

    public static PasswordCollection getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        return self;
    }

    // ==========

    public SpecialPassword getSelected()
    {
        return selectedPassword;
    }

    public void setSelected(SpecialPassword pwd)
    {
        selectedPassword = pwd;
    }

    // ==========

    public void addPassword(SpecialPassword sp) throws Exceptions
    {
        for (SpecialPassword existing : db)
            if (existing.getName().equals(sp.getName())) throw new Exceptions(XC.PASSWORD_NAME_ALREADY_EXISTS);

        UserFileIO.getInstance().add(CryptoSystem.getInstance().rsaEncrypt(Utilities.objectToBytes(sp.getMap())));
        db.addElement(sp);
    }

    public void removePassword(SpecialPassword sp) throws Exceptions
    {
        UserFileIO.getInstance().delete(CryptoSystem.getInstance().rsaEncrypt(Utilities.objectToBytes(sp.getMap())));
        db.remove(sp);
    }

    public void replacePasword(SpecialPassword sp) throws Exceptions
    {
        String oldEntry = CryptoSystem.getInstance().rsaEncrypt(Utilities.objectToBytes(selectedPassword.getMap()));
        selectedPassword.setShaCycles(sp.getShaCycles());
        String newEntry = CryptoSystem.getInstance().rsaEncrypt(Utilities.objectToBytes(selectedPassword.getMap()));

        UserFileIO.getInstance().replace(newEntry, oldEntry);
    }

    // ==========
    public ObservableList<iSpecialPassword> getIface()
    {
        ObservableList<iSpecialPassword> pSet = FXCollections.observableArrayList();

        for (SpecialPassword sp : db)
        {
            pSet.add(new iSpecialPassword(sp));
        }

        return pSet;
    }

    public void load()
    {
        Vector<String> encryptedDB;
        try
        {
            encryptedDB = UserFileIO.getInstance().read();
            for (String entry : encryptedDB)
                db.addElement(new SpecialPassword((HashMap<String, String>) Utilities.bytesToObject(CryptoSystem
                        .getInstance().rsaDecrypt(entry))));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

    }

    public void export(String fileName)
    {
        final String TAB = "\t";
        final String FILE_START_TAG = "<file_start>";
        final String FILE_END_TAG = "</file_end>";
        final String RECORD_START_TAG = TAB + "<record_start>";
        final String RECORD_END_TAG = TAB + "</record_end>";
        final String HASH_CODE_START_TAG = "<hash>";
        final String HASH_CODE_END_TAG = "</hash>";

        Vector<String> exportStrings = new Vector<String>();

        exportStrings.add(FILE_START_TAG);
        for (SpecialPassword sp : db)
        {
            exportStrings.add(RECORD_START_TAG);
            exportStrings.add(TAB + TAB + "name=" + sp.getName());
            exportStrings.add(TAB + TAB + "URL=" + sp.getUrl());
            exportStrings.add(TAB + TAB + "comment=" + sp.getComment());
            exportStrings.add(TAB + TAB + "SHA cycles=" + sp.getShaCycles());
            exportStrings.add(TAB + TAB + "password=" + sp.getPassword());
            exportStrings.add(RECORD_END_TAG);
        }
        exportStrings.add(FILE_END_TAG);

        int hash = exportStrings.hashCode();

        exportStrings.add(HASH_CODE_START_TAG);
        exportStrings.add(TAB + hash);
        exportStrings.add(HASH_CODE_END_TAG);

        try
        {
            Utilities.writeToFile(fileName, exportStrings);
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
