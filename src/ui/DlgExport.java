package ui;

import java.io.File;

import db.PasswordCollection;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Exceptions;
import main.Terminator;

public class DlgExport
{

    protected DlgExport(AbstractForm parent)
    {
        FileChooser fc = new FileChooser();
        File outFile = fc.showSaveDialog(new Stage(Common.STAGE_STYLE.UNIFIED));

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
