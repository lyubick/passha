package org.kgbt.passha.desktop.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.common.cfg.Settings;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.Button;
import org.kgbt.passha.desktop.ui.elements.EntryField;
import org.kgbt.passha.desktop.ui.elements.EntryField.TEXTFIELD;
import org.kgbt.passha.desktop.ui.elements.Label;
import org.kgbt.passha.desktop.ui.elements.LabeledItem;

public class FormSettings extends AbstractForm
{
    private EntryField             ef_clipboard = null;
    private Label                  l_header     = null;
    private Button                 b_ok         = null;

    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return event -> {
            try
            {
                //Settings.getInstance().setLanguage(cb_language.getValue());
                Settings.getInstance().setClipboardLiveTime(ef_clipboard.getText());
                Settings.getInstance().saveSettings();

                close();
                if (Settings.getInstance().isRestartRequired()) FormVaultsManager.reload();

            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }
        };
    }

    public FormSettings(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.LABEL_SETTINGS, WindowPriority.ALWAYS_ON_TOP, false);

        l_header = new Label(Texts.LABEL_SETTINGS.toString());
        l_header.setTextAlignment(TextAlignment.CENTER);
        l_header.beHeader();

        ef_clipboard =
            new EntryField(Texts.LABEL_DELAY.toString() + " " + Texts.LABEL_SECONDS.toString(), TEXTFIELD.WIDTH.S);

        b_ok = new Button(Texts.LABEL_OK.toString());

        try
        {
            //cb_language.setValue(langOptions.get(Settings.getInstance().getLanguage()));
            ef_clipboard.setText(Integer.toString(Settings.getInstance().getClipboardLiveTime() / 1000));
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        GridPane.setHalignment(l_header, HPos.CENTER);

        grid.addHElement(l_header, 0, 2);
        grid.addHElement((LabeledItem) ef_clipboard);
        //grid.addHElement((LabeledItem) cb_language);
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
