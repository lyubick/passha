/**
 *
 */
package UI;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import Common.Exceptions;
import Common.Exceptions.XC;
import Main.ABEND;

/**
 * @author curious-odd-man
 *
 */
public abstract class AbstractForm
{
    protected Controller       ctrl = null;

    protected static final int HGAP = 10;
    protected static final int VGAP = 10;

    protected final class PADDING
    {
        public static final int bottom = 10;
        public static final int top    = 10;
        public static final int right  = 10;
        public static final int left   = 10;
    };

    protected final class WINDOW
    {
        public static final int width  = 500;
        public static final int height = 400;
    }

    protected GridPane  grid           = new GridPane();
    protected Scene     scene          = new Scene(grid, WINDOW.width, WINDOW.height);

    protected final int buttonHeight   = 30;
    protected final int buttonWidth    = 80;
    protected final int buttonXWidth   = 100;

    protected final int buttonHoldTime = 300;

    public abstract void draw(Stage stage) throws Exceptions;

    protected AbstractForm()
    {
        // if (Logger.getLogLevel().equals(Logger.LOGLEVELS.DEBUG))
        // grid.setGridLinesVisible(true);

        grid.setHgap(HGAP);
        grid.setVgap(VGAP);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));
        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(WINDOW.width - PADDING.left - PADDING.right, WINDOW.height - PADDING.top
                - PADDING.bottom);
        grid.setMinSize(WINDOW.width - PADDING.left - PADDING.right, WINDOW.height - PADDING.top
                - PADDING.bottom);
        grid.setPrefSize(WINDOW.width - PADDING.left - PADDING.right, WINDOW.height - PADDING.top
                - PADDING.bottom);

        try
        {
            ctrl = Controller.getInstance();
        }
        catch (Exceptions e)
        {
            ABEND.terminate(e);
        }

    }

    protected Button getButton(String name)
    {
        Button tmp = new Button(name);
        tmp.setMinWidth(buttonWidth);
        tmp.setMinHeight(buttonHeight);

        if (tmp.getWidth() != buttonWidth) tmp.setMinWidth(buttonXWidth);

        return tmp;
    }

    // note: this should be done AFTER buttons is added to scene, else will
    // throw
    protected void setButtonShortcut(final Button btn, KeyCodeCombination cmb) throws Exceptions
    {
        if (btn.getScene() == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);
        btn.getScene().getAccelerators().put(cmb, new Runnable()
        {
            @Override
            public void run()
            {
                // Do it with stile - show animation
                btn.arm();
                PauseTransition pt = new PauseTransition(Duration.millis(buttonHoldTime));
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
