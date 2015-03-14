/**
 *
 */
package UI;

import Common.Exceptions;
import Common.Exceptions.XC;
import Logger.Logger;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public final class Controller
{
    private static Stage      mainStage = null;
    private static Controller self      = null;

    static AbstractForm[]     forms     = new AbstractForm[]
                                        { new LoginForm(), new ManagePasswordsForm(),
            new SpecialPasswordForm(), };

    public enum FORMS
    {
        LOGIN,
        MAN_PWD,
        NEW_PWD,
    }

    public static Controller getInstance(Stage primaryStage)
    {
        if (self == null) self = new Controller(primaryStage);

        return self;
    }

    public static Controller getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.STAGE_NOT_SET);

        return self;
    }

    private Controller(Stage primaryStage)
    {
        mainStage = primaryStage;
    }

    public void switchForm(FORMS form)
    {
        Logger.printDebug("Controller performs switch: from " + mainStage.getScene() + " to "
                + form.ordinal());

        forms[form.ordinal()].draw(mainStage);
    }
}
