package org.kgbt.passha.desktop.ui.elements;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.input.KeyCodeCombination;
import javafx.util.Duration;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.Button.BUTTON.SIZE;

public class Button extends javafx.scene.control.Button
{
    public static final class BUTTON
    {
        public static final class WIDTH
        {
            public final static int RESERVE = 20;
            public final static int S       = 40;
            public final static int M       = 80;
            public final static int L       = 125;
        }

        public static final class HEIGHT
        {
            public final static int S = 20;
            public final static int M = 30;
        }

        public enum SIZE
        {
            S,
            M,
            L
        }

        public static final int holdTime = 300;
    }

    public Button(String name)
    {
        this(name, BUTTON.WIDTH.M, BUTTON.HEIGHT.M);
    }

    public Button(Texts name)
    {
        this(name.toString());
    }

    public Button(String name, Node graphic)
    {
        super(name, graphic);
        this.setMinSize(27, 24);
        this.setMaxSize(27, 24);
    }

    public Button(String name, SIZE size)
    {
        super(name);

        double height = 0, width = 0;

        switch (size)
        {
            case S:
                width = BUTTON.WIDTH.S;
                height = BUTTON.HEIGHT.S;
            break;
            case M:
                width = BUTTON.WIDTH.M;
                height = BUTTON.HEIGHT.M;
            break;
            case L:
                width = BUTTON.WIDTH.L;
                height = BUTTON.HEIGHT.M;
            default:
            break;
        }

        setMinWidth(width);
        setMinHeight(height);
        setMaxWidth(width);
        setMaxHeight(height);
    }

    public Button(String name, double width, double height)
    {
        super(name);

        setMinWidth(width);
        setMinHeight(height);
        setMaxWidth(width);
        setMaxHeight(height);
    }

    public Button(String name, String... names)
    {
        this(name, Math.max(Label.calcLength(name), Label.calcMaxLength(names)) + BUTTON.WIDTH.RESERVE,
            BUTTON.HEIGHT.M);
    }

    public static void setButtonShortcut(final Button btn, KeyCodeCombination cmb) throws Exceptions
    {
        if (btn.getScene() == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        btn.getScene().getAccelerators().put(cmb, () ->
        {
            // Do it with style - show animation
            btn.arm();
            PauseTransition pt = new PauseTransition(Duration.millis(BUTTON.holdTime));
            pt.setOnFinished(event ->
            {
                btn.fire();
                btn.disarm();
            });
            pt.play();
        });
    }
}
