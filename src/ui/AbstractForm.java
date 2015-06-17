/**
 *
 */
package ui;

import ui.elements.GridPane;
import ui.elements.Label;
import main.Exceptions;
import main.Exceptions.XC;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author curious-odd-man
 *
 */
public abstract class AbstractForm
{
    private static final boolean IS_DEBUG = false; // FIXME

    protected static final int   HGAP     = 10;
    protected static final int   VGAP     = 10;

    protected final class PADDING
    {
        public static final int bottom = 10;
        public static final int top    = 10;
        public static final int right  = 10;
        public static final int left   = 10;
    };

    protected final class WINDOW
    {
        public static final int width  = 1050;
        public static final int height = 650;
    }

    protected GridPane  grid            = new GridPane();
    protected VBox      group           = new VBox();
    protected Scene     scene           = new Scene(group, WINDOW.width, WINDOW.height);

    protected MenuBar   mb_Main         = null;

    protected final int BUTTON_HEIGHT   = 30;
    protected final int BUTTON_WIDTH    = 80;
    protected final int BUTTON_X_WIDTH  = 125;

    protected final int FIELD_WIDTH_S   = 50;
    protected final int FIELD_WIDTH_N   = 100;
    protected final int FIELD_WIDTH_L   = 300;
    protected final int FIELD_WIDTH_PWD = 200;

    protected final int buttonHoldTime  = 300;

    public abstract void draw(Stage stage) throws Exceptions;

    protected AbstractForm()
    {
        grid.setGridLinesVisible(IS_DEBUG);

        group.getChildren().addAll(grid);

        grid.setHgap(HGAP);
        grid.setVgap(VGAP);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));
        grid.setAlignment(Pos.CENTER);

    }

    protected Button getButton(String name)
    {
        Button tmp = new Button(name);
        tmp.setMinWidth(BUTTON_WIDTH);
        tmp.setMinHeight(BUTTON_HEIGHT);

        if (tmp.getWidth() != BUTTON_WIDTH) tmp.setMinWidth(BUTTON_X_WIDTH);

        return tmp;
    }

    protected Label getWarningLabel(String text)
    {
        Label tmp = new Label(text);
        return tmp;
    }

    protected void setButtonShortcut(final Button btn, KeyCodeCombination cmb) throws Exceptions
    {
        if (btn.getScene() == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        btn.getScene().getAccelerators().put(cmb, new Runnable()
        {
            @Override
            public void run()
            {
                // Do it with style - show animation
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
