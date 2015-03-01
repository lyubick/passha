/**
 *
 */
package UI;

import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public abstract class AbstractForm
{
    public static final int     HGAP  = 10;
    public static final int     VGAP  = 10;

    public final class PADDING
    {
        public static final int bottom = 10;
        public static final int top    = 10;
        public static final int right  = 10;
        public static final int left   = 10;
    }

    public abstract void draw(Stage stage);
}
