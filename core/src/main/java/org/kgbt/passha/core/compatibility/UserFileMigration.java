package org.kgbt.passha.core.compatibility;

import org.kgbt.passha.core.GenericUI;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.common.Utilities;
import org.kgbt.passha.core.db.SpecialPassword;
import org.kgbt.passha.core.db.Vault;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.rsa.RSA;
import org.kgbt.passha.core.sha.SHA;

import java.io.File;
import java.util.Arrays;

public class UserFileMigration
{
    private static final String SALT_FILENAME  = "FILENAME";
    private static final String SALT_P         = "P";
    private static final String SALT_Q         = "Q";
    private static final String SALT_E         = "E";

    private static final String USER_FILE_PATH = "user/";
    private static final String USER_FILE_EXT  = ".cif";

    private static File getOldUserFile(String masterPassword) throws Exceptions
    {
        File userFolder = new File(USER_FILE_PATH);
        Logger.printDebug("Searching old user dir: " + USER_FILE_PATH);
        if (!userFolder.exists()) throw new Exceptions(XC.DIR_DOES_NOT_EXIST);
        Logger.printDebug("Found old user dir: " + USER_FILE_PATH);

        byte[] masterHash = SHA.getHashBytes(masterPassword.getBytes());

        File userFile = new File(USER_FILE_PATH
            + SHA.getHashString((Arrays.toString(masterHash) + SALT_FILENAME).getBytes()) + USER_FILE_EXT);

        Logger.printDebug("Searching old user file: " + userFile.getAbsolutePath());
        if (!userFile.exists())
        {
            String[] list  = userFolder.list();
            if (list != null && list.length == 0) userFolder.delete();
            throw new Exceptions(XC.FILE_DOES_NOT_EXIST);
        }
        Logger.printDebug("Found old user file: " + userFile.getAbsolutePath());

        return userFile;
    }

    /**
     * Try migrate v2.4 user file to v3.0
     *
     * @param masterPassword
     * @return vault added to VaultManager
     * @throws Exceptions
     */
    public static Vault tryMigrate(String masterPassword) throws Exceptions
    {
        Logger.printTrace("Migration STARTED!");
        try
        {
            RSA oldRSA = new RSA(
                Utilities.bytesToHex(SHA.getHashBytes((masterPassword + SALT_P).getBytes())),
                Utilities.bytesToHex(SHA.getHashBytes((masterPassword + SALT_Q).getBytes())),
                Utilities.bytesToHex(SHA.getHashBytes((masterPassword + SALT_E).getBytes())),
                RSA.RSA_SIZE._2048 // FIXME: Just for compilation
            );

            File oldFile = getOldUserFile(masterPassword);      // might throw

            int numberOfErrors = 0;
            Vault vault = VaultManager.getInstance().addVault(SHA.getHashBytes(masterPassword.getBytes()), true, ""); //FIXME

            for (String encryptedPassword : Utilities.readStringsFromFile(oldFile.getAbsolutePath()))
            {
                try
                {
                    vault.addPassword(new SpecialPassword(
                            Utilities.bytesToObject(oldRSA.decrypt(encryptedPassword)), vault));
                }
                catch (Exceptions e)        // if one password failed - don't need to abort all migration
                {
                    ++numberOfErrors;
                    Logger.printError("Failed to migrate password: " + e.getCode().toString());
                }
            }

            if (numberOfErrors > 0)
            {
                Logger.printTrace("Migration contained errors!");

                GenericUI.getInstance().reportMigrationErrors(numberOfErrors);
            }
            else
            {
                oldFile.delete();
                String[] list = oldFile.getParentFile().list();
                if (list != null && list.length == 0) oldFile.getParentFile().delete();

                GenericUI.getInstance().reportMigrationSuccess();

                Logger.printTrace("Migration SUCCESSFUL!");
            }
            return vault;
        }
        catch (Exceptions e)
        {
            if (e.getCode() == XC.DIR_DOES_NOT_EXIST || e.getCode() == XC.FILE_DOES_NOT_EXIST)
            {
                Logger.printDebug("Migration error: " + e.getCode().toString());
                throw e;
            }

            Logger.printFatal("Unexpected exception: " + e.getCode().toString());
            Terminator.terminate(e);
            return null;
        }
    }
}
