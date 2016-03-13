package ui.elements;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import main.Exceptions;
import main.Exceptions.XC;

public interface TabContent
{
    public void activateTab();

    public void closeTab();

    public void refreshTab();

    public default EventHandler<ActionEvent> getOnEditBtnAction() throws Exceptions
    {
        throw new Exceptions(XC.HANDLER_DOES_NOT_EXIST);
    }

    public default EventHandler<ActionEvent> getOnCopyToClipboardBtnAction() throws Exceptions
    {
        throw new Exceptions(XC.HANDLER_DOES_NOT_EXIST);
    }

    public default EventHandler<ActionEvent> getOnNewBtnAction() throws Exceptions
    {
        throw new Exceptions(XC.HANDLER_DOES_NOT_EXIST);
    }

    public default EventHandler<ActionEvent> getOnDeleteBtnAction() throws Exceptions
    {
        throw new Exceptions(XC.HANDLER_DOES_NOT_EXIST);
    }

    public default EventHandler<ActionEvent> getOnResetBtnAction() throws Exceptions
    {
        throw new Exceptions(XC.HANDLER_DOES_NOT_EXIST);
    }

    public default EventHandler<ActionEvent> getOnExportBtnAction() throws Exceptions
    {
        throw new Exceptions(XC.HANDLER_DOES_NOT_EXIST);
    }
}
