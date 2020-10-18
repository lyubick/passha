package org.kgbt.passha.desktop.ui;

import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.stage.StageStyle;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.GridPane;
import org.kgbt.passha.desktop.ui.elements.Search;
import org.kgbt.passha.desktop.ui.elements.VaultTabContent;
import org.kgbt.passha.desktop.ui.interfaces.iSpecialPassword;

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
