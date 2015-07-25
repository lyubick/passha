package ui.elements;

import ui.elements.Label.LABEL;
import javafx.scene.layout.HBox;
import languages.Texts.TextID;

public class EntryField extends javafx.scene.control.TextField
{
    public static final class TEXTFIELD
    {
        public static final class WIDTH
        {
            public static final int XS  = 25;
            public static final int S   = 50;
            public static final int M   = 100;
            public static final int L   = 200;
            public static final int XL  = 300;
            public static final int XXL = 350;
        }

        public static final class HEIGTH
        {
            public static final int M = 30;
        }
    }

    private Label label = null;

    public EntryField(TextID label, int maxLength)
    {
        this(label.toString(), maxLength);
    }

    public EntryField(String label, int maxLength)
    {
        this.label = new Label(label);
        this.label.setMinWidth(LABEL.WIDTH.L);

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
