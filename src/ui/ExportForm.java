/**
 *
 */
package ui;

import java.io.File;

import db.PasswordCollection;
import javafx.stage.FileChooser;
import main.Exceptions;

/**
 * @author curious-odd-man
 *
 */
public class ExportForm extends AbstractForm
{

    protected ExportForm(AbstractForm parent)
    {
        super(parent);

        // TODO Auto-generated method stub
        FileChooser fc = new FileChooser();
        File outFile = fc.showSaveDialog(stage);

        if (outFile != null)
        {
            try
            {
                PasswordCollection.getInstance().export(outFile.getAbsolutePath());
            }
            catch (Exceptions e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
