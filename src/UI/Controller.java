/**
 *
 */
package UI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import Common.Exceptions;
import Common.Exceptions.XC;
import Logger.Logger;
import Main.Terminator;

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
        // Forms
        LOGIN,
        MANAGE_PWDS,
        CREATE_PWD,

        // Dialogues
        CHANGE_PWD,
        EXPORT,

        SETTINGS,

        // other TODO PREV should be usefull
        END,
        CURRENT,
        UNKNOWN,
    }

    static AbstractForm[] forms = null;

    public static Controller getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    private Controller()
    {
    }

    public static Controller init(Stage primaryStage)
    {
        if (self == null)
        {
            self = new Controller();
            mainStage = primaryStage;
        }
        else
            Terminator.terminate(new Exceptions(XC.INSTANCE_ALREADY_EXISTS));

        forms = new AbstractForm[FORMS.END.ordinal()];

        forms[FORMS.LOGIN.ordinal()] = new LoginForm();
        forms[FORMS.MANAGE_PWDS.ordinal()] = new ManagePasswordsForm();
        forms[FORMS.CREATE_PWD.ordinal()] = new SpecialPasswordForm();
        forms[FORMS.CHANGE_PWD.ordinal()] = new ChangePasswordConfirmDlg();
        forms[FORMS.SETTINGS.ordinal()] = new SettingsForm();
        forms[FORMS.EXPORT.ordinal()] = new ExportForm();

        return self;
    }

    public void switchForm(FORMS form) throws Exceptions
    {
        // TODO
        mainStage.close();
        mainStage = new Stage();

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                HotKeyAgent.getInstance().unregister();
                Terminator.terminate(new Exceptions(XC.THE_END));
            }
        });

        mainStage.iconifiedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void
                    changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
            {
                // TODO Auto-generated method stub
                mainStage.hide();
                mainStage.setIconified(false);
            }
        });

        Logger.printDebug("Controller performs switch: from " + currentForm.name() + " to "
                + form.name());
        if (form == FORMS.CURRENT)
        {
            form = currentForm;
            mainStage.setIconified(false);
        }

        if (form != FORMS.END && form != FORMS.UNKNOWN) forms[form.ordinal()].draw(mainStage);
        currentForm = form;
    }
}
