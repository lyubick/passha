package ui;

import java.util.Vector;

import ui.elements.GridPane;
import languages.Local.Texts;
import main.Exceptions;
import main.Exceptions.XC;
import main.Main;
import main.Properties;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
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

    protected GridPane   grid     = null;
    protected VBox       group    = null;
    protected Scene      scene    = null;
    protected Stage      stage    = null;

    protected MenuBar    menuMain = null;

    protected AnchorPane ap_main  = null;

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

    public void restore()
    {
        stage.show();
        stage.setIconified(false);
        stage.requestFocus();
        Coords.recall(stage);
        if (childs != null)
        {
            for (AbstractForm child : childs)
                child.restore();
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

    // Method that is called when User tries to close form, by pressing [X]
    protected void onUserCloseRequest()
    {
        close(); // default
    }

    // Method that called when User tries to restore form, by double-clicking tray icon
    public void onUserRestoreRequest()
    {
        restore(); // default
    }

    // Method that is called when User tries to minimize form, by pressing [_]
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
                if (newValue) onUserMinimizeRequest();
                stage.setIconified(false);
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

    protected AbstractForm(AbstractForm parent, Texts titleTextId, WindowPriority priority) throws Exceptions
    {
        this.parent = parent;
        this.priority = priority;

        String title = titleTextId.toString();

        if (this.priority == WindowPriority.ONLY_ONE_OPENED && this.parent != null)
        {
            for (AbstractForm brother : this.parent.childs)
            {
                if (brother.getClass().getName().equals(this.getClass().getName()))
                {
                    brother.stage.requestFocus();
                    throw new Exceptions(XC.FORM_ALREADY_OPEN);
                }
            }
        }

        // Initialize everything to avoid NullPointerExceptions
        childs = new Vector<>();

        grid = new GridPane();

        grid.setHgap(Properties.GUI.GAP.H);
        grid.setVgap(Properties.GUI.GAP.V);

        // grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(Properties.GUI.PADDING.top, Properties.GUI.PADDING.right,
            Properties.GUI.PADDING.bottom, Properties.GUI.PADDING.left));

        grid.setGridLinesVisible(Main.DEBUG ? true : false);

        group = new VBox();

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints(); // NOTE: unused
        c1.setHgrow(Priority.ALWAYS);
        c2.setHgrow(Priority.SOMETIMES);

        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints(); // NOTE: unused
        r1.setVgrow(Priority.ALWAYS);
        r2.setVgrow(Priority.SOMETIMES);

        grid.getColumnConstraints().addAll(c1, c2);
        grid.getRowConstraints().addAll(r1, r2);

        group.setPadding(new Insets(0, 0, 0, 0));
        // final Background focusBackground =
        // new Background(new BackgroundFill(Color.web("#ABCDEF"), CornerRadii.EMPTY, Insets.EMPTY));
        // group.setBackground(focusBackground);

        AnchorPane.setRightAnchor(group, 0.0);
        AnchorPane.setBottomAnchor(group, 0.0);
        AnchorPane.setLeftAnchor(group, 0.0);
        AnchorPane.setTopAnchor(group, 0.0);

        AnchorPane.setRightAnchor(grid, 0.0);
        AnchorPane.setBottomAnchor(grid, 0.0);
        AnchorPane.setLeftAnchor(grid, 0.0);
        AnchorPane.setTopAnchor(grid, 0.0);

        VBox.setVgrow(grid, Priority.ALWAYS);

        ap_main = new AnchorPane();
        ap_main.getChildren().addAll(group, grid);

        scene = new Scene(ap_main);

        stage = new Stage(StageStyle.UNIFIED);
        stage.setScene(scene);

        stage.getIcons().add(new Image(Properties.PATHS.TRAY_ICON));
        stage.setTitle(title + " - " + Properties.SOFTWARE.NAME + " (" + Texts.LABEL_VERSION.toString() + ")");
        stage.setResizable(true);

        stage.setOnCloseRequest(getOnCloseRequest());
        stage.iconifiedProperty().addListener(getIconifiedPropertyListener());
        stage.focusedProperty().addListener(getFocusedPropertyListener());

        stage.centerOnScreen();
    }
}