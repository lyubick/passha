package tests.vaultmanager;

import org.junit.After;
import org.junit.Before;
import core.VaultManager;
import logger.Logger;
import main.Exceptions;
import reflections.DatabaseReflection;
import reflections.VaultManagerReflection;
import reflections.VaultReflection;

public class Functional
{
    private VaultManagerReflection vaultManagerReflection = VaultManagerReflection.getInstance();
    private VaultReflection        vaultReflection        = VaultReflection.getInstance();
    private DatabaseReflection     databaseReflection     = DatabaseReflection.getInstance();

    @Before
    public void setUp() throws Exception
    {
        VaultManager.init();

        try
        {
            Logger.loggerON("");
        }
        catch (Exceptions e)
        {
            System.out.println("Can't open logger: " + e.getCode().toString());
        }
    }

    @After
    public void tearDown() throws Exception
    {
        vaultManagerReflection.self().set(null, null);

        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions e)
        {
            System.out.println("Can't close logger: " + e.getCode().toString());
        }
    }

    // @Test
    // public void testAddVaultStringBoolean()
    // {
    // fail("Not yet implemented");
    // }

    // @Test
    // public void testAddVaultByteArrayBoolean()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testGetSelectedPassword()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testRemoveVault()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testActivateVault()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testGetActiveVault()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testDeactivateVault()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testIsFull()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testSize()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testActivateNextVault()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testAutologin()
    // {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testIsReadyToExit()
    // {
    // fail("Not yet implemented");
    // }

}
