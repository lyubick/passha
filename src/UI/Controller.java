/**
 *
 */
package UI;

import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public final class Controller
{
    static Stage mainStage = null;

    static AbstractForm[] forms = new AbstractForm[] {
            new LoginForm(),
            new ManagePasswordsForm(),
            new SpecialPasswordForm(),
    };

    public enum FORMS
    {
        LOGIN,
        MAN_PWD,
        NEW_PWD,
    }

    public Controller(Stage primaryStage)
    {
        mainStage = primaryStage;
    }

    public static void switchForm(FORMS form)
    {
        forms[form.ordinal()].draw(mainStage);
    }
}
