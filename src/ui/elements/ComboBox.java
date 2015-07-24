package ui.elements;

import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import languages.Texts.TextID;
import ui.elements.Label.LABEL;

public class ComboBox extends javafx.scene.control.ComboBox<String>
{
    private Label label = null;

    public ComboBox(ObservableList<String> list, TextID label, double maxLength)
    {
        this(list, label.toString(), maxLength);
    }

    public ComboBox(ObservableList<String> list, String label, double maxLength)
    {
        super(list);
        this.label = new Label(label);
        this.label.setMinWidth(LABEL.WIDTH.L);
        this.label.setMaxHeight(LABEL.WIDTH.L);

        this.setMaxWidth(maxLength);
        this.setMinWidth(maxLength);
        this.setMinHeight(LABEL.HEIGHT.M);
        this.setMaxHeight(LABEL.HEIGHT.M);
    }

    public Label getLabel()
    {
        return label;
    }

    public HBox getHBoxed()
    {
        HBox hBox = new HBox(label, this);

        hBox.setMinWidth(label.getMinWidth() + this.getMinWidth());
        hBox.setMinHeight(LABEL.HEIGHT.M);
        hBox.setMaxWidth(label.getMaxWidth() + this.getMaxWidth());
        hBox.setMaxHeight(LABEL.HEIGHT.M);

        return hBox;
    }
}
