package org.kgbt.passha.core;

import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Supplier;


// TODO: Review this idea - maybe there is some other way of implementing this

/**
 * This abstract class collects functions that are required by core, but whose implementation depends on environment (desktop, android, etc)
 */
public abstract class GenericUI
{
    private static GenericUI self = null;

    /**
     *
     * @param uiCreator - new child of GenericUI class supplier
     */
    public static void init(Supplier<GenericUI> uiCreator) {
        // Child class instantiation will call GenericUI constructor, which will fill self variable;
        // This schema is implemented for consistence with other singleton classes
        uiCreator.get();
    }

    public static GenericUI getInstance() throws Exceptions
    {
        if (self == null)
            throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    protected GenericUI()
    {
        self = this;
    }

    /**
     * Request user input on whether to force exit, when core is not ready to exit. (e.g.: if save operation is in progress)
     *
     * @return - true: force exit, false: do not exit.
     */
    public abstract boolean confirmUnsafeExit();

    /**
     * Restart the application
     * @throws IOException
     * @throws URISyntaxException
     */
    public abstract void restart() throws IOException, URISyntaxException;

    public abstract void reportMigrationErrors(int numberOfErrors);

    public abstract void reportMigrationSuccess();
}
