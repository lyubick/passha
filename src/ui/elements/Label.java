package ui.elements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import languages.Texts.TextID;
import logger.Logger;
import ui.AbstractForm.STANDARD;

public class Label extends javafx.scene.control.Label
{
    // Magic number don't lose it!
    protected final static int LABEL_LENGTH_COEFICIENT = 8;

    protected static final Font FONT_PRIMARY = Font.font("Comic Sans MS", FontWeight.NORMAL, 12);
    protected static final Font FONT_ERROR   = Font.font("Comic Sans MS", FontWeight.BOLD, 12);

    private void setUp()
    {
        this.beNormal();

        if (!this.getText().isEmpty())
            this.setWidth(calcLength(this.getText()));
        else
            this.setWidth(STANDARD.SIZE.WIDTH);

        this.setHeight(STANDARD.SIZE.HEIGHT);

        Label tmp = this;

        this.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                tmp.setWidth(calcLength(newValue));
            }
        });
    }

    public Label()
    {
        super();
        this.setUp();
    }

    public Label(TextID name)
    {
        this(name.toString());
    }

    public Label(String name)
    {
        super(name);
        this.setUp();
    }

    public Label(String name, int wrapped)
    {
        super(name);
        this.setUp();

        this.setWidth(wrapped);
        this.setHeight(Math.ceil(calcLength(name) / wrapped) * STANDARD.SIZE.HEIGHT);

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
        Text tmp = new Text(name);
        tmp.setFont(FONT_PRIMARY);

        Logger.printDebug("CALCUALTING LENGTH: " + tmp.getLayoutBounds().getWidth());

        return tmp.getLayoutBounds().getWidth();
    }

    @Override
    public void setWidth(double width)
    {
        super.setWidth(width);
        setMinWidth(width);
        setMaxWidth(width);
    }

    @Override
    public void setHeight(double height)
    {
        super.setHeight(height);
        setMinHeight(height);
        setMaxHeight(height);
    }
}
