package ui.elements;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import languages.Local.Texts;
import main.Properties;

public class PasswordEntryField extends PasswordField implements LabeledItem
{
    private Label                 label   = null;

    private SimpleBooleanProperty isValid = null;

    public PasswordEntryField(Texts label, int maxLength)
    {
        this(label.toString(), maxLength);
        beNormal();
    }

    public PasswordEntryField(String label, int maxLength)
    {
        this.label = new Label(label);
        beNormal();

        this.label.visibleProperty().bind(this.visibleProperty());
        isValid = new SimpleBooleanProperty(false);

        this.setMaxWidth(maxLength);
        this.setMinWidth(maxLength);
        this.setMinHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);
        this.setMaxHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);
    }

    public Label getLabel()
    {
        return label;
    }

    public HBox getHBoxed()
    {
        HBox hBox = new HBox(label, this);

        hBox.setMinWidth(label.getMinWidth() + this.getMinWidth());
        hBox.setMinHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);
        hBox.setMaxWidth(label.getMaxWidth() + this.getMaxWidth());
        hBox.setMaxHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);

        return hBox;
    }

    public void beError()
    {
        label.beError();
        this.setFont(Label.FONT_ERROR);
        this.setStyle("-fx-text-fill: red; -fx-text-box-border: red;");
    }

    public void beNormal()
    {
        label.beNormal();
        this.setFont(Label.FONT_PRIMARY);
        this.setStyle("-fx-text-fill: black; -fx-text-box-border: black;");
    }

    public void setValid(boolean valid)
    {
        isValid.setValue(valid);
        if (valid == false)
            beError();
        else
            beNormal();
    }

    public SimpleBooleanProperty isValidProperty()
    {
        return isValid;
    }
}
