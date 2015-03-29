/**
 *
 */
package UI;

import Common.Exceptions;
import Common.Exceptions.XC;
import Logger.Logger;
import Main.ABEND;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author curious-odd-man
 *
 */
public final class Controller
{
    private static Stage      mainStage   = null;
    private static Controller self        = null;

    private FORMS             currentForm = FORMS.UNKNOWN;

    public enum FORMS
    {
        LOGIN,
        MANAGE_PWDS,
        CREATE_PWD,

        END,

        UNKNOWN,
    }

    static AbstractForm[] forms = null;

    public static Controller getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.NO_INSTANCE_EXISTS);

        return self;
    }

    private Controller(Stage primaryStage)
    {
        mainStage = primaryStage;

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                System.exit(0); // todo
            }
        });

    }

    public static Controller init(Stage primaryStage)
    {
        if (self == null)
            self = new Controller(primaryStage);
        else
            ABEND.terminate(new Exceptions(XC.INSTANCE_ALREADY_EXISTS));

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
        currentForm = form;
    }
}
