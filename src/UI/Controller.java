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
import Main.ABEND;

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

        CHANGE_PWD,

        SETTINGS,

        END,
        CURRENT,
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
                HotKeyAgent.getInstance().unregister();
                System.exit(0); // TODO
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
        forms[FORMS.CHANGE_PWD.ordinal()] = new ChangePasswordConfirmDlg();
        forms[FORMS.SETTINGS.ordinal()] = new SettingsForm();

        return self;
    }

    public void switchForm(FORMS form) throws Exceptions
    {
        // TODO
        mainStage.close();
        //mainStage = new Stage();

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
