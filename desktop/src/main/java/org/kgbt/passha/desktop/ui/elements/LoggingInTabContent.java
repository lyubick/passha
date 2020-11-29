package org.kgbt.passha.desktop.ui.elements;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import org.kgbt.passha.desktop.languages.Local;

public class LoggingInTabContent extends org.kgbt.passha.desktop.ui.elements.GridPane implements TabContent
{
    private Label             l_text      = null;
    private ProgressIndicator pi_progress = null;

    public LoggingInTabContent()
    {
        l_text = new Label(Local.Texts.LABEL_LOGGING_IN.toString());
        l_text.beHeader();

        pi_progress = new ProgressIndicator();

        setAlignment(Pos.CENTER);

        addHElement(l_text);
        addHElement(pi_progress);

        GridPane.setHalignment(l_text, HPos.CENTER);
        GridPane.setHalignment(pi_progress, HPos.CENTER);
    }

    @Override public void activateTab()
    {

    }

    @Override public void closeTab()
    {

    }

    @Override public void reload()
    {

    }

    @Override public void setName(String name)
    {

    }

    @Override public String getName()
    {
        return Local.Texts.LABEL_LOGGING_IN.toString();
    }
}
