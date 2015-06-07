/**
 *
 */
package ui;

import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import ui.Controller.FORMS;
import db.PasswordCollection;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author lyubick
 *
 */
public class SaveDlg extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 500;
        public static final int height = 200;
    }

    private Label       l_Message   = null;

    private Button      b_Yes       = null;
    private Button      b_No        = null;
    private Button      b_Cancel    = null;

    private HBox        hb_Answers  = null;

    private HBox        statusBar   = null;
    private ProgressBar pb_Progress = null;
    private Label       l_Progress  = null;

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

        // ========== STATUS ========== //
        pb_Progress = new ProgressBar();
        l_Progress = new Label(TextID.SAVING.toString() + ": ");

        statusBar = new HBox();
        statusBar.setAlignment(Pos.BOTTOM_RIGHT);
        statusBar.getChildren().addAll(l_Progress, pb_Progress);

        group.getChildren().add(statusBar);

        statusBar.setVisible(false);

        b_Yes.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    statusBar.setVisible(true);
                    b_Yes.setDisable(true);
                    b_No.setDisable(true);
                    b_Cancel.setDisable(true);

                    Task<Void> saveTask = PasswordCollection.getInstance().save();

                    pb_Progress.progressProperty().bind(saveTask.progressProperty());

                    saveTask.setOnSucceeded(EventHandler -> {
                        pb_Progress.progressProperty().unbind();
                        Logger.printDebug("saveTask -> successfully finished");
                        Terminator.terminate(new Exceptions(XC.END));
                    });

                    Thread reloadTaskThread = new Thread(saveTask);
                    reloadTaskThread.start();

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
                Terminator.terminate(new Exceptions(XC.END_DISCARD));
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
