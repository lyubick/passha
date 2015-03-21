/**
 *
 */
package UI;

import java.util.HashMap;
import java.util.Map;

import Common.Exceptions;
import Common.Exceptions.XC;
import Common.Return;
import Common.Return.RC;
import Logger.Logger;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public final class Controller
{
    private static Stage      mainStage   = null;
    private static Controller self        = null;

    private static FORMS      currentForm = FORMS.UNKNOWN;

    public enum FORMS
    {
        LOGIN,
        MANAGE_PWDS,
        CREATE_PWD,

        END,

        UNKNOWN,
    }

    static AbstractForm[] forms = null;

    // static Map<FORMS, AbstractForm> forms = new HashMap<FORMS,
    // AbstractForm>();

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
        if (self == null)
            self = new Controller(primaryStage);
        else
            System.exit(RC.ABEND.ordinal());

        forms = new AbstractForm[FORMS.END.ordinal()];

        forms[FORMS.LOGIN.ordinal()] = new LoginForm();
        forms[FORMS.MANAGE_PWDS.ordinal()] = new ManagePasswordsForm();
        forms[FORMS.CREATE_PWD.ordinal()] = new SpecialPasswordForm();

        return self;
    }

    public void switchForm(FORMS form) throws Exceptions
    {
            Logger.printDebug("Controller performs switch: from " + currentForm.name() + " to "
                    + form.name());

        if (form != FORMS.END && form != FORMS.UNKNOWN) forms[form.ordinal()].draw(mainStage);
    }
}
