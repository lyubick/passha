package org.kgbt.passha.core.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.kgbt.passha.core.db.Database.Status;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.rsa.RSA;
import org.kgbt.passha.core.sha.SHA;
import org.kgbt.passha.core.common.Utilities;
import org.kgbt.passha.core.common.Terminator;

public class Vault
{
    private final String SALT_FILENAME = "FILENAME";
    private final String SALT_P        = "P";
    private final String SALT_Q        = "Q";
    private final String SALT_E        = "E";

    private byte[] masterHash = null;

    private Database        database         = null;
    private SpecialPassword selectedPassword = null;

    public Vault(byte[] hash, boolean isNewUser, String root) throws Exceptions
    {
        masterHash = hash;

        // Initialize RSA
        RSA rsa = new RSA(SHA.getHashString(masterHash, SALT_P), SHA.getHashString(masterHash, SALT_Q),
                SHA.getHashString(masterHash, SALT_E));

        // Initialize Database
        database = new Database(rsa, SHA.getHashString(masterHash, SALT_FILENAME), isNewUser, root, this);

        // All initialized, let's clean-up a little
        System.gc();
    }


    public Vault(byte[] hash, boolean isNewUser) throws Exceptions
    {
        masterHash = hash;

        // Initialize RSA
        RSA rsa = new RSA(SHA.getHashString(masterHash, SALT_P), SHA.getHashString(masterHash, SALT_Q),
            SHA.getHashString(masterHash, SALT_E));

        // Initialize Database
        database = new Database(rsa, SHA.getHashString(masterHash, SALT_FILENAME), isNewUser, "", this);

        // All initialized, let's clean-up a little
        System.gc();
    }

    public Vector<SpecialPassword> getDecryptedPasswords()
    {
        return database.getDecrypted();
    }

    // Android specific
    public Collection<SpecialPassword> getPasswords()
    {
        return database.getDecrypted();
    }

    public SpecialPassword getPasswordByShortcut(String shortcut)
    {
        return database.getDecrypted().stream().filter(sp -> sp.getShortcut().equals(shortcut)).limit(1).findAny()
            .orElse(null);
    }

    public void addPassword(SpecialPassword password) throws Exceptions
    {
        database.addEntry(password);
        password.setParentVault(this);
    }

    public void replacePassword(SpecialPassword newEntry) throws Exceptions
    {
        newEntry.setParentVault(this);
        database.replaceEntry(newEntry, selectedPassword);

        selectedPassword = null;
    }

    public void removePassword(SpecialPassword entry) throws Exceptions
    {
        if (entry == null)
            entry = selectedPassword;
        database.deleteEntry(entry);
    }

    public void setSelected(SpecialPassword password)
    {
        selectedPassword = password;
    }

    public SpecialPassword getSelected()
    {
        return selectedPassword;
    }

    public String getHashForPassword(long cycles, String pwdName)
    {
        byte[] tmp = null;

        try
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(masterHash.clone());
            outputStream.write((pwdName + cycles).getBytes());
            tmp = outputStream.toByteArray();
        }
        catch (IOException e)
        {
            Terminator.terminate(new Exceptions(XC.ERROR));
        }

        while (cycles-- > 0)
        {
            tmp = SHA.getHashBytes(tmp);
        }

        return Utilities.bytesToHex(tmp);
    }

    public void export(String vaultLabel, String nameLabel, String urlLabel, String commentLabel, String passwordLabel,
        String fileName)
    {
        Vector<String> exportStrings = new Vector<>();
        final String delimiter = "-----------------------------------------------";

        exportStrings.add(vaultLabel + ": '" + database.getName() + "'");

        for (SpecialPassword sp : database.getDecrypted())
        {
            exportStrings.add(delimiter);
            exportStrings.add(nameLabel + ": '" + sp.getName() + "'");
            if (!sp.getUrl().isEmpty())
                exportStrings.add(urlLabel + ": '" + sp.getUrl() + "'");
            if (!sp.getComment().isEmpty())
                exportStrings.add(commentLabel + ": '" + sp.getComment() + "'");
            exportStrings.add(passwordLabel + ": '" + sp.getPassword() + "'");
        }

        exportStrings.add(delimiter);

        try
        {
            Utilities.writeToFile(fileName, exportStrings);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
    }

    public Vector<SpecialPassword> getPasswordsWithShortcut()
    {
        return database.getDecrypted().stream().filter(sp -> !sp.getShortcut().isEmpty())
            .collect(Collectors.toCollection(Vector::new));
    }

    public String getName()
    {
        return database.getName();
    }

    public void setName(String name)
    {
        database.setName(name);
    }

    public boolean initializedFrom(byte[] hash)
    {
        return Arrays.equals(masterHash, hash);
    }

    public String getMasterHash(RSA rsa)
    {
        return rsa.encrypt(masterHash);
    }

    public void setOnDbStatusChanged(Consumer<Status> onDbStatusChanged)
    {
        database.setOnStatusChanged(onDbStatusChanged);
    }

    public Status getDBStatus()
    {
        return database.getStatus();
    }
}
