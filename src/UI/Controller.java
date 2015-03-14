/**
 *
 */
package UI;

import java.util.HashMap;
import java.util.Map;

import Common.Exceptions;
import Common.Exceptions.XC;
import Common.Return.RC;
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

    // static AbstractForm[] forms = null;

    public enum FORMS
    {
        LOGIN,
        MAN_PWD,
        NEW_PWD,
    }

    static Map<FORMS, AbstractForm> forms = new HashMap<FORMS, AbstractForm>();

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

        return self;
    }

    public void switchForm(FORMS form)
    {
        Logger.printDebug("Controller performs switch: from " + mainStage.getScene() + " to "
                + form.ordinal());

        AbstractForm existingForm = forms.get(form);

        if (existingForm == null)
        {
            AbstractForm newForm = null;

            switch (form)
            {
                case LOGIN:
                    newForm = new LoginForm();
                    break;

                case MAN_PWD:
                    newForm = new ManagePasswordsForm();
                    break;

                case NEW_PWD:
                    newForm = new SpecialPasswordForm();
                    break;

                default:
                    System.exit(500); // TODO
                    break;
            }

            forms.put(form, newForm);
            existingForm = newForm;
        }

        existingForm.draw(mainStage);
    }
}
