package org.kgbt.passha.db;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kgbt.passha.core.Vault;
import org.kgbt.passha.main.Exceptions;
import org.kgbt.passha.main.Exceptions.XC;
import org.kgbt.passha.main.Properties;
import org.kgbt.passha.rsa.RSA;
import org.kgbt.passha.utilities.Utilities;
import org.kgbt.passha.db.SpecialPassword;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import org.kgbt.passha.logger.Logger;
import org.kgbt.passha.main.Terminator;

public class Database
{
    // Keys compared only by SpecialPassword::getName();
    private TreeMap<SpecialPassword, String> db             = null;

    private File                             vaultFile      = null;

    private String                           name           = "";

    private RSA                              rsa            = null;

    private int                              retriesLeft    = Properties.DATABASE.MAX_RETRIES;

    protected final static String            VAULT_NAME_KEY = "vaultName";

    public enum Status
    {
        SYNCHRONIZED,
        DESYNCHRONIZED,
        SYNCHRONIZING,
        SYNCHRONIZATION_FAILED
    }

    private volatile SimpleObjectProperty<Status> status = null;

    public void requestSync()
    {
        status.set(Status.DESYNCHRONIZED);
        retriesLeft = Properties.DATABASE.MAX_RETRIES;
        sync();
    }

    public Database(RSA myRSA, String filename, boolean newUser, Vault vault) throws Exceptions
    {
        status = new SimpleObjectProperty<>(Status.SYNCHRONIZING);

        File vaultDir = new File(Properties.PATHS.VAULT);

        if (!vaultDir.exists() && !vaultDir.mkdirs())
        {
            Logger.printError("Failed to create/access " + vaultDir.getAbsolutePath());
            throw new Exceptions(XC.DIR_DOES_NOT_EXIST);
        }

        vaultFile = new File(Properties.PATHS.VAULT + filename + Properties.EXTENSIONS.VAULT);

        Logger.printDebug("Accessing vault file: " + vaultFile.getAbsolutePath());

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
                Logger.printError("Failed to open file: " + vaultFile.getAbsolutePath());
                throw new Exceptions(XC.FILE_DOES_NOT_EXIST);
            }
        }

        // RSA initialized and created by Vault, DB has only pointer
        rsa = myRSA;

        db = new TreeMap<>(Comparator.comparing(SpecialPassword::getName));

        for (String entry : Utilities.readStringsFromFile(vaultFile.getAbsolutePath()))
        {
            HashMap<String, String> decryptedEntry =
                (HashMap<String, String>) Utilities.bytesToObject(rsa.decrypt(entry));

            if (decryptedEntry.containsKey(VAULT_NAME_KEY))
            {
                name = decryptedEntry.get(VAULT_NAME_KEY);
                continue;
            }

            SpecialPassword pwd = new SpecialPassword(decryptedEntry, vault);
            db.put(pwd, rsa.encrypt(Utilities.objectToBytes(pwd.getMap())));
        }

        status.set(Status.SYNCHRONIZED);
    }

    private void validatePassword(SpecialPassword newEntry) throws Exceptions
    {
        validatePassword(newEntry, null);
    }

    private void validatePassword(SpecialPassword newEntry, SpecialPassword oldEntry) throws Exceptions
    {
        if (db.containsKey(newEntry) && (oldEntry == null || !newEntry.getName().equals(oldEntry.getName())))
            throw new Exceptions(XC.PASSWORD_NAME_EXISTS);

        if (!newEntry.getShortcut().isEmpty())
        {
            Optional<SpecialPassword> optSp =
                db.keySet().stream().filter(sp -> sp.getShortcut().equals(newEntry.getShortcut())).limit(1).findAny();

            if (optSp.isPresent() && (oldEntry == null || !newEntry.getShortcut().equals(oldEntry.getShortcut())))
                throw new Exceptions(XC.PASSWORD_SHORTCUT_IN_USE, optSp.get());
        }
    }

    public void addEntry(SpecialPassword entry) throws Exceptions
    {
        validatePassword(entry);

        db.put(entry, rsa.encrypt(Utilities.objectToBytes(entry.getMap())));

        requestSync();
    }

    public void deleteEntry(SpecialPassword entry) throws Exceptions
    {
        if (!db.containsKey(entry)) throw new Exceptions(XC.PASSWORD_NOT_FOUND);
        db.remove(entry);

        requestSync();
    }

    public void replaceEntry(SpecialPassword newEntry, SpecialPassword oldEntry) throws Exceptions
    {
        // All checks should be before anything else to avoid loosing oldEntry
        // (either replace is completed fully, or not at all)

        if (!db.containsKey(oldEntry)) throw new Exceptions(XC.PASSWORD_NOT_FOUND);

        validatePassword(newEntry, oldEntry);

        db.remove(oldEntry);
        db.put(newEntry, rsa.encrypt(Utilities.objectToBytes(newEntry.getMap())));

        requestSync();
    }

    public Vector<SpecialPassword> getDecrypted()
    {
        return db.keySet().stream().map(sp ->
        {
            try
            {
                return new SpecialPassword(sp);
            }
            catch (Exceptions e)
            {
                if (e.getCode() == XC.NULL) Logger.printError("Database entry is null!");
                Terminator.terminate(e);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toCollection(Vector::new));
    }

    public ObjectProperty<Status> getStatusProperty()
    {
        return status;
    }

    public Status getStatus()
    {
        return status.get();
    }

    private void sync()
    {

        Logger.printDebug("Saving Database to '" + vaultFile.getAbsolutePath() + "'...");

        Task<Void> task = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                status.set(Status.SYNCHRONIZING);
                try
                {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("vaultName", name);

                    Utilities.writeToFile(vaultFile.getAbsolutePath(),
                        db.values().stream().collect(Collectors.toCollection((Supplier<Vector<String>>) Vector::new)),
                        rsa.encrypt(Utilities.objectToBytes(map)));
                }
                catch (Exceptions e)
                {
                    throw new Exception();
                }
                return null;
            }
        };

        task.setOnSucceeded(event ->
        {
            status.set(Status.SYNCHRONIZED);
            Logger.printDebug("Saving Database to '" + vaultFile.getAbsolutePath() + "' DONE!");
            Logger.printDebug("Database Synchronization returned status: " + status.toString());
        });

        task.setOnFailed(event ->
        {
            status.set(Status.SYNCHRONIZATION_FAILED);
            try
            {
                Thread.sleep(Properties.DATABASE.SYNC_RETRY_DELAY_MS);
            }
            catch (Exception e)
            {
                Terminator.terminate(new Exceptions(XC.ERROR));
            }

            if (--retriesLeft > 0) sync();
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (name.equals(this.name)) return;

        this.name = name;
        requestSync();
    }
}
