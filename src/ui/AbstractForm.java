package ui;

import java.util.Vector;

import ui.elements.GridPane;
import languages.Texts.TextID;
import main.Main;
import main.Properties;

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

    protected AbstractForm         parent   = null;
    protected Vector<AbstractForm> childs   = null;
    protected WindowPriority       priority = null;

    protected static class Coords
    {
        private static double x;
        private static double y;

        public static void remember(Stage stage)
        {
            x = stage.getX();
            y = stage.getY();
        }

        public static void recall(Stage stage)
        {
            stage.setX(x);
            stage.setY(y);
        }
    }

    protected GridPane grid     = null;
    protected VBox     group    = null;
    protected Scene    scene    = null;
    protected Stage    stage    = null;

    protected MenuBar  menuMain = null;

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
        if (parent != null) parent.stage.requestFocus();
    }

    public void maximize()
    {
        stage.show();
        stage.setIconified(false);
        stage.requestFocus();
        Coords.recall(stage);
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
        Coords.remember(stage);
        stage.hide();
    }

    // Method that is called when User try to close form, by pressing [X]
    protected void onUserCloseRequest()
    {
        close(); // default
    }

    // Method that is called when User try to minimise form, by pressing [_]
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
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValule, Boolean newValue)
            {
                // Cancel minimisation to avoid loss of stage coordinates
                stage.setIconified(false);
                if (newValue) onUserMinimizeRequest();
            }
        };
    }

    private ChangeListener<Boolean> getFocusedPropertyListener()
    {
        return new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (!newValue && priority == WindowPriority.NORMAL) return;

                if (newValue && priority == WindowPriority.ALWAYS_ON_TOP) return;

                // Check if we have Children with highest priorities
                if (!childs.isEmpty())
                {
                    for (AbstractForm child : childs)
                    {
                        if (child.priority.equals(WindowPriority.ALWAYS_ON_TOP))
                        {
                            child.stage.requestFocus();
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
                            return;
                        }
                    }
                }

                // No! Let focus! :)
            }
        };
    }

    protected AbstractForm(AbstractForm parent, TextID title)
    {
        this(parent, title.toString());
    }

    protected AbstractForm(AbstractForm parent, String title)
    {
        this.parent = parent;
        priority = WindowPriority.NORMAL;

        // Initialise everything to avoid NullPointerExceptions
        childs = new Vector<AbstractForm>();

        grid = new GridPane();

        grid.setHgap(Properties.GUI.GAP.H);
        grid.setVgap(Properties.GUI.GAP.V);

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(Properties.GUI.PADDING.top, Properties.GUI.PADDING.right,
                Properties.GUI.PADDING.bottom, Properties.GUI.PADDING.left));

        grid.setGridLinesVisible(Main.DEBUG ? true : false);

        group = new VBox();
        group.getChildren().addAll(grid);

        group.setAlignment(Pos.CENTER);
        group.setPadding(new Insets(0, 0, 0, 0));

        scene = new Scene(group);

        stage = new Stage(StageStyle.UNIFIED);
        stage.setScene(scene);

        stage.getIcons().add(new Image(Properties.PATHS.TRAY_ICON));
        stage.setTitle(title + " - " + Properties.SOFTWARE.NAME + " (" + TextID.COMMON_LABEL_VERSION.toString() + ")");
        stage.setResizable(true);

        stage.setOnCloseRequest(getOnCloseRequest());
        stage.iconifiedProperty().addListener(getIconifiedPropertyListener());
        stage.focusedProperty().addListener(getFocusedPropertyListener());

        stage.centerOnScreen();
    }
}