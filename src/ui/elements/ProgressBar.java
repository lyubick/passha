package ui.elements;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.layout.VBox;
import languages.Texts.TextID;
import ui.elements.Label.LABEL;

public class ProgressBar extends javafx.scene.control.ProgressBar
{
    private Label label = null;

    public ProgressBar(TextID label, double width, double height)
    {
        this(label.toString(), width, height);
    }

    public ProgressBar(String label, double width, double height)
    {
        this.label = new Label(label);

        this.label.setMinWidth(LABEL.WIDTH.M);
        this.label.setMaxWidth(LABEL.WIDTH.M);

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
