package ui.elements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import languages.Texts.TextID;

public class Label extends javafx.scene.control.Label
{
    public static final class LABEL
    {
        public static final class WIDTH
        {
            public static final int M = 100;
            public static final int L = 120;
        }

        public static final class HEIGHT
        {
            public static final int M = 30; // FIXME
        }
    }

    // Magic number don't lose it!
    protected final static int LABEL_LENGTH_COEFICIENT = 8;

    protected final Font FONT_PRIMARY = Font.font("Comic Sans MS", FontWeight.NORMAL, 12);
    protected final Font FONT_ERROR   = Font.font("Comic Sans MS", FontWeight.BOLD, 12);

    private void setUp()
    {
        this.beNormal();
        this.setMinWidth(LABEL.WIDTH.M);
        this.setMaxWidth(LABEL.WIDTH.M);
        this.setMinHeight(LABEL.HEIGHT.M);
        this.setMaxHeight(LABEL.HEIGHT.M);

        Label tmp = this;

        this.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                tmp.setMinWidth(newValue.length() * 7);
                tmp.setMaxWidth(tmp.getMinWidth());
            }
        });
    }

    public Label()
    {
        super();
        this.setUp();
    }

    public Label(String name)
    {
        super(name);
        this.setUp();

        this.setMinWidth(name.length() * LABEL_LENGTH_COEFICIENT);
        this.setMaxWidth(this.getMinWidth());
    }

    public Label(String name, int wrapped)
    {
        super(name);
        this.setUp();

        this.setMinWidth(wrapped);
        this.setMaxWidth(this.getMinWidth());

        this.setMinHeight(Math.ceil(calcLength(name) / wrapped) * (LABEL.HEIGHT.M - 10));
        this.setMaxHeight(Math.ceil(calcLength(name) / wrapped) * (LABEL.HEIGHT.M - 10));

        this.setWrapText(true);
    }

    public void beError()
    {
        this.setTextFill(Color.RED);
        this.setFont(FONT_ERROR);
    }

    public void beNormal()
    {
        this.setTextFill(Color.BLACK);
        this.setFont(FONT_PRIMARY);
    }

    public static double calcLength(TextID name)
    {
        return calcLength(name.toString());
    }

    public static double calcLength(String name)
    {
        return name.length() * LABEL_LENGTH_COEFICIENT;
    }
}
