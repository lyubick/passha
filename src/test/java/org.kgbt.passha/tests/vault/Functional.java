package org.kgbt.passha.tests.vault;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.kgbt.passha.core.Vault;
import org.kgbt.passha.db.Database;
import org.kgbt.passha.db.SpecialPassword;
import org.kgbt.passha.db.iSpecialPassword;
import javafx.collections.ObservableList;
import org.kgbt.passha.languages.Local.Texts;
import org.kgbt.passha.logger.Logger;
import org.kgbt.passha.main.Exceptions;
import org.kgbt.passha.main.JfxRunner;
import org.kgbt.passha.main.Properties;
import org.kgbt.passha.main.Settings;
import org.kgbt.passha.reflections.VaultReflection;
import org.kgbt.passha.sha.SHA;

/**
 * Vault tests expects that underlying Database works correctly.
 */
@RunWith(JfxRunner.class)
public class Functional
{
    private byte[]                  masterHash     = null;
    private String                  databaseFilename;

    private Vault                   vault          = null;
    private Database                vaultsDatabase = null;
    private File                    vaultFile      = null;

    private final Stream<Integer>   numStream      = Stream.iterate(0, n -> n + 1);

    static AtomicInteger            num            = new AtomicInteger(0);

    private Vector<SpecialPassword> passwords      = null;

    @Before
    public void setUp() throws Exception
    {
        try
        {
            Logger.loggerON("");
        }
        catch (Exceptions e)
        {
            System.out.println("Can't open logger: " + e.getCode().toString());
        }

        try
        {
            masterHash = SHA.getHashBytes(("This is test!" + num.incrementAndGet()).getBytes());
            vault = new Vault(masterHash, true);
            vaultsDatabase = (Database) VaultReflection.getInstance().database().get(vault);
            databaseFilename = Properties.PATHS.VAULT
                + SHA.getHashString(masterHash, (String) VaultReflection.getInstance().SALT_FILENAME().get(vault))
                + Properties.EXTENSIONS.VAULT;
            vaultFile = new File(databaseFilename);
            passwords = new Vector<SpecialPassword>();
        }
        catch (Exceptions e)
        {
            throw new Exception(e.getCode().toString());
        }
    }

