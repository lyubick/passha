package org.kgbt.passha.core.logger;

import org.kgbt.passha.core.common.Exceptions;

public interface Output
{
    /**
     * Function for any initialization requirements for output (Open streams, files, etc).
     * Called on Logger instantiation
     */
    void init() throws Exceptions;

    /**
     * Log output function
     *
     * @param lvl - level of the log
     * @param log - log string
     */
    void log(Logger.LOGLEVELS lvl, String log);

    /**
     * Function for any termination requirements for output (close streams, files, etc)
     */
    void terminate();
}
