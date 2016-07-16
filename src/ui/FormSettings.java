package ui;

import languages.Local.Texts;
import main.Exceptions;
import main.Settings;
import main.Terminator;
import ui.elements.Button;
import ui.elements.ComboBox;
import ui.elements.EntryField;
import ui.elements.LabeledItem;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

public class FormSettings extends AbstractForm
{
    private EntryField             ef_clipboard = null;
    private ComboBox               cb_language  = null;
    private CheckBox               cb_autologin = null;
    private Label                  l_header     = null;
    private Button                 b_ok         = null;

    private ObservableList<String> langOptions  = null;

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
                    Settings.getInstance().setAutologin(cb_autologin.isSelected());
                    Settings.getInstance().saveSettings();

                    close();
                    if (Settings.getInstance().isRestartRequired()) FormVaultsManager.reload();

                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        };
    }

    public FormSettings(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.LABEL_SETTINGS, WindowPriority.ALWAYS_ON_TOP);

        l_header = new Label(Texts.LABEL_SETTINGS.toString());
        l_header.setTextAlignment(TextAlignment.CENTER);
        l_header.beHeader();

        langOptions =
            FXCollections.observableArrayList(Settings.LANGUAGE.ENGLISH.name(), Settings.LANGUAGE.RUSSIAN.name());

        cb_language = new ComboBox(langOptions, Texts.LABEL_LANGUAGE, TEXTFIELD.WIDTH.M);

        cb_autologin = new CheckBox(Texts.LABEL_AUTOLOGIN.toString());

        ef_clipboard =
            new EntryField(Texts.LABEL_DELAY.toString() + " " + Texts.LABEL_SECONDS.toString(), TEXTFIELD.WIDTH.S);

        b_ok = new Button(Texts.LABEL_OK.toString());

        try
        {
            cb_autologin.setSelected(Settings.getInstance().isAutologinOn());
            cb_language.setValue(langOptions.get(Settings.getInstance().getLanguage()));
            ef_clipboard.setText(Integer.toString(Settings.getInstance().getClipboardLiveTime() / 1000));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(cb_autologin, HPos.CENTER);

        grid.addHElement(l_header, 0, 2);
        grid.addHElement((LabeledItem) ef_clipboard);
        grid.addHElement((LabeledItem) cb_language);
        grid.addHElement(cb_autologin, 0, 2);
        grid.addHElement(b_ok, 0, 2);

        b_ok.setOnAction(getOnOKBtnAction());

        open();
    }

    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
