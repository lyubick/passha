package org.kgbt.passha.desktop.ui.elements;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import org.kgbt.passha.desktop.ui.interfaces.iSpecialPassword;

public class Search extends TextField {
    public Search(FilteredList<iSpecialPassword> p) {
        this.setOnKeyReleased(event -> p.setPredicate(predicate -> predicate.getName().toLowerCase().contains(this.getText().toLowerCase())));
    }
}
