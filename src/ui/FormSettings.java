/**
 *
 */
package ui;

import languages.Texts.TextID;
import main.Exceptions;
import main.Exceptions.XC;
import main.Settings;
import main.Terminator;
import ui.elements.EntryField;
import ui.elements.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

/**
 * @author lyubick
 *
 */
public class FormSettings extends AbstractForm
{
    private EntryField             f_Clipboard = null;

    private HBox                   hb_Language = null;
    private Label                  l_Language  = null;
    private ComboBox<String>       cb_Language = null;

    private Label                  l_Header    = null;

    private ObservableList<String> langOptions = null;

    private Button                 b_OK        = null;

    private final class WINDOW
    {
        public static final int width  = 300;
        public static final int height = 200;
    }

    public FormSettings(AbstractForm parent)
    {
        super(parent, TextID.FORM_SETTINGS_NAME.toString());

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        priority = ShowPriority.ALWAYS;

        l_Header = new Label(TextID.FORM_SETTINGS_NAME.toString());
        l_Header.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(l_Header, HPos.CENTER);

        langOptions =
                FXCollections.observableArrayList(Settings.LANGUAGE.ENGLISH.name(), Settings.LANGUAGE.RUSSIAN.name());

        l_Language = new Label(TextID.FORM_SETTINGS_LABEL_LANGUAGE.toString());
        l_Language.setMinWidth(EntryField.LABEL_WIDTH);

        cb_Language = new ComboBox<String>(langOptions);

        try
        {
            cb_Language.setValue(langOptions.get(Settings.getInstance().getLanguage()));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        hb_Language = new HBox();
        hb_Language.getChildren().addAll(l_Language, cb_Language);

        f_Clipboard =
                new EntryField(TextID.FORM_SETTINGS_LABEL_DELAY.toString() + " "
                        + TextID.COMMON_LABEL_SECONDS.toString(), FIELD_WIDTH.S);
        try
        {
            f_Clipboard.setText(Integer.toString(Settings.getInstance().getClipboardLiveTime() / 1000));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        b_OK = new Button(TextID.COMMON_LABEL_OK.toString());

        grid.add(l_Header, 0, 0);
        grid.add(f_Clipboard.getHBoxed(), 0, 1);
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
                    Settings.getInstance().setClipboardLiveTime(f_Clipboard.getText());
                    Settings.getInstance().saveSettings();

                    if (Settings.getInstance().isRestartRequired())
                        Terminator.terminate(new Exceptions(XC.RESTART));
                    else
                        close();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        open();
    }

    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
