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
public class DlgExport extends AbstractForm
{

    protected DlgExport(AbstractForm parent)
    {
        super(parent, ""); // FIXME

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
