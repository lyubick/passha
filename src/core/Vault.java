package core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

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

    public Vault(String password, boolean isNewUser) throws Exceptions
    {
        // Generate master hash
        masterHash = SHA.getHashBytes(password.getBytes());

        // Initialize RSA
        RSA rsa = new RSA(SHA.getHashString(masterHash, SALT_P), SHA.getHashString(masterHash, SALT_Q),
                SHA.getHashString(masterHash, SALT_E));

        // Initialize Database
        database = new Database(rsa, SHA.getHashString(masterHash, SALT_FILENAME), isNewUser, this);

        // FIXME Call garbage collector on finish
    }

    public ObservableList<iSpecialPassword> getIface()
    {
        ObservableList<iSpecialPassword> pSet = FXCollections.observableArrayList();

        for (SpecialPassword sp : database.getDecrypted())
        {
            pSet.add(new iSpecialPassword(sp));
        }
        return pSet;
    }

    public SpecialPassword getPasswordByShortcut(String shortcut)
    {
        // TODO: use javafx8 features
        for (SpecialPassword sp : database.getDecrypted())
            if (sp.getShortcut().equals(shortcut)) return sp;
        return null;
    }

    public void addPassowrd(SpecialPassword password) throws Exceptions
    {
        database.addEntry(password);
        password.setParentVault(this);
    }

    public void replacePassword(SpecialPassword newEntry) throws Exceptions
    {
        SpecialPassword sp = getPasswordByShortcut(newEntry.getShortcut());
        Logger.printDebug("sp is " + (sp == null ? "null" : "not null"));
        if (sp != null)
        {
            Logger.printDebug("newEntry: shortcut [" + newEntry.getShortcut() + "] name " + newEntry.getName());
            Logger.printDebug("SP: shortcut [" + sp.getShortcut() + "] name " + sp.getName());
            if (!newEntry.getShortcut().equals("") && !sp.getName().equals(newEntry.getName()))
                throw new Exceptions(XC.PASSWORD_SHORTCUT_ALREADY_IN_USE).setText(sp.getName());
        }

        database.replaceEntry(newEntry, selectedPassword);
        newEntry.setParentVault(this);
        selectedPassword.clearParentVault();
    }

    public void removePassword(SpecialPassword entry) throws Exceptions
    {
        if (entry == null) entry = selectedPassword;
        database.deleteEntry(entry);
        entry.clearParentVault();
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
        // TODO: move to properties maybe???
        final String TAB = "\t";
        final String FILE_START_TAG = "<file_start>";
        final String FILE_END_TAG = "</file_end>";
        final String RECORD_START_TAG = TAB + "<record_start>";
        final String RECORD_END_TAG = TAB + "</record_end>";
        final String HASH_CODE_START_TAG = "<hash>";
        final String HASH_CODE_END_TAG = "</hash>";

        Vector<String> exportStrings = new Vector<String>();

        exportStrings.add(FILE_START_TAG);
        for (SpecialPassword sp : database.getDecrypted())
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
            Terminator.terminate(e);
        }
    }

    public Vector<SpecialPassword> getPasswordsWithShortcut()
    {
        Vector<SpecialPassword> pwds = new Vector<SpecialPassword>();

        // TODO: java8 style
        for (SpecialPassword sp : database.getDecrypted())
            if (!sp.getShortcut().equals("")) pwds.add(sp);

        return pwds;
    }
}
