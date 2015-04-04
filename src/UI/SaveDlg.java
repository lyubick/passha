/**
 *
 */
package UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import Common.Exceptions;
import Common.Exceptions.XC;
import Languages.Texts.TextID;
import Logger.Logger;
import Main.PasswordCollection;
import Main.Terminator;
import UI.Controller.FORMS;

/**
 * @author lyubick
 *
 */
public class SaveDlg extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 350;
        public static final int height = 200;
    }

    private Label  l_Message  = null;

    private Button b_Yes      = null;
    private Button b_No       = null;
    private Button b_Cancel   = null;

    private HBox   hb_Answers = null;

    public SaveDlg()
    {
        l_Message = new Label(TextID.SAVE_CONFIRMATION.toString());

        b_Yes = getButton(TextID.SAVE.toString());
        b_No = getButton(TextID.DISCARD.toString());
        b_Cancel = getButton(TextID.CANCEL.toString());

        hb_Answers = new HBox();
        hb_Answers.setSpacing(10);

        hb_Answers.getChildren().addAll(b_Yes, b_No, b_Cancel);

        grid.add(l_Message, 0, 0);
        grid.add(hb_Answers, 0, 1);

        b_Yes.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    PasswordCollection.getInstance().save();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        b_No.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {
                Terminator.terminate(new Exceptions(XC.THE_END));
            }
        });

        b_Cancel.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    Controller.getInstance().switchForm(FORMS.PREVIOUS);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }

            }
        });
    }

    @Override
    public void draw(Stage stage) throws Exceptions
    {
        Logger.printDebug("SaveDlg preparing...");

        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent event)
            {
                event.consume();
            }
        });

        stage.setTitle(TextID.PROGRAM_NAME.toString());
        stage.setResizable(false);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.setScene(scene);

        Logger.printDebug("SaveDlg displaying");
        stage.show();
    }

}
