/**
 *
 */
package ui;

import java.util.Vector;

import ui.elements.GridPane;
import main.Exceptions;
import main.Exceptions.XC;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.Terminator;

/**
 * @author curious-odd-man
 *
 */
public abstract class AbstractForm
{
    protected AbstractForm         parent  = null;
    protected Vector<AbstractForm> childs  = null;
    protected int                  priority;      // TODO

    protected GridPane             grid    = null;
    protected VBox                 group   = null;
    protected Scene                scene   = null;
    protected Stage                stage   = null;

    protected MenuBar              mb_Main = null;

    protected static final class GAP
    {
        public static final int H = 10;
        public static final int V = 10;
    };

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

    protected final class BUTTON
    {
        public static final int height   = 30;
        public static final int width    = 80;
        public static final int xWidth   = 125;
        public static final int holdTime = 300;
    }

    protected final class FIELD_WIDTH
    {
        public static final int S  = 50;
        public static final int M  = 100;
        public static final int L  = 200;
        public static final int XL = 300;
    }

    // Method that must be called when User try to close form, by pressing X
    public abstract void onClose() throws Exceptions;

    // Actual Form closing (destroying) called by Programmer wisely
    public abstract void close() throws Exceptions;

    // Actual Form opening (creation) called by Programmer wisely
    public abstract void open() throws Exceptions;

    protected AbstractForm(AbstractForm parent)
    {
        this.parent = parent;

        grid = new GridPane();
        grid.setHgap(GAP.H);
        grid.setVgap(GAP.V);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));
        grid.setAlignment(Pos.CENTER);

        group = new VBox();
        group.getChildren().addAll(grid);

        scene = new Scene(group, WINDOW.width, WINDOW.height);
        // scene.getWindow().centerOnScreen();

        stage = new Stage();
        stage.setScene(scene);

        stage.setResizable(false);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                try
                {
                    onClose();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        stage.iconifiedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
            {
                stage.hide();
                stage.setIconified(false);
            }
        });
    }

    protected static Button getButton(String name)
    {
        Button tmp = new Button(name);
        tmp.setMinWidth(BUTTON.width);
        tmp.setMinHeight(BUTTON.height);

        if (tmp.getWidth() != BUTTON.width) tmp.setMinWidth(BUTTON.xWidth);

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
