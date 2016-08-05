package main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// List all test suites below
@RunWith(Suite.class)
@Suite.SuiteClasses(
{ tests.database.AllTests.class })
public class AllTests
{
    // holder for annotations
}
