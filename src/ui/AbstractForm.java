/**
 *
 */
package ui;

import java.util.Vector;

import ui.elements.GridPane;
import logger.Logger;
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
    protected enum ShowPriority
    {
        LOW,
        NORMAL,
        ABOVE,
        ALWAYS,
    };

    protected AbstractForm         parent  = null;
    protected Vector<AbstractForm> childs  = null;
    protected ShowPriority         priority;      // TODO

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

    // Method that must be called when User try to close form, by pressing [X]
    public abstract void onUserCloseRequest() throws Exceptions;

    // Actual Form closing (hiding) called by Programmer wisely
    public abstract void hide() throws Exceptions;

    // Actual Form opening (showing) called by Programmer wisely
    public abstract void show() throws Exceptions;

    // Used to open new Form that depends on current form
    public void open(AbstractForm form)
    {
        if (form.priority == ShowPriority.ABOVE)
        {
            for (AbstractForm child : childs)
            {
                if (child.getClass().getName().equals(form.getClass().getName()))
                {
                    child.stage.requestFocus();
                    return;
                }
            }
        }

        childs.add(form);

        // Automatically show new Form
        try
        {
            form.show();
        }
        catch (Exceptions e)
        {
            Logger.printError("Form showing FAILED!");
            childs.remove(form);
        }
    }

    // Used to close Form that depends on current form
    public void close(AbstractForm form)
    {
        childs.remove(form);
    }

    protected AbstractForm(AbstractForm parent)
    {
        this.parent = parent;
        priority = ShowPriority.NORMAL;

        // initialize everything to avoid NullPointerExceptions
        childs = new Vector<AbstractForm>();

        grid = new GridPane();
        grid.setHgap(GAP.H);
        grid.setVgap(GAP.V);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));
        grid.setAlignment(Pos.CENTER);

        group = new VBox();
        group.getChildren().addAll(grid);

        scene = new Scene(group, WINDOW.width, WINDOW.height);

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
                    onUserCloseRequest();
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
            public void
                    changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
            {
                stage.hide();
                stage.setIconified(false);
            }
        });

        stage.focusedProperty().addListener(new ChangeListener<Boolean>()
        {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                // Check if we have Children with highest priorities
                if (!childs.isEmpty())
                {
                    for (AbstractForm child : childs)
                    {
                        if (child.priority.equals(ShowPriority.ALWAYS))
                        {
                            child.stage.requestFocus();

                            Logger.printDebug("Focus of window will be switched to "
                                    + child.getClass().getName());
                            return;
                        }
                    }
                }

                // No, we have no children with higher priority. Maybe brothers?
                if ((parent != null) && (!parent.childs.isEmpty()))
                {
                    for (AbstractForm child : parent.childs)
                    {
                        if (child.priority.equals(ShowPriority.ALWAYS))
                        {
                            child.stage.requestFocus();

                            Logger.printDebug("Focus of window will be switched to "
                                    + child.getClass().getName());
                            return;
                        }
                    }
                }

                // No! Let focus! :)
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
