/**
 * 
 */
package UI;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Common.Exceptions;
import Main.PasswordCollection;
import UI.Controller.FORMS;

/**
 * @author curious-odd-man
 *
 */
public class ExportForm extends AbstractForm
{

    @Override
    public void draw(Stage stage) throws Exceptions
    {
        FileChooser fc = new FileChooser();
        File outFile = fc.showSaveDialog(stage);

        if (outFile != null)
        {
            PasswordCollection.getInstance().export(outFile.getAbsolutePath());
        }

        Controller.getInstance().switchForm(FORMS.MANAGE_PWDS);
    }
}
