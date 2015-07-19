package ui.elements;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCodeCombination;
import javafx.util.Duration;
import main.Exceptions;
import main.Exceptions.XC;

public class Button extends javafx.scene.control.Button
{
    private final class BUTTON
    {
        public final class WIDTH
        {
            public final static int S = 40;
            public final static int M = 80;
            public final static int L = 125;
        }

        public final class HEIGHT
        {
            public final static int S = 20;
            public final static int M = 30;
        }

        public static final int holdTime = 300;
    }

    public Button(String name)
    {
        this(name, false);
    }

    public Button(String name, boolean isSmall)
    {
        super(name);
        if (isSmall)
        {
            setMinWidth(BUTTON.WIDTH.S);
            setMinHeight(BUTTON.HEIGHT.S);
            if (getWidth() != BUTTON.WIDTH.S) setMinWidth(BUTTON.WIDTH.M);
        }
        else
        {
            setMinWidth(BUTTON.WIDTH.M);
            setMinHeight(BUTTON.HEIGHT.M);
            if (getWidth() != BUTTON.WIDTH.M) setMinWidth(BUTTON.WIDTH.L);
        }

    }

    public static void setButtonShortcut(final Button btn, KeyCodeCombination cmb)
            throws Exceptions
    {
        if (btn.getScene() == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        btn.getScene().getAccelerators().put(cmb, new Runnable()
        {
            @Override
            public void run()
            {
                // Do it with style - show animation
                btn.arm();
                PauseTransition pt = new PauseTransition(Duration.millis(BUTTON.holdTime));
                pt.setOnFinished(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        btn.fire();
                        btn.disarm();
                    }
                });
                pt.play();
            }
        });
    }
}
