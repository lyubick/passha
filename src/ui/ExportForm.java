/**
 *
 */
package ui;

import java.io.File;

import main.Exceptions;
import db.PasswordCollection;
import javafx.stage.FileChooser;

/**
 * @author curious-odd-man
 *
 */
public class ExportForm extends AbstractForm
{

    protected ExportForm(AbstractForm parent)
    {
        super(parent);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onClose() throws Exceptions
    {
        // TODO Auto-generated method stub
        close();
    }

    @Override
    public void close() throws Exceptions
    {
        // TODO Auto-generated method stub
        stage.hide();
    }

    @Override
    public void open() throws Exceptions
    {
        // TODO Auto-generated method stub
        FileChooser fc = new FileChooser();
        File outFile = fc.showSaveDialog(stage);

        if (outFile != null)
        {
            PasswordCollection.getInstance().export(outFile.getAbsolutePath());
        }

        close();
    }
}
