package db;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Vector;
import java.util.stream.Collectors;

import core.Vault;
import main.Exceptions;
import main.Exceptions.XC;
import main.Properties;
import rsa.RSA;
import utilities.Utilities;
import db.SpecialPassword;
import javafx.concurrent.Task;
import logger.Logger;

public class Database
{
    // Keys compared only by SpecialPassword::getName();
    private TreeMap<SpecialPassword, String> db          = null;

    private File                             vaultFile   = null;

    private RSA                              rsa         = null;

    private Vault                            parentVault = null;

    public enum Status
    {
        SYNCHRONIZED,
        DESYNCHRONIZED,
        SYNCHRONIZING,
        SYNCHRONIZATION_FAILED
    };

    // FIXME Check this upon closing
    private volatile Status status = null;

    // TODO: basically what we want is separate Thread for DB,
    // to allow DB independently decide when to dump to file.
    // TODO: on closing database MUST reassure that everything is synced.
    public void requestSync()
    {
        sync();
    }

    public Database(RSA myRSA, String filename, boolean newUser, Vault vault) throws Exceptions
    {
        parentVault = vault;
        // FIXME Redundant? Or move it to Vault of the Vaults :)
        File vaultDir = new File(Properties.PATHS.VAULT);

        if (!vaultDir.exists() && vaultDir.mkdirs() != true)
        {
            Logger.printDebug("Failed to create/access " + vaultDir.getAbsolutePath());
            throw new Exceptions(XC.DIR_DOES_NOT_EXIST);
        }

        vaultFile = new File(Properties.PATHS.VAULT + filename + Properties.EXTENSIONS.VAULT);

        Logger.printDebug(vaultFile.getAbsolutePath());

        if (!vaultFile.exists())
        {
            if (newUser)
            {
                try
                {
                    vaultFile.createNewFile();
                }
                catch (IOException e)
                {
                    Logger.printError("Failed to create file: " + vaultFile.getAbsolutePath());
                    throw new Exceptions(XC.FILE_CREATE_ERROR);
                }
            }
            else
            {
                Logger.printDebug("Failed to open file: " + vaultFile.getAbsolutePath());
                throw new Exceptions(XC.FILE_DOES_NOT_EXISTS);
            }
        }

        // RSA initialized and created by Vault, DB has only pointer
        rsa = myRSA;

        db = new TreeMap<SpecialPassword, String>(new Comparator<SpecialPassword>()
        {
            @Override
            public int compare(SpecialPassword o1, SpecialPassword o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (String entry : Utilities.readStringsFromFile(vaultFile.getAbsolutePath()))
        {
            HashMap<String, String> decryptedEntry =
                (HashMap<String, String>) Utilities.bytesToObject(rsa.decrypt(entry));

            if (decryptedEntry.containsKey("vaultName"))
            {
                parentVault.setName(decryptedEntry.get("vaultName"));
                continue;
            }

            SpecialPassword pwd = new SpecialPassword(decryptedEntry, vault);
            db.put(pwd, rsa.encrypt(Utilities.objectToBytes(pwd.getMap())));
        }
    }

    /*
     * Method evaluates if password fits in database.
     * It shall be called whenever new entry is added to database.
     * Current rules:
     * 1) password name must be unique
     * 2) password shortcut must be empty or unique
     * NOTE: Order of checks matters for replaceEntry();
     */
    private void validatePassword(SpecialPassword entry) throws Exceptions
    {
        if (!entry.getShortcut().isEmpty())
        {
            Optional<SpecialPassword> optSp =
                db.keySet().stream().filter(sp -> sp.getShortcut().equals(entry.getShortcut())).findFirst();
            if (optSp.isPresent())
                throw new Exceptions(XC.PASSWORD_SHORTCUT_ALREADY_IN_USE).setText(optSp.get().getName());
            // TODO: some smarter way to pass info through exceptions (maybe pass copy of whole SpecialPassword?)
        }

        if (db.containsKey(entry)) throw new Exceptions(XC.PASSWORD_NAME_ALREADY_EXISTS);
    }

    public void addEntry(SpecialPassword entry) throws Exceptions
    {
        validatePassword(entry);

        db.put(entry, rsa.encrypt(Utilities.objectToBytes(entry.getMap())));

        status = Status.DESYNCHRONIZED;
        sync();
    }

    public void deleteEntry(SpecialPassword entry) throws Exceptions
    {
        if (!db.containsKey(entry)) throw new Exceptions(XC.NO_PASSWORD_FOUND);
        db.remove(entry);

        status = Status.DESYNCHRONIZED;
        sync();
    }

    public void replaceEntry(SpecialPassword newEntry, SpecialPassword oldEntry) throws Exceptions
    {
        // All checks should be before anything else to avoid loosing oldEntry
        // (either replace is completed fully, or not at all)

        if (!db.containsKey(oldEntry)) throw new Exceptions(XC.NO_PASSWORD_FOUND);

        try
        {
            validatePassword(newEntry);
        }
        catch (Exceptions e)
        {
            // Password may have existing name, if it will substitute password with this name
            if (e.getCode() == XC.PASSWORD_NAME_ALREADY_EXISTS && oldEntry.getName().equals(newEntry.getName()))
                Logger.printDebug("Replacing same SpecialPassword");        // Not an error
            // Password may have conflicting shortcut with itself
            else if (e.getCode() == XC.PASSWORD_SHORTCUT_ALREADY_IN_USE && e.getText().equals(newEntry.getName()))
                Logger.printDebug("Didn't change shortcut for password");   // Not an error
            else
                throw e;        // Error
        }
        db.remove(oldEntry);
        db.put(newEntry, rsa.encrypt(Utilities.objectToBytes(newEntry.getMap())));

        status = Status.DESYNCHRONIZED;
        sync();
    }

    public Vector<SpecialPassword> getDecrypted()
    {
        return db.keySet().stream().collect(Collectors.toCollection(() -> new Vector<SpecialPassword>()));
    }

    private void sync()
    {
        Logger.printDebug("Saving Database to '" + vaultFile.getAbsolutePath() + "'...");

        Task<Void> task = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                try
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("vaultName", parentVault.getName());

                    Utilities.writeToFile(vaultFile.getAbsolutePath(),
                        db.values().stream().collect(Collectors.toCollection(() -> new Vector<String>())),
                        rsa.encrypt(Utilities.objectToBytes(map)));

                    status = Status.SYNCHRONIZED;
                    this.succeeded();
                }
                catch (Exceptions e)
                {
                    status = Status.SYNCHRONIZATION_FAILED;
                    this.failed();
                }
                return null;
            }
        };

        task.setOnSucceeded(EventHandler ->
        {
            Logger.printDebug("Saving Database to '" + vaultFile.getAbsolutePath() + "' DONE!");
            Logger.printDebug("Database Synchronization returned status: " + status.toString());
        });

        Thread thread = new Thread(task);
        thread.start();
    }
}
