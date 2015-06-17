/**
 *
 */
package ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import logger.Logger;
import main.Exceptions;
import main.Settings;
import main.Terminator;
import main.Exceptions.XC;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Watcher;

/**
 * @author curious-odd-man
 *
 */
public final class Controller
{
    private Stage             mainStage    = null;
    private static Controller self         = null;

    private FORMS             currentForm  = FORMS.UNKNOWN;
    private FORMS             previousForm = FORMS.UNKNOWN;

    public enum FORMS
    {
        // Forms
        LOGIN,
        MANAGE_PWDS,
        CREATE_PWD,
        SETTINGS,

        // Dialogues
        CHANGE_PWD,
        DELETE_PWD,
        EXPORT,

        END,

        // Management
        PREVIOUS,
        CURRENT,
        UNKNOWN,
    }

    static AbstractForm[] forms = null;

    public static Controller getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    private Controller(Stage primaryStage)
    {
        mainStage = primaryStage;

        TimerTask watcher = new Watcher()
        {
            @Override
            protected void onChange()
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            switchForm(FORMS.CURRENT);
                        }
                        catch (Exceptions e)
                        {
                            Terminator.terminate(e);
                        }
                    }
                });

            }

        };

        Timer timer = new Timer();
        timer.schedule(watcher, new Date(), Settings.ENV_VARS.SINGLE_INSTANCE_CHECK_TIMEOUT);
    }

    public static Controller init(Stage primaryStage)
    {
        if (self == null)
            self = new Controller(primaryStage);
        else
            Terminator.terminate(new Exceptions(XC.INSTANCE_ALREADY_EXISTS));

        forms = new AbstractForm[FORMS.END.ordinal()];

        forms[FORMS.LOGIN.ordinal()] = new LoginForm();
        forms[FORMS.MANAGE_PWDS.ordinal()] = new ManagePasswordsForm();
        forms[FORMS.CREATE_PWD.ordinal()] = new SpecialPasswordForm();
        forms[FORMS.CHANGE_PWD.ordinal()] = new ChangePasswordConfirmDlg();
        forms[FORMS.DELETE_PWD.ordinal()] = new DeletePasswordConfirmDlg();
        forms[FORMS.SETTINGS.ordinal()] = new SettingsForm();
        forms[FORMS.EXPORT.ordinal()] = new ExportForm();

        return self;
    }

    public void switchForm(FORMS form) throws Exceptions
    {
        mainStage.close();
        mainStage = new Stage();

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                event.consume();
                mainStage.setIconified(true);
            }
        });

        mainStage.iconifiedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
            {
                mainStage.hide();
                mainStage.setIconified(false);
            }
        });

        Logger.printDebug("Controller performs switch: from " + currentForm.name() + " to " + form.name());
        if (form == FORMS.CURRENT)
        {
            form = currentForm;
            mainStage.setIconified(false);
        }

        if (form == FORMS.PREVIOUS)
        {
            form = previousForm;
        }

        if (form != FORMS.END && form != FORMS.UNKNOWN) forms[form.ordinal()].draw(mainStage);
        previousForm = currentForm;
        currentForm = form;
    }
}
