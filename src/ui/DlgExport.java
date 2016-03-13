package ui;

import java.io.File;

import core.VaultManager;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Exceptions;
import main.Terminator;

public class DlgExport
{

    public DlgExport(AbstractForm parent)
    {
        FileChooser fc = new FileChooser();
        File outFile = fc.showSaveDialog(new Stage(StageStyle.UNIFIED));

        if (outFile != null)
        {
            try
            {
                VaultManager.getInstance().getActiveVault().export(outFile.getAbsolutePath());
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }
        }
    }
}
