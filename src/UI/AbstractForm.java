/**
 *
 */
package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Common.Exceptions;
import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public abstract class AbstractForm
{
    protected Controller ctrl = null;

    protected static final int HGAP = 10;
    protected static final int VGAP = 10;

    protected final class PADDING
    {
        public static final int bottom = 10;
        public static final int top    = 10;
        public static final int right  = 10;
        public static final int left   = 10;
    };

    protected final class WINDOW
    {
        public static final int width  = 500;
        public static final int height = 400;
    }

    // TODO see http://docs.oracle.com/javafx/2/layout/builtin_layouts.htm for
    // layouts description and use as
    protected GridPane grid  = new GridPane();
    protected Scene    scene = new Scene(grid, WINDOW.width, WINDOW.height);

    public abstract void draw(Stage stage) throws Exceptions;

    protected AbstractForm()
    {
        //if (Logger.getLogLevel().equals(Logger.LOGLEVELS.DEBUG)) grid.setGridLinesVisible(true);

        grid.setHgap(HGAP);
        grid.setVgap(VGAP);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));
        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(WINDOW.width - PADDING.left - PADDING.right, WINDOW.height - PADDING.top - PADDING.bottom);
        grid.setMinSize(WINDOW.width - PADDING.left - PADDING.right, WINDOW.height - PADDING.top - PADDING.bottom);
        grid.setPrefSize(WINDOW.width - PADDING.left - PADDING.right, WINDOW.height - PADDING.top - PADDING.bottom);

        try
        {
            ctrl = Controller.getInstance();
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
