package ui;

import core.VaultManager;
import ui.elements.Button;
import ui.elements.EntryField;
import ui.elements.GridPane;
import ui.elements.Label;
import ui.elements.LabeledItem;
import ui.elements.EntryField.TEXTFIELD;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import languages.Local.Texts;
import main.Exceptions;
import main.Terminator;

public class FormDeletePwd extends AbstractForm
{
    private Button     b_confirm        = null;
    private Label      l_note           = null;
    private Label      l_header         = null;
    private Label      l_deleteWord     = null;
    private TextField  tf_confirmation  = null;
    private boolean    confirmed        = false;
    private EntryField ef_passwordName  = null;
    private String     confirmationText = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private ChangeListener<String> getConfirmationTFListener()
    {
        return new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (newValue.equals(confirmationText))
                {
                    b_confirm.setText(Texts.LABEL_DELETE.toString());
                    confirmed = true;
                }
                else
                {
                    b_confirm.setText(Texts.LABEL_CANCEL.toString());
                    confirmed = false;
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnConfirmBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (confirmed)
                {
                    try
                    {
                        VaultManager.getInstance().getActiveVault().removePassword(null);
                    }
                    catch (Exceptions e)
                    {
                        Terminator.terminate(e);
                    }
                }

                close();
            }
        };
    }

    /* PUBLIC ROUTINE */
    public FormDeletePwd(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.LABEL_PASSWORD, WindowPriority.ALWAYS_ON_TOP);
        stage.setResizable(false);

        confirmationText = new String(Texts.FORM_DELETEPWD_MSG_CONFIRMATION.toString());
        l_header = new Label(Texts.LABEL_PASSWORD.toString());
        l_header.beHeader();

        l_note = new Label(Texts.FORM_DELETEPWD_MSG_NOTE.toString());
        l_deleteWord = new Label(confirmationText);

        l_deleteWord.setTextAlignment(TextAlignment.CENTER);
        l_note.setTextAlignment(TextAlignment.CENTER);

        l_note.beError();
        l_deleteWord.beError();

        b_confirm = new Button(Texts.LABEL_CANCEL.toString(), Texts.LABEL_PASSWORD.toString());

        tf_confirmation = new TextField();
        tf_confirmation.setMaxWidth(TEXTFIELD.WIDTH.L);
        tf_confirmation.setMinWidth(TEXTFIELD.WIDTH.L);
        tf_confirmation.setMinHeight(TEXTFIELD.HEIGTH.M);
        tf_confirmation.setMaxHeight(TEXTFIELD.HEIGTH.M);
        ef_passwordName = new EntryField(Texts.LABEL_NAME, TEXTFIELD.WIDTH.XL);
        ef_passwordName.setEditable(false);

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(l_note, HPos.CENTER);
        GridPane.setHalignment(l_deleteWord, HPos.CENTER);
        GridPane.setHalignment(b_confirm, HPos.CENTER);
        GridPane.setHalignment(tf_confirmation, HPos.CENTER);
        GridPane.setHalignment(ef_passwordName, HPos.RIGHT);

        tf_confirmation.textProperty().addListener(getConfirmationTFListener());

        b_confirm.setOnAction(getOnConfirmBtnAction());

        grid.addHElement(l_header, 0, 2);
        grid.addHElement((LabeledItem) ef_passwordName);
        grid.addHElement(l_note, 0, 2);
        grid.addHElement(l_deleteWord, 0, 2);
        grid.addHElement(tf_confirmation, 0, 2);
        grid.addHElement(b_confirm, 0, 2);

        try
        {
            ef_passwordName.setText(VaultManager.getSelectedPassword().getName());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        open();
        tf_confirmation.requestFocus();
    }

    /* OVERRIDE */
    @Override
    protected void onUserMinimizeRequest()
    {
        stage.setIconified(false);
    }
}
