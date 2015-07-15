package ui.elements;

import javafx.scene.layout.HBox;
import languages.Texts.TextID;

public class EntryField extends javafx.scene.control.TextField
{
    public static final int LABEL_WIDTH = 100;

    private Label           label       = null;

    public EntryField(TextID label, int maxLength)
    {
        this.label = new Label(label.toString());
        this.label.setMinWidth(LABEL_WIDTH);

        this.setMaxWidth(maxLength);
        this.setMinWidth(maxLength);
    }

    public EntryField(String label, int maxLength)
    {
        this.label = new Label(label);
        this.label.setMinWidth(LABEL_WIDTH);

        this.setMaxWidth(maxLength);
        this.setMinWidth(maxLength);
    }

    public Label getLabel()
    {
        return label;
    }

    public HBox getHBoxed()
    {
        return new HBox(label, this);
    }

    public void beError()
    {
        // TODO text field highlight
        label.beError();
    }

    public void beNormal()
    {
        // TODO text field highlight
        label.beNormal();
    }
}