    @After
    public void tearDown() throws Exception
    {
        vault = null;
        vaultsDatabase = null;
        databaseFilename = null;
        vaultFile.delete();
        vaultFile = null;
        passwords.clear();
        passwords = null;
        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions e)
        {
            System.out.println("Can't close logger: " + e.getCode().toString());
        }
    }

    private SpecialPassword newSpecialPassword(String name)
    {
        return newSpecialPassword(name, "", "", "");
    }

    private SpecialPassword newSpecialPassword(String name, String shortcut)
    {
        return newSpecialPassword(name, "", "", shortcut);
    }

    private SpecialPassword newSpecialPassword(String name, String comment, String url, String shortcut)
    {
        try
        {
            return new SpecialPassword(name, comment, url, "8", false, false, "", shortcut, vault);
        }
        catch (Exceptions e)
        {
            throw new RuntimeException(e.getCode().toString());
        }
    }

    private void addPasswordToDatabase(SpecialPassword pwd)
    {
        try
        {
            vaultsDatabase.addEntry(pwd);
        }
        catch (Exceptions e)
        {
            new RuntimeException(e.getCode().toString());
        }
    }

    @Test
    public void testGetIfaceSuccess()
    {
        numStream.limit(3).map(number -> newSpecialPassword("V" + number))
            .collect(Collectors.toCollection(() -> passwords));
        passwords.stream().forEach(pwd -> addPasswordToDatabase(pwd));

        ObservableList<iSpecialPassword> list = vault.getIface();
        assertEquals(passwords.size(), list.size());
        assertTrue(passwords.stream().allMatch(pwd -> list.stream().anyMatch(ipwd -> pwd.equals(ipwd.getOrigin()))));
    }

    @Test
    public void testGetIfaceNoPasswords()
    {
        ObservableList<iSpecialPassword> list = vault.getIface();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGetPasswordByShortcutSuccess()
    {
        numStream.limit(3).map(number -> newSpecialPassword("V" + number, Integer.toString(number)))
            .collect(Collectors.toCollection(() -> passwords));
        passwords.stream().forEach(pwd -> addPasswordToDatabase(pwd));

        SpecialPassword pwd = vault.getPasswordByShortcut("2");
        assertNotEquals(pwd, null);
        assertTrue(
            pwd.equals(passwords.stream().filter(p -> p.getShortcut().equals("2")).limit(1).findAny().orElse(null)));
    }

    @Test
    public void testGetPasswordByShortcutNoSuchShortcut()
    {
        numStream.limit(3).map(number -> newSpecialPassword("V" + number, Integer.toString(number)))
            .collect(Collectors.toCollection(() -> passwords));
        passwords.stream().forEach(pwd -> addPasswordToDatabase(pwd));

    }

    @Test
    public void testGetPasswordByShortcutEmptyVault()
    {
        assertEquals(vault.getPasswordByShortcut("1"), null);
    }

    @Test
    public void testAddPassword()
    {
        SpecialPassword pwd = newSpecialPassword("Somename");
        try
        {
            vault.addPassword(pwd);
            SpecialPassword pwdFromDatabase = vaultsDatabase.getDecrypted().get(0);
            assertEquals(pwdFromDatabase.getParentVault(), vault);
            assertEquals(pwd, pwdFromDatabase);
        }
        catch (Exceptions e)
        {
            fail(e.getCode().toString());
        }
    }

    @Test
    public void testReplacePassword()
    {
        SpecialPassword pwd = newSpecialPassword("Somename");
        SpecialPassword pwd2 = newSpecialPassword("Othername");
        addPasswordToDatabase(pwd);

        vault.setSelected(pwd);

        try
        {
            vault.replacePassword(pwd2);
            assertEquals(vaultsDatabase.getDecrypted().size(), 1);
            SpecialPassword pwdFromDatabase = vaultsDatabase.getDecrypted().get(0);
            assertEquals(pwdFromDatabase.getParentVault(), vault);
            assertEquals(pwd2, pwdFromDatabase);
        }
        catch (Exceptions e)
        {
            fail(e.getCode().toString());
        }
    }

    @Test
    public void testRemoveSelectedPassword()
    {
        SpecialPassword pwd = newSpecialPassword("Somename");
        addPasswordToDatabase(pwd);

        vault.setSelected(pwd);

        try
        {
            vault.removePassword(null);
            assertTrue(vaultsDatabase.getDecrypted().isEmpty());
        }
        catch (Exceptions e)
        {
            fail(e.getCode().toString());
        }
    }

    @Test
    public void testRemovePassword()
    {
        SpecialPassword pwd = newSpecialPassword("Somename");
        addPasswordToDatabase(pwd);

        try
        {
            vault.removePassword(pwd);
            assertTrue(vaultsDatabase.getDecrypted().isEmpty());
        }
        catch (Exceptions e)
        {
            fail(e.getCode().toString());
        }
    }

    @Test
    public void testExport()
    {
        try
        {
            // Settings are required for Language, that is required for labels in export file
            Settings.init();
        }
        catch (Exceptions e)
        {
            fail(e.getCode().toString());
        }

        numStream.limit(10).map(number -> newSpecialPassword("V" + number, "C" + number, "U" + number, ""))
            .collect(Collectors.toCollection(() -> passwords));
        passwords.stream().forEach(pwd -> addPasswordToDatabase(pwd));

        final String exportFilname = "exportTest.txt";
        File outfile = new File(exportFilname);
        if (outfile.exists()) outfile.delete();

        vault.export(exportFilname);

        assertTrue(outfile.exists());

        try
        {
            Map<String, List<String>> m = Files.lines(Paths.get(outfile.getAbsolutePath()))
                .collect(Collectors.groupingBy(line -> line.substring(0, Math.max(0, line.indexOf(':')))));

            m.remove(Texts.LABEL_VAULT.toString());
            m.remove("");   // empty key contains delimiters
            assertTrue(m.values().stream().allMatch(list -> list.size() == 10));
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

        outfile.delete();
    }

    @Test
    public void testGetPasswordsWithShortcut()
    {
        numStream.limit(10)
            .map(number -> newSpecialPassword("V" + number, number % 2 == 0 ? Integer.toString(number) : ""))
            .collect(Collectors.toCollection(() -> passwords));
        passwords.stream().forEach(pwd -> addPasswordToDatabase(pwd));

        Vector<SpecialPassword> pwds = vault.getPasswordsWithShortcut();
        assertEquals(5, pwds.size());
        assertTrue(pwds.stream().distinct().allMatch(p -> !p.getShortcut().isEmpty()));
    }

    @Test
    public void testInitializedFrom()
    {
        assertTrue(vault.initializedFrom(masterHash));
        assertFalse(vault.initializedFrom(SHA.getHashBytes(masterHash)));
    }

}
