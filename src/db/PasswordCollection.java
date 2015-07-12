package db;

import java.util.Vector;

import utilities.Utilities;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * NB! All logic with Database must be implemented here and nowhere else!
 *
 * All checks for existence, naming etc. MUST be located in this Class. Password
 * collection is the only Class that is working with UI. All request must be
 * going through PasswordColeection.
 *
 * Physical File (on Disk) : contains encrypted Collection of Passwords.
 * Database (in memory) : contains both encrypted/decrypted Collection of
 * Passwords. UserFileIO : responsible for synchronization between memory and
 * physical file.
 *
 * @author lyubick
 *
 */
public class PasswordCollection
{
    // Database IS and MUST be linked with UserFileIO, so use references with
    // caution. Basically it could be Singleton (TODO), but it would make too
    // much Singletons in code. So lets try to keep it that way.
    Database                          db               = null; /* UserFileIO */
    private static PasswordCollection self             = null;
    private SpecialPassword           selectedPassword = null;

    // ==========

    private PasswordCollection() throws Exceptions
    {
        db = UserFileIO.getInstance().getDatabase();
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

    public void addPassword(SpecialPassword entry) throws Exceptions
    {
        for (SpecialPassword existing : db.getDecrypted())
            if (existing.getName().equals(entry.getName()))
                throw new Exceptions(XC.PASSWORD_NAME_ALREADY_EXISTS);

        db.addEntry(entry);

        UserFileIO.getInstance().sync(); // Save to file
    }

    public void removePassword(SpecialPassword entry) throws Exceptions
    {
        if (entry == null) entry = selectedPassword;
        db.deleteEntry(entry);

        UserFileIO.getInstance().sync(); // Save to file
    }

    public void replacePasword(SpecialPassword newEntry) throws Exceptions
    {
        db.replaceEntry(newEntry, selectedPassword);

        UserFileIO.getInstance().sync(); // Save to file
    }

    // ==========
    public ObservableList<iSpecialPassword> getIface()
    {
        ObservableList<iSpecialPassword> pSet = FXCollections.observableArrayList();

        for (SpecialPassword sp : db.getDecrypted())
        {
            pSet.add(new iSpecialPassword(sp));
        }

        return pSet;
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
        for (SpecialPassword sp : db.getDecrypted())
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
            Terminator.terminate(e); // FIXME
        }
    }

    public SpecialPassword getPasswordByShortcut(String shortcut)
    {
        for (SpecialPassword sp : db.getDecrypted())
            if (sp.getShortcut().equals(shortcut)) return sp;
        return null;
    }

    public Vector<SpecialPassword> getPasswordsWithShortcut()
    {
        Vector<SpecialPassword> pwds = new Vector<SpecialPassword>();

        for (SpecialPassword sp : db.getDecrypted())
            if (!sp.getShortcut().equals("")) pwds.add(sp);

        return pwds;
    }
}
