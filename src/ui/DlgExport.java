package ui;

import java.io.File;
import db.PasswordCollection;
import javafx.stage.FileChooser;
import main.Exceptions;
import main.Terminator;

public class DlgExport extends AbstractForm
{

    protected DlgExport(AbstractForm parent)
    {
        super(parent, ""); // FIXME

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
                Terminator.terminate(e);
            }
        }
    }
}
