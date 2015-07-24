package ui.elements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
            public static final int M = 30;
        }
    }

    // Magic number don't lose it!
    protected final int LABEL_LENGTH_COEFICIENT = 8;

    protected final Font FONT_PRIMARY = Font.font("Comic Sans MS", FontWeight.NORMAL, 12);
    protected final Font FONT_ERROR   = Font.font("Comic Sans MS", FontWeight.BOLD, 12);

    private void setUp()
    {
        this.beNormal();
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
}
