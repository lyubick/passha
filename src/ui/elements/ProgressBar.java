package ui.elements;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.layout.VBox;
import languages.Texts.TextID;

public class ProgressBar extends javafx.scene.control.ProgressBar
{
    public static final int LABEL_WIDTH = 100;

    private Label label = null;

    public ProgressBar(TextID label)
    {
        this(label.toString());
    }

    public ProgressBar(String label)
    {
        this.label = new Label(label);
        this.label.setMinWidth(LABEL_WIDTH);
        this.label.setMaxWidth(LABEL_WIDTH);
    }

    public Label getLabel()
    {
        return label;
    }

    public VBox getVBoxed()
    {
        return new VBox(label, this);
    }

    public void setLabel(String label)
    {
        this.label.setText(label);
    }

    public void setLabel(TextID label)
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
