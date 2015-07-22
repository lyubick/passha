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
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.Label;
import ui.elements.Label.LABEL;
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
    private EntryField ef_clipboard = null;

    private HBox             hb_language = null;
    private Label            l_language  = null;
    private ComboBox<String> cb_language = null;

    private Label l_header = null;

    private ObservableList<String> langOptions = null;

    private Button b_ok = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {

                try
                {
                    Settings.getInstance().setLanguage(cb_language.getValue());
                    Settings.getInstance().setClipboardLiveTime(ef_clipboard.getText());
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
        };
    }

    /* PUBLIC ROUTINE */
    public FormSettings(AbstractForm parent)
    {
        super(parent, TextID.FORM_SETTINGS_NAME.toString());

        priority = WindowPriority.ALWAYS_ON_TOP;

        l_header = new Label(TextID.FORM_SETTINGS_NAME.toString());
        l_header.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(l_header, HPos.CENTER);

        langOptions = FXCollections.observableArrayList(Settings.LANGUAGE.ENGLISH.name(),
                Settings.LANGUAGE.RUSSIAN.name());

        l_language = new Label(TextID.FORM_SETTINGS_LABEL_LANGUAGE.toString());
        l_language.setMinWidth(LABEL.WIDTH.M);

        cb_language = new ComboBox<String>(langOptions);

        try
        {
            cb_language.setValue(langOptions.get(Settings.getInstance().getLanguage()));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        hb_language = new HBox();
        hb_language.getChildren().addAll(l_language, cb_language);

        ef_clipboard = new EntryField(TextID.FORM_SETTINGS_LABEL_DELAY.toString() + " "
                + TextID.COMMON_LABEL_SECONDS.toString(), TEXTFIELD.WIDTH.S);
        try
        {
            ef_clipboard.setText(
                    Integer.toString(Settings.getInstance().getClipboardLiveTime() / 1000));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        b_ok = new Button(TextID.COMMON_LABEL_OK.toString());

        grid.addHElement(l_header, 0);
        grid.addHElement(ef_clipboard.getHBoxed(), 0);
        grid.addHElement(hb_language, 0);
        grid.addHElement(b_ok, 0);

        b_ok.setOnAction(getOnOKBtnAction());

        autoSize();
        open();
    }

    /* OVERRIDE */
    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
