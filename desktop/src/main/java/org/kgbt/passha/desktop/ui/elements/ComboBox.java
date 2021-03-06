package org.kgbt.passha.desktop.ui.elements;

import javafx.collections.ObservableList;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.desktop.languages.Local.Texts;

public class ComboBox extends javafx.scene.control.ComboBox<String> implements LabeledItem
{
    private Label label = null;

    public ComboBox(ObservableList<String> list, Texts label, double maxLength)
    {
        this(list, label.toString(), maxLength);
    }

    public ComboBox(ObservableList<String> list, String label, double maxLength)
    {
        super(list);
        this.label = new Label(label);

        this.setMaxWidth(maxLength);
        this.setMinWidth(maxLength);
        this.setMinHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);
        this.setMaxHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);

    }

    public Label getLabel()
    {
        return label;
    }

    @Override
    public void beError()
    {
        label.beError();
    }

    @Override
    public void beNormal()
    {
        label.beNormal();
    }
}
