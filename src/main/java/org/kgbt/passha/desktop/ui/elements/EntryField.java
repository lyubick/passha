package org.kgbt.passha.desktop.ui.elements;

import javafx.scene.layout.HBox;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.core.common.cfg.Properties;

public class EntryField extends javafx.scene.control.TextField implements LabeledItem
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

    public EntryField(Texts label, int maxLength)
    {
        this(label.toString(), maxLength);
        beNormal();
    }

    public EntryField(String label, int maxLength)
    {
        this.label = new Label(label);
        beNormal();

        this.label.visibleProperty().bind(this.visibleProperty());

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
}
