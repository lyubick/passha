package org.kgbt.passha.core.db;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.rsa.RSA;
import org.kgbt.passha.core.common.Utilities;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Terminator;

public class Database
{
    // Keys compared only by SpecialPassword::getName();
    private TreeMap<SpecialPassword, String> db = null;

    private File vaultFile = null;

    private String name = "";

    private RSA rsa = null;

    private int retriesLeft = Properties.DATABASE.MAX_RETRIES;

    protected final static String VAULT_NAME_KEY = "vaultName";

    private Consumer<Status> onStatusChanged = null;

    public enum Status
    {
        SYNCHRONIZED,
        DESYNCHRONIZED,
        SYNCHRONIZING,
        SYNCHRONIZATION_FAILED
    }

    private volatile Status status = null;

    public void requestSync()
    {
        updateStatus(Status.DESYNCHRONIZED);
        retriesLeft = Properties.DATABASE.MAX_RETRIES;
        sync();
    }

    public void setOnStatusChanged(Consumer<Status> consumer)
    {
        onStatusChanged = consumer;
    }

    public Database(RSA myRSA, String filename, boolean newUser, String root, Vault vault) throws Exceptions
    {
        updateStatus(Status.SYNCHRONIZING);

        File vaultDir = new File(root + "/" + Properties.PATHS.VAULT);

        if (!vaultDir.exists() && !vaultDir.mkdirs())
        {
            Logger.printError("Failed to create/access " + vaultDir.getAbsolutePath());



            throw new Exceptions(XC.DIR_DOES_NOT_EXIST);
        }

        vaultFile = new File(root + "/" + Properties.PATHS.VAULT + filename + Properties.EXTENSIONS.VAULT);

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
                Utilities.bytesToObject(rsa.decrypt(entry));

            if (decryptedEntry.containsKey(VAULT_NAME_KEY))
            {
                name = decryptedEntry.get(VAULT_NAME_KEY);
                continue;
            }

            SpecialPassword pwd = new SpecialPassword(decryptedEntry, vault);
            db.put(pwd, rsa.encrypt(Utilities.objectToBytes(pwd.getMap())));
        }

        updateStatus(Status.SYNCHRONIZED);
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
        if (!db.containsKey(entry))
            throw new Exceptions(XC.PASSWORD_NOT_FOUND);
        db.remove(entry);

        requestSync();
    }

    public void replaceEntry(SpecialPassword newEntry, SpecialPassword oldEntry) throws Exceptions
    {
        // All checks should be before anything else to avoid loosing oldEntry
        // (either replace is completed fully, or not at all)

        if (!db.containsKey(oldEntry))
            throw new Exceptions(XC.PASSWORD_NOT_FOUND);

        validatePassword(newEntry, oldEntry);

        db.remove(oldEntry);
        db.put(newEntry, rsa.encrypt(Utilities.objectToBytes(newEntry.getMap())));

        requestSync();
    }

    public Vector<SpecialPassword> getDecrypted()
    {
        return db.keySet().stream().map(sp -> {
            try
            {
                return new SpecialPassword(sp);
            }
            catch (Exceptions e)
            {
                if (e.getCode() == XC.NULL)
                    Logger.printError("Database entry is null!");
                Terminator.terminate(e);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toCollection(Vector::new));
    }

    public Status getStatus()
    {
        return status;
    }

    private void sync()
    {
        Logger.printDebug("Saving Database to '" + vaultFile.getAbsolutePath() + "'...");

        Thread thread = new Thread(() -> {
            updateStatus(Status.SYNCHRONIZING);
            try
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("vaultName", name);

                Utilities.writeToFile(vaultFile.getAbsolutePath(),
                    db.values().stream().collect(Collectors.toCollection((Supplier<Vector<String>>) Vector::new)),
                    rsa.encrypt(Utilities.objectToBytes(map)));

                updateStatus(Status.SYNCHRONIZED);
                Logger.printDebug("Saving Database to '" + vaultFile.getAbsolutePath() + "' DONE!");
                Logger.printDebug("Database Synchronization returned status: " + status.toString());
            }
            catch (Exceptions e)
            {
                Logger.printError(e.getText());
                updateStatus(Status.SYNCHRONIZATION_FAILED);
                try
                {
                    Thread.sleep(Properties.DATABASE.SYNC_RETRY_DELAY_MS);
                }
                catch (Exception e1)
                {
                    Terminator.terminate(new Exceptions(XC.ERROR));
                }

                if (--retriesLeft > 0)
                    sync();
            }
        });

        thread.start();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (name.equals(this.name))
            return;

        this.name = name;
        requestSync();
    }

    private void updateStatus(Status newStatus)
    {
        status = newStatus;
        if (onStatusChanged != null)
            onStatusChanged.accept(status);
    }
}
