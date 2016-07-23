package core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import languages.Local.Texts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

import db.Database;
import db.SpecialPassword;
import db.iSpecialPassword;
import main.Exceptions;
import main.Exceptions.XC;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;
import main.Terminator;

public class Vault
{
    private final String    SALT_FILENAME    = "FILENAME";
    private final String    SALT_P           = "P";
    private final String    SALT_Q           = "Q";
    private final String    SALT_E           = "E";

    private byte[]          masterHash       = null;

    private Database        database         = null;
    private SpecialPassword selectedPassword = null;

    private String          name             = "";

    public Vault(byte[] hash, boolean isNewUser) throws Exceptions
    {
        masterHash = hash;

        // Initialize RSA
        RSA rsa = new RSA(SHA.getHashString(masterHash, SALT_P), SHA.getHashString(masterHash, SALT_Q),
            SHA.getHashString(masterHash, SALT_E));

        // Initialize Database
        database = new Database(rsa, SHA.getHashString(masterHash, SALT_FILENAME), isNewUser, this);

        // All initialized, let's clean-up a little
        System.gc();
    }

    public ObservableList<iSpecialPassword> getIface()
    {
        ObservableList<iSpecialPassword> pSet = FXCollections.observableArrayList();
        database.getDecrypted().stream().map(sp -> new iSpecialPassword(sp))
            .collect(Collectors.toCollection(() -> pSet));
        return pSet;
    }

    public SpecialPassword getPasswordByShortcut(String shortcut)
    {
        Optional<SpecialPassword> pwd =
            database.getDecrypted().stream().filter(sp -> sp.getShortcut().equals(shortcut)).findFirst();

        if (pwd.isPresent()) return pwd.get();

        return null;
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
        if (entry == null) entry = selectedPassword;
        database.deleteEntry(entry);

        entry = null;
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

    public void export(String fileName)
    {
        Vector<String> exportStrings = new Vector<>();
        final String delimiter = "-----------------------------------------------";

        exportStrings.add(Texts.LABEL_VAULT.toString() + ": '" + name + "'");

        for (SpecialPassword sp : database.getDecrypted())
        {
            exportStrings.add(delimiter);
            exportStrings.add(Texts.LABEL_NAME.toString() + ": '" + sp.getName() + "'");
            if (!sp.getUrl().isEmpty()) exportStrings.add(Texts.LABEL_URL.toString() + ": '" + sp.getUrl() + "'");
            if (!sp.getComment().isEmpty())
                exportStrings.add(Texts.LABEL_COMMENT.toString() + ": '" + sp.getComment() + "'");
            exportStrings.add(Texts.LABEL_PASSWORD.toString() + ": '" + sp.getPassword() + "'");
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
            .collect(Collectors.toCollection(() -> new Vector<>()));
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (this.name.equals(name)) return;

        this.name = name;

        // Most likely database is now setting name while creating
        if (database != null) database.requestSync();
    }

    public boolean initializedFrom(byte[] hash)
    {
        return Arrays.equals(masterHash, hash);
    }

    public String getMasterHash(RSA rsa)
    {
        return rsa.encrypt(masterHash);
    }
}
