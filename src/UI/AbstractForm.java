/**
 *
 */
package UI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public abstract class AbstractForm
{
    public static final int HGAP = 10;
    public static final int VGAP = 10;

    public final class PADDING
    {
        public static final int bottom = 10;
        public static final int top    = 10;
        public static final int right  = 10;
        public static final int left   = 10;
    };

    public final class WINDOW
    {
        public static final int width  = 250;
        public static final int height = 200;
    }

    protected static GridPane grid  = new GridPane();
    protected static Scene    scene = new Scene(grid, WINDOW.width, WINDOW.height);

    public abstract void draw(Stage stage);

    public AbstractForm()
    {
        if (Logger.getLogLevel().equals(Logger.LOGLEVELS.DEBUG))
            grid.setGridLinesVisible(true);

        grid.setHgap(HGAP);
        grid.setVgap(VGAP);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));

    }

}
