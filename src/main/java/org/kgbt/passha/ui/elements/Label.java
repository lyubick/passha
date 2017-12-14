package org.kgbt.passha.ui.elements;

import java.util.Arrays;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.kgbt.passha.languages.Local.Texts;
import org.kgbt.passha.logger.Logger;
import org.kgbt.passha.main.Properties;

public class Label extends javafx.scene.control.Label
{
    // Magic number don't lose it!
    protected final static int LABEL_LENGTH_COEFICIENT = 8;

    public static final Font   FONT_PRIMARY            = Font.font("Comic Sans MS", FontWeight.NORMAL, 12);
    public static final Font   FONT_ERROR              = Font.font("Comic Sans MS", FontWeight.BOLD, 12);
    public static final Font   FONT_HEADER             = Font.font("Comic Sans MS", FontWeight.BOLD, 14);

    private void setUp()
    {
        this.beNormal();

        if (!this.getText().isEmpty())
            this.setWidth(calcLength(this.getText()));
        else
            this.setWidth(Properties.GUI.STANDARD.SIZE.WIDTH);

        this.setHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);

        Label tmp = this;

        this.textProperty().addListener((observable, oldValue, newValue) -> tmp.setWidth(calcLength(newValue)));
    }

    private void setUpWrap(String name, int maxWidth)
    {
        this.setWidth(maxWidth);
        this.setHeight(Math.ceil(calcLength(name) / maxWidth) * Properties.GUI.STANDARD.SIZE.HEIGHT);

        this.setWrapText(true);
    }

    public Label()
    {
        super();
        this.setUp();
    }

    public Label(Texts name)
    {
        this(name.toString());
    }

    public Label(String name)
    {
        super(name);
        this.setUp();
    }

    public Label(Texts name, int maxWidth)
    {
        this(name.toString(), maxWidth);
    }

    public Label(String name, int maxWidth)
    {
        super(name);
        this.setUp();
        setUpWrap(name, maxWidth);
    }

    public void wrap(int maxWidth)
    {
        setUpWrap(super.getText(), maxWidth);
    }

    public void setText(Texts text)
    {
        super.setText(text.toString());
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

    public Label beHeader()
    {
        this.setTextFill(Color.DARKBLUE);
        this.setFont(FONT_HEADER);
        this.setWidth(calcLength(this.getText(), FONT_HEADER));
        return this;
    }

    public static double calcLength(Texts name)
    {
        return calcLength(name.toString());
    }

    public static double calcLength(String name, Font font)
    {
        Text tmp = new Text(name);
        tmp.setFont(font);

        return tmp.getLayoutBounds().getWidth();
    }

    public static double calcLength(String name)
    {
        return calcLength(name, FONT_ERROR);
    }

    public static double calcMaxLength(String... names)
    {
        double maxWidth = Arrays.stream(names).mapToDouble(Label::calcLength).max().orElse(0);
        Logger.printDebug("MAX LENGTH is : " + maxWidth);
        return maxWidth;
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
