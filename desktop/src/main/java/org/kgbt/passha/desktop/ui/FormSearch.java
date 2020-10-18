package org.kgbt.passha.desktop.ui;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Border;
import javafx.stage.StageStyle;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.*;
import org.kgbt.passha.desktop.ui.elements.Button;
import org.kgbt.passha.desktop.ui.elements.Label;
import org.kgbt.passha.desktop.ui.interfaces.iSpecialPassword;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class FormSearch extends AbstractForm
{
    public FormSearch(AbstractForm parent, VaultTabContent x, FilteredList<iSpecialPassword> filter) throws Exceptions
    {
        super(parent, Texts.LABEL_ABOUT, WindowPriority.ONLY_ONE_OPENED, false);

        Search tf_search = new Search(filter);

        stage.initStyle(StageStyle.UNDECORATED);

        grid.addHElement(tf_search);

        GridPane.setHalignment(tf_search, HPos.CENTER);

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) close();
        });

        open();
        tf_search.requestFocus();
    }
}
