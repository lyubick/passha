/**
 * 
 */
package ui;

import java.io.File;

import main.Exceptions;
import ui.Controller.FORMS;
import db.PasswordCollection;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
