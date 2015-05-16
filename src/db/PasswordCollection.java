/**
 *
 */
package db;

import java.util.Vector;

import utilities.Utilities;
import cryptosystem.CryptoSystem;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

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

    public void replacePasword(SpecialPassword newSp)
    {
        selectedPassword.setShaCycles(newSp.getShaCycles());
        changed = true;
    }

    public void addPassword(SpecialPassword sp) throws Exceptions
    {
        for (SpecialPassword existing : db)
            if (existing.getName().equals(sp.getName()))
                throw new Exceptions(XC.PASSWORD_NAME_ALREADY_EXISTS);

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

    public Task<Void> save()
    {
        Task<Void> saveTask = new Task<Void>()
        {

            @Override
            protected Void call() throws Exception
            {
                updateProgress(0, 1);

                CryptoSystem cs = null;
                Vector<String> cryptSP = new Vector<String>();
                UserFileIO writer = null;

                try
                {
                    cs = CryptoSystem.getInstance();
                    writer = UserFileIO.getInstance();

                    int i = 0;
                    for (SpecialPassword sp : db)
                    {
                        cryptSP.add(cs.encryptPassword(sp));
                        updateProgress(++i, db.size());
                    }

                    writer.writeToUserFile(cryptSP);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                changed = false;
                return null;
            }
        };

        return saveTask;
    }

    private Task<Void> load()
    {
        Task<Void> loadTask = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                updateProgress(1, 100);
                CryptoSystem cs = null;
                Vector<String> cryptSP = new Vector<String>();
                UserFileIO reader = null;

                try
                {
                    db.clear();
                    cs = CryptoSystem.getInstance();
                    reader = UserFileIO.getInstance();
                    cryptSP = reader.readFromUserFile();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

                for (int i = 0; i < cryptSP.size(); ++i)
                {
                    db.addElement(new SpecialPassword(cs.decryptPassword(cryptSP.elementAt(i))));
                    updateProgress(i + 1, cryptSP.size());
                }

                return null;
            }
        };

        return loadTask;
    }

    public Task<Void> reload()
    {
        changed = false;
        return load();
    }

    public void removePassword(SpecialPassword sp)
    {
        db.remove(sp);
        changed = true;
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
