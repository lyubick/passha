/**
 *
 */
package ui;

import languages.Texts.TextID;
import main.Exceptions;
import main.Exceptions.XC;
import main.Settings;
import main.Terminator;
import ui.Controller.FORMS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * @author lyubick
 *
 */
public class SettingsForm extends AbstractForm
{
    private HBox                   hb_Clipboard = null;

    private HBox                   hb_Language  = null;
    private Label                  l_Language   = null;
    private ComboBox<String>       cb_Language  = null;

    private Label                  l_Header     = null;

    private ObservableList<String> langOptions  = null;

    private Button                 b_OK         = null;

    private final class WINDOW
    {
        public static final int width  = 300;
        public static final int height = 200;
    }

    public SettingsForm()
    {
        l_Header = new Label(TextID.SETTINGS.toString());
        l_Header.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(l_Header, HPos.CENTER);

        // TODO
        langOptions =
                FXCollections.observableArrayList(Settings.LANGUAGE.ENGLISH.name(), Settings.LANGUAGE.RUSSIAN.name());

        l_Language = new Label(TextID.LANGUAGE.toString());
        l_Language.setMinWidth(LABEL_WIDTH);

        cb_Language = new ComboBox<String>(langOptions);

        try
        {
            cb_Language.setValue(langOptions.get(Settings.getInstance().getLanguage()));
        }
        catch (Exceptions e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        hb_Language = new HBox();
        hb_Language.getChildren().addAll(l_Language, cb_Language);

        hb_Clipboard = getTextEntry(TextID.DELAY.toString() + " " + TextID.S.toString(), FIELD_WIDTH_S);
        try
        {
            hb_Clipboard.getEntryTextField().setText(
                    Integer.toString(Settings.getInstance().getClipboardLiveTime() / 1000));
        }
        catch (Exceptions e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        b_OK = getButton(TextID.OK.toString());

        grid.add(l_Header, 0, 0);
        grid.add(hb_Clipboard, 0, 1);
        grid.add(hb_Language, 0, 2);
        grid.add(b_OK, 0, 3);

        b_OK.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {

                try
                {
                    Settings.getInstance().setLanguage(cb_Language.getValue());
                    Settings.getInstance().setClipboardLiveTime(hb_Clipboard.getEntryTextField().getText());

                    Settings.getInstance().saveSettings();

                    if (Settings.getInstance().isRestartRequired())
                        Terminator.terminate(new Exceptions(XC.RESTART));
                    else
                        Controller.getInstance().switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void draw(Stage stage) throws Exceptions
    {
        stage.setScene(scene);

        stage.setTitle(TextID.PROGRAM_NAME.toString() + " " + TextID.VERSION.toString());

        stage.setResizable(false);

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.show();
    }
}
