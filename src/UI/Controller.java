/**
 *
 */
package UI;

import Common.Exceptions;
import Common.Exceptions.XC;
import Common.RC.RCODES;
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

    public static Controller getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);

        return self;
    }

    private Controller(Stage primaryStage)
    {
        mainStage = primaryStage;
    }

    public static Controller init(Stage primaryStage)
    {
        if (self == null) self = new Controller(primaryStage);
        else System.exit(RCODES.ABEND.ordinal());

        return self;
    }

    public void switchForm(FORMS form)
    {
        Logger.printDebug("Controller performs switch: from " + mainStage.getScene() + " to "
                + form.ordinal());

        forms[form.ordinal()].draw(mainStage);
    }
}
