package ui;

import db.PasswordCollection;
import ui.elements.Button;
import ui.elements.EntryField;
import ui.elements.GridPane;
import ui.elements.Label;
import ui.elements.EntryField.TEXTFIELD;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;

public class FormDeletePwd extends AbstractForm
{
    private Button     b_Confirm        = null;
    private Label      l_note           = null;
    private Label      l_Header         = null;
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
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                if (newValue.equals(confirmationText))
                {
                    b_Confirm.setText(TextID.FORM_DELETEPWD_NAME.toString());
                    confirmed = true;
                }
                else
                {
                    b_Confirm.setText(TextID.COMMON_LABEL_CANCEL.toString());
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
                        PasswordCollection.getInstance().removePassword(null);
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
    public FormDeletePwd(AbstractForm parent)
    {
        super(parent, TextID.FORM_DELETEPWD_NAME.toString());
        priority = WindowPriority.ALWAYS_ON_TOP;

        confirmationText = new String("DELETE");
        l_Header = new Label(TextID.FORM_DELETEPWD_NAME.toString());
        l_note = new Label(TextID.FORM_DELETEPWD_MSG_NOTE.toString() + "\n" + confirmationText,
                TEXTFIELD.WIDTH.XL);

        l_note.setTextAlignment(TextAlignment.CENTER); // FIXME
        GridPane.setHalignment(l_note, HPos.CENTER);

        b_Confirm = new Button(TextID.COMMON_LABEL_CANCEL.toString());
        l_note.beError();
        tf_confirmation = new TextField();
        tf_confirmation.setMaxWidth(TEXTFIELD.WIDTH.XL);
        tf_confirmation.setMinWidth(TEXTFIELD.WIDTH.XL);
        tf_confirmation.setMinHeight(TEXTFIELD.HEIGTH.M);
        tf_confirmation.setMaxHeight(TEXTFIELD.HEIGTH.M);
        ef_passwordName = new EntryField(TextID.FORM_CREATEPWD_LABEL_NAME, TEXTFIELD.WIDTH.L);
        ef_passwordName.setEditable(false);

        GridPane.setHalignment(l_Header, HPos.CENTER);
        GridPane.setHalignment(l_note, HPos.CENTER);
        GridPane.setHalignment(b_Confirm, HPos.CENTER);
        GridPane.setHalignment(tf_confirmation, HPos.CENTER);

        tf_confirmation.textProperty().addListener(getConfirmationTFListener());

        b_Confirm.setOnAction(getOnConfirmBtnAction());

        grid.addColumn(0, l_Header, ef_passwordName.getHBoxed(), l_note, tf_confirmation,
                b_Confirm);

        try
        {
            ef_passwordName.setText(PasswordCollection.getInstance().getSelected().getName());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        autoSize();
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
