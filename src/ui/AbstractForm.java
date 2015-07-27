package ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ui.elements.GridPane;
import utilities.BiHashMap;
import utilities.Cell;
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

    public static final class STANDARD
    {
        public static final class SIZE
        {
            public static final double WIDTH  = 300.0;
            public static final double HEIGHT = 30.0;
        }
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
        BiHashMap<Integer, Integer, Cell> cells = new BiHashMap<Integer, Integer, Cell>();

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
            Integer vSpan = GridPane.getRowSpan((Node) child);
            Integer hSpan = GridPane.getColumnSpan((Node) child);

            if (vSpan == null)
            {
                vSpan = 1;
            }

            if (hSpan == null)
            {
                hSpan = 1;
            }

            height = (height - (vSpan - 1) * GAP.V) / vSpan;
            width = (width - (hSpan - 1) * GAP.H) / hSpan;

            Cell cell = new Cell(width, height);
            Cell dfltCell = new Cell(0, 0);

            for (int i = vKey; i < vKey + vSpan; i++)
            {
                for (int j = hKey; j < hKey + hSpan; j++)
                {
                    cells.put(j, i, cell.grow(cells.getOrDefault(j, i, dfltCell)));
                }
            }

            String log =
                    "ROW: " + vKey + " COL: " + hKey + " EL: " + child.getClass().getSimpleName()
                            + "[" + child.getClass().getName() + "]" + " W: " + width + " Wspan: "
                            + vSpan + " H: " + height + " Hspan: " + hSpan;
            if (Math.min(height, width) > 0)
                Logger.printDebug(log);
            else
                Logger.printError(log);
        }

        double hMax = 0, vMax = 0;

        Cell mapDimensions = cells.getDimensions();
        Cell dfltCell = new Cell(0, 0);
        Map<Integer, Double> columnWidths = new HashMap<Integer, Double>();
        Map<Integer, Double> rowHights = new HashMap<Integer, Double>();

        for (int i = 0; i < mapDimensions.getHeight(); i++)
        {
            for (int j = 0; j < mapDimensions.getWidth(); j++)
            {
                columnWidths.put(
                        j,
                        Math.max(columnWidths.getOrDefault(j, (double) 0),
                                cells.getOrDefault(j, i, dfltCell).getWidth()));

                rowHights.put(
                        i,
                        Math.max(rowHights.getOrDefault(i, (double) 0),
                                cells.getOrDefault(j, i, dfltCell).getHeight()));
            }
        }

        for (double w : columnWidths.values())
        {
            hMax += w;
        }

        for (double h : rowHights.values())
        {
            vMax += h;
        }

        hMax += PADDING.left + PADDING.right + GAP.H * mapDimensions.getWidth() - GAP.H;
        vMax += PADDING.top + PADDING.bottom + GAP.V * mapDimensions.getHeight() - GAP.V;

        hMax = Math.ceil(hMax);
        vMax = Math.ceil(vMax);

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
                + ". Form is: " + mapDimensions.getWidth() + "x" + mapDimensions.getHeight());
    }
}
