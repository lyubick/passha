/**
 *
 */
package db;

import java.util.HashMap;
import java.util.Vector;

import main.Exceptions;
import cryptosystem.CryptoSystem;
import utilities.Utilities;
import db.SpecialPassword;

/**
 * @author lyubick
 *
 */
public class Database
{
    private Vector<String>          encrypted = null;
    private Vector<SpecialPassword> decrypted = null;

    @SuppressWarnings("unchecked")
    public Database(Vector<String> encryptedDB, boolean encrypt) throws Exceptions
    {
        encrypted = new Vector<String>();
        decrypted = new Vector<SpecialPassword>();

        for (String entry : encryptedDB)
        {
            decrypted.add(new SpecialPassword((HashMap<String, String>) Utilities.bytesToObject(CryptoSystem
                    .getInstance().rsaDecrypt(entry))));

            // Change encryption or keep it ?
            if (!encrypt)
                encrypted.add(entry);
            else
            {
                encrypted.add(CryptoSystem.getInstance().rsaEncrypt(
                        Utilities.objectToBytes(decrypted.lastElement().getMap())));
            }

        }
    }

    public Database(Vector<SpecialPassword> decryptedDB) throws Exceptions
    {
        for (SpecialPassword entry : decryptedDB)
        {
            decryptedDB.add(entry);
            encrypted.add(CryptoSystem.getInstance().rsaEncrypt(Utilities.objectToBytes(entry)));
        }
    }

    public void addEntry(SpecialPassword entry) throws Exceptions
    {
        decrypted.addElement(entry);
        encrypted.addElement(CryptoSystem.getInstance().rsaEncrypt(Utilities.objectToBytes(entry.getMap())));
    }

    public void deleteEntry(SpecialPassword entry)
    {
        int idx = decrypted.indexOf(entry);
        decrypted.remove(idx);
        encrypted.remove(idx);
    }

    public void replaceEntry(SpecialPassword newEntry, SpecialPassword oldEntry) throws Exceptions
    {
        int idx = decrypted.indexOf(oldEntry);
        decrypted.remove(idx);
        encrypted.remove(idx);
        decrypted.add(idx, newEntry);
        encrypted.add(idx, CryptoSystem.getInstance().rsaEncrypt(Utilities.objectToBytes(newEntry.getMap())));
    }

    public Vector<String> getEncrypted()
    {
        return encrypted;
    }

    public Vector<SpecialPassword> getDecrypted()
    {
        return decrypted;
    }

}
