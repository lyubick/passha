package ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import ui.elements.GridPane;
import languages.Texts.TextID;
import logger.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javafx.scene.control.Control;

public abstract class AbstractForm
{
    protected enum WindowPriority
    {
        NORMAL,
        ONLY_ONE_OPENED,
        ALWAYS_ON_TOP,
    };

    protected AbstractForm         parent = null;
    protected Vector<AbstractForm> childs = null;
    protected WindowPriority       priority;

    protected GridPane grid  = null;
    protected VBox     group = null;
    protected Scene    scene = null;
    protected Stage    stage = null;

    protected MenuBar mb_Main = null;

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

    // Method will create reference to this instance in parent instance
    protected void open()
    {
        if (parent != null) parent.childs.add(this);
        stage.show();

        Logger.printDebug("OPEN! Grid W: " + grid.getWidth() + " H: " + grid.getHeight());
        Logger.printDebug("OPEN! Group W: " + group.getWidth() + " H: " + group.getHeight());
        Logger.printDebug("OPEN! Scene W: " + scene.getWidth() + " H: " + scene.getHeight());
        Logger.printDebug("OPEN! Stage W: " + stage.getWidth() + " H: " + stage.getHeight());
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

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));

        grid.setGridLinesVisible(true); // TODO: make it somehow automatically

        group = new VBox();
        group.getChildren().addAll(grid);

        group.setAlignment(Pos.CENTER);
        group.setPadding(new Insets(0, 0, 0, 0));

        scene = new Scene(group);

        stage = new Stage(StageStyle.UNIFIED);
        stage.setScene(scene);

        stage.getIcons().add(new Image("resources/tray_icon.png"));
        stage.setTitle(title + " - " + TextID.COMMON_APPLICATION_NAME.toString() + " ("
                + TextID.COMMON_LABEL_VERSION.toString() + ")");
        stage.setResizable(false);

        stage.setOnCloseRequest(getOnCloseRequest());
        stage.iconifiedProperty().addListener(getIconifiedPropertyListener());
        stage.focusedProperty().addListener(getFocusedPropertyListener());

        stage.centerOnScreen();
    }

    protected void autoSize()
    {
        Map<Integer, Double> hMap = new HashMap<Integer, Double>();
        Map<Integer, Double> vMap = new HashMap<Integer, Double>();

        for (Object child : grid.getChildren())
        {
            double width = 0;
            double height = 0;

            try
            {
                Control ctrl = (Control) child;
                width = Math.max(ctrl.getMinWidth(), ctrl.getMaxWidth());
                height = Math.max(ctrl.getMinHeight(), ctrl.getMaxHeight());
            }
            catch (java.lang.ClassCastException e)
            {
                javafx.scene.layout.Pane ctrl = null;

                try
                {
                    ctrl = (javafx.scene.layout.Pane) child; // FIXME
                    width = Math.max(ctrl.getMinWidth(), ctrl.getMaxWidth());
                    height = Math.max(ctrl.getMinHeight(), ctrl.getMaxHeight());
                }
                catch (java.lang.ClassCastException e1)
                {
                    Logger.printError("Unknown Element: " + child.getClass().getName());
                    continue;
                }

            }

            int vKey = GridPane.getRowIndex((Node) child);
            int hKey = GridPane.getColumnIndex((Node) child);

            hMap.put(vKey, hMap.getOrDefault(vKey, (double) 0) + width);
            vMap.put(hKey, vMap.getOrDefault(hKey, (double) 0) + height);

            String log = "ROW: " + vKey + " COL: " + hKey + " EL: "
                    + child.getClass().getSimpleName() + "[" + child.getClass().getName() + "]"
                    + " W: " + width + " H: " + height;
            if (Math.min(height, width) > 0)
                Logger.printDebug(log);
            else
                Logger.printError(log);
        }

        double hMax = 0, vMax = 0;

        for (double cur : hMap.values())
            hMax = Math.max(cur, hMax);

        for (double cur : vMap.values())
            vMax = Math.max(cur, vMax);

        int rowCount = 1;
        for (int key : hMap.keySet())
            rowCount = Math.max(rowCount, key);

        int colCount = 1;
        for (int key : vMap.keySet())
            colCount = Math.max(colCount, key);

        hMax += PADDING.left + PADDING.right + GAP.H * colCount - GAP.H;
        vMax += PADDING.top + PADDING.bottom + GAP.V * rowCount - GAP.V;

        grid.setMinWidth(hMax);
        grid.setMinHeight(vMax);
        grid.setMaxWidth(hMax);
        grid.setMaxHeight(vMax);

        group.setMinWidth(grid.getMinWidth());
        group.setMinHeight(grid.getMinHeight());
        group.setMaxWidth(grid.getMaxWidth());
        group.setMaxHeight(grid.getMaxHeight());

        stage.sizeToScene();

        Logger.printDebug("Autoresize completed. Width: " + hMax + ", Height: " + vMax
                + ". Form is: " + (colCount + 1) + "x" + (rowCount + 1));
    }
}
