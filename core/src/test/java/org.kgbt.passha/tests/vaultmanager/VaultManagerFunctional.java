package org.kgbt.passha.tests.vaultmanager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.reflections.DatabaseReflection;
import org.kgbt.passha.reflections.VaultManagerReflection;
import org.kgbt.passha.reflections.VaultReflection;

public class VaultManagerFunctional
{
    private VaultManagerReflection vaultManagerReflection = VaultManagerReflection.getInstance();
    private VaultReflection        vaultReflection        = VaultReflection.getInstance();
    private DatabaseReflection     databaseReflection     = DatabaseReflection.getInstance();

    @Before
    public void setUp() {
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

     @Test
     public void testAddVaultStringBoolean()
     {
         //TODO: Implement tests
         System.out.println("no test");

        //fail("Not yet implemented");
     }

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
