/**
 *
 */
package ui;

import java.util.Vector;

import ui.elements.GridPane;
import languages.Texts.TextID;
import logger.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    protected final class FIELD_WIDTH
    {
        public static final int S  = 50;
        public static final int M  = 100;
        public static final int L  = 200;
        public static final int XL = 300;
    }

    // Method will create reference to this instance in parent instance
    protected void open()
    {
        if (parent != null) parent.childs.add(this);
        stage.show();
    }

    // Method will delete references to this from parent instance
    protected void close()
    {
        if (parent != null) parent.childs.remove(this);
        if (childs != null)
        {
            for (AbstractForm child : childs)
                child.close();
        }
        stage.close();
    }

    public void maximize()
    {
        stage.show();
        stage.requestFocus();
        if (childs != null)
        {
            for (AbstractForm child : childs)
                child.maximize();
        }
    }

    public void minimize()
    {
        if (childs != null)
        {
            for (AbstractForm child : childs)
                child.minimize();
        }
        stage.hide();
        stage.setIconified(false);
    }

    // Method that is called when User try to close form, by pressing [X]
    protected void onUserCloseRequest()
    {
        close(); // default
    }

    // Method that is called when User try to minimize form, by pressing [_]
    protected void onUserMinimizeRequest()
    {
        minimize(); // default
    }

    protected AbstractForm(AbstractForm parent, String title)
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

        stage.getIcons().add(new Image("resources/tray_icon.png"));
        stage.setTitle(title + " - " + TextID.COMMON_APPLICATION_NAME.toString() + " ("
                + TextID.COMMON_LABEL_VERSION.toString() + ")");
        stage.setResizable(false);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                onUserCloseRequest();
            }
        });

        stage.iconifiedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValule, Boolean newValue)
            {
                if (newValue) onUserMinimizeRequest();
            }
        });

        stage.focusedProperty().addListener(new ChangeListener<Boolean>()
        {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                // Check if we have Children with highest priorities
                if (!childs.isEmpty())
                {
                    for (AbstractForm child : childs)
                    {
                        if (child.priority.equals(ShowPriority.ALWAYS))
                        {
                            child.stage.requestFocus();

                            Logger.printDebug("Focus of window will be switched to " + child.getClass().getName());
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

                            Logger.printDebug("Focus of window will be switched to " + child.getClass().getName());
                            return;
                        }
                    }
                }

                // No! Let focus! :)
            }
        });
    }
}
