package org.kgbt.passha.desktop.ui;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.desktop.Main;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.GridPane;

import java.util.Optional;
import java.util.Vector;

public abstract class AbstractForm
{
    protected enum WindowPriority
    {
        NORMAL,
        ONLY_ONE_OPENED,
        ALWAYS_ON_TOP,
    }

    protected AbstractForm         parent   = null;
    protected Vector<AbstractForm> children = null;
    protected WindowPriority       priority = null;

    protected static class Coords
    {
        private static double x;
        private static double y;

        // TODO: Maybe somehow it could be taken based on some logic
        public static final double minX = -10.0;
        public static final double minY = -10.0;

        public static void setX(double x) {Coords.x = Math.max(x, minX);}
        public static void setY(double y) {Coords.y = Math.max(y, minY);}

        public static void recall(Stage stage)
        {
            stage.setX(Math.max(x, minX));
            stage.setY(Math.max(y, minY));
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
        if (parent != null) parent.children.add(this);
        stage.show();
    }

    // Method will delete references to this from parent instance
    protected void close()
    {
        if (parent != null) parent.children.remove(this);
        if (children != null) children.forEach(AbstractForm::close);
        stage.close();
        if (parent != null) parent.stage.requestFocus();
    }

    public void restore()
    {
        stage.show();
        stage.setIconified(false);
        stage.requestFocus();
        Coords.recall(stage);
        if (children != null) children.forEach(AbstractForm::restore);
    }

    public void minimize()
    {
        if (children != null) children.forEach(AbstractForm::minimize);
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

    private ChangeListener<Boolean> getIconifiedPropertyListener()
    {
        return (observable, oldValue, newValue) -> {
            if (newValue) onUserMinimizeRequest();
            stage.setIconified(false);
        };
    }

    private ChangeListener<Boolean> getFocusedPropertyListener()
    {
        return (observable, oldValue, newValue) -> {
            if (!newValue && priority == WindowPriority.NORMAL) return;

            if (newValue && priority == WindowPriority.ALWAYS_ON_TOP) return;

            {
                // Check if we have Children with highest priorities
                Optional<AbstractForm> childOnTop = children.stream()
                    .filter(child -> child.priority.equals(WindowPriority.ALWAYS_ON_TOP)).limit(1).findAny();
                if (childOnTop.isPresent())
                {
                    childOnTop.get().stage.requestFocus();
                    return;
                }
            }

            // No, we have no children with higher priority. Maybe brothers?
            if ((parent != null) && (!parent.children.isEmpty()))
            {
                Optional<AbstractForm> childOnTop = parent.children.stream()
                    .filter(child -> child.priority.equals(WindowPriority.ALWAYS_ON_TOP)).limit(1).findAny();
                childOnTop.ifPresent(abstractForm -> abstractForm.stage.requestFocus());
            }

            // No! Let focus! :)
        };
    }

    protected AbstractForm(AbstractForm parent, Texts titleTextId, WindowPriority priority, boolean resizable)
        throws Exceptions
    {
        this.parent = parent;
        this.priority = priority;

        String title = titleTextId.toString();

        if (this.priority == WindowPriority.ONLY_ONE_OPENED && this.parent != null)
        {
            for (AbstractForm brother : this.parent.children)
            {
                if (brother.getClass().getName().equals(this.getClass().getName()))
                {
                    brother.stage.requestFocus();
                    throw new Exceptions(XC.FORM_ALREADY_OPEN);
                }
            }
        }

        // Initialize everything to avoid NullPointerExceptions
        children = new Vector<>();

        grid = new GridPane();

        grid.setHgap(Properties.GUI.GAP.H);
        grid.setVgap(Properties.GUI.GAP.V);

        // grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(Properties.GUI.PADDING.top, Properties.GUI.PADDING.right,
            Properties.GUI.PADDING.bottom, Properties.GUI.PADDING.left));

        grid.setGridLinesVisible(Main.DEBUG);

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

        stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);

        stage.getIcons().add(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream(Properties.PATHS.TRAY_ICON)));
        stage.setTitle(title + " - " + Properties.SOFTWARE.NAME + " (" + Texts.LABEL_VERSION.toString() + ")");
        stage.setResizable(resizable);

        stage.setOnCloseRequest(event -> onUserCloseRequest());
        stage.iconifiedProperty().addListener(getIconifiedPropertyListener());
        stage.focusedProperty().addListener(getFocusedPropertyListener());

        stage.centerOnScreen();

        // Record coordinates on change to restore after minimize() or close()
        stage.xProperty().addListener((a, oldX, newX) -> {
            if (newX.doubleValue() > Coords.minX) Coords.setX(newX.doubleValue());
        });
        stage.yProperty().addListener((a, oldY, newY) -> {
            if (newY.doubleValue() > Coords.minY) Coords.setY(newY.doubleValue());
        });
    }
}