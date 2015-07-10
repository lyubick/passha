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
        public static final int height   = 30;
        public static final int width    = 80;
        public static final int xWidth   = 125;
        public static final int holdTime = 300;
    }

    public Button(String name)
    {
        // TODO Auto-generated constructor stub
        super(name);
        setMinWidth(BUTTON.width);
        setMinHeight(BUTTON.height);
        if (getWidth() != BUTTON.width) setMinWidth(BUTTON.xWidth);
    }

    public static void setButtonShortcut(final Button btn, KeyCodeCombination cmb) throws Exceptions
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
