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
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public abstract class AbstractForm
{
    protected enum WindowPriority
    {
        NORMAL,
        ONLY_ONE_OPENED,
        ALWAYS_ON_TOP,
    };

    protected AbstractForm         parent  = null;
    protected Vector<AbstractForm> childs  = null;
    protected WindowPriority       priority;

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

    protected static final class PADDING
    {
        public static final int bottom = 10;
        public static final int top    = 10;
        public static final int right  = 10;
        public static final int left   = 10;
    };

    protected static final class WINDOW
    {
        public static final int width  = 1050;
        public static final int height = 650;
    }

    protected static final class TEXTFIELD_WIDTH
    {
        public static final int XS  = 25;
        public static final int S   = 50;
        public static final int M   = 100;
        public static final int L   = 200;
        public static final int XL  = 300;
        public static final int XXL = 350;
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

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<WindowEvent> getOnCloseRequest()
    {
        return new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                onUserCloseRequest();
            }
        };
    }

    private ChangeListener<Boolean> getIconifiedPropertyListener()
    {
        return new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValule,
                    Boolean newValue)
            {
                if (newValue) onUserMinimizeRequest();
            }
        };
    }

    private ChangeListener<Boolean> getFocusedPropertyListener()
    {
        return new ChangeListener<Boolean>()
        {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                if (!newValue && priority == WindowPriority.NORMAL)
                {
                    Logger.printDebug("Lost focus. Ignore. (NORMAL)");
                    return;
                }

                if (newValue && priority == WindowPriority.ALWAYS_ON_TOP)
                {
                    Logger.printDebug("Gain focus. Ignore. (ALWAYS_ON_TOP)");
                    return;
                }

                // Check if we have Children with highest priorities
                if (!childs.isEmpty())
                {
                    for (AbstractForm child : childs)
                    {
                        if (child.priority.equals(WindowPriority.ALWAYS_ON_TOP))
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
                        if (child.priority.equals(WindowPriority.ALWAYS_ON_TOP))
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
        };
    }

    protected AbstractForm(AbstractForm parent, String title)
    {
        this.parent = parent;
        priority = WindowPriority.NORMAL;

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

        stage = new Stage(StageStyle.UNIFIED);
        stage.setScene(scene);

        stage.getIcons().add(new Image("resources/tray_icon.png"));
        stage.setTitle(title + " - " + TextID.COMMON_APPLICATION_NAME.toString() + " ("
                + TextID.COMMON_LABEL_VERSION.toString() + ")");
        stage.setResizable(false);

        stage.setOnCloseRequest(getOnCloseRequest());
        stage.iconifiedProperty().addListener(getIconifiedPropertyListener());
        stage.focusedProperty().addListener(getFocusedPropertyListener());
    }
}
