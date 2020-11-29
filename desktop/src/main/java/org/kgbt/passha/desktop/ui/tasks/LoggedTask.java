package org.kgbt.passha.desktop.ui.tasks;

import javafx.concurrent.Task;
import org.kgbt.passha.core.logger.Logger;

public abstract class LoggedTask<V> extends Task<V>
{
    @Override protected void scheduled()
    {
        super.scheduled();
        Logger.printTrace("Init task execution scheduled!");
    }

    @Override protected void running()
    {
        super.running();
        Logger.printTrace("Init task execution running!");
    }

    @Override protected void succeeded()
    {
        super.succeeded();
        Logger.printTrace("Init task execution succeeded!");
    }

    @Override protected void cancelled()
    {
        super.cancelled();
        Logger.printTrace("Init task execution cancelled!");
    }

    @Override protected void failed()
    {
        super.failed();
        Logger.printTrace("Init task execution failed!");
    }
}
