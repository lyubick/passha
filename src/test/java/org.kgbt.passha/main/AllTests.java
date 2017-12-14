package org.kgbt.passha.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// List all test suites below
@RunWith(Suite.class)
@Suite.SuiteClasses(
{
        org.kgbt.passha.tests.database.AllTests.class,
        org.kgbt.passha.tests.vault.AllTests.class,
        org.kgbt.passha.tests.vaultmanager.AllTests.class
})
public class AllTests
{
    // holder for annotations
}
