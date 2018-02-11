package org.kgbt.passha.desktop.ui.elements;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.layout.VBox;
import org.kgbt.passha.desktop.languages.Local.Texts;

public class ProgressBar extends javafx.scene.control.ProgressBar
{
    private Label label = null;

    public ProgressBar(Texts label, double width, double height)
    {
        this(label.toString(), width, height);
    }

    public ProgressBar(String label, double width, double height)
    {
        this.label = new Label(label);

        this.setMinWidth(width);
        this.setMinHeight(height);
        this.setMaxWidth(width);
        this.setMaxHeight(height);
    }

    public Label getLabel()
    {
        return label;
    }

    public VBox getVBoxed()
    {
        VBox vBox = new VBox(label, this);

        vBox.setMinWidth(label.getMinWidth() + this.getMinWidth());
        vBox.setMinHeight(label.getMinHeight() + this.getMinHeight());
        vBox.setMaxWidth(label.getMaxWidth() + this.getMaxWidth());
        vBox.setMaxHeight(label.getMaxHeight() + this.getMaxHeight());

        return vBox;
    }

    public void setLabel(String label)
    {
        this.label.setText(label);
    }

    public void setLabel(Texts label)
    {
        setLabel(label.toString());
    }

    public void rebind(ReadOnlyDoubleProperty readOnlyDoubleProperty,
            ReadOnlyStringProperty readOnlyStringProperty)
    {
        unbind();

        this.progressProperty().bind(readOnlyDoubleProperty);
        this.getLabel().textProperty().bind(readOnlyStringProperty);
    }

    public void unbind()
    {
        this.progressProperty().unbind();
        this.getLabel().textProperty().unbind();
    }
}
