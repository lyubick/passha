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
            public static final int M = 40;
        }
    }

    private Label label = null;

    public EntryField(TextID label, int maxLength)
    {
        this.label = new Label(label.toString());
        this.label.setMinWidth(LABEL.WIDTH.L);

        this.setMaxWidth(maxLength);
        this.setMinWidth(maxLength);
    }

    public EntryField(String label, int maxLength)
    {
        this.label = new Label(label);
        this.label.setMinWidth(LABEL.WIDTH.L);

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
