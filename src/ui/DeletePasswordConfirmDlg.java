package ui;

import db.PasswordCollection;
import ui.elements.EntryField;
import ui.elements.GridPane;
import ui.elements.Label;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;

public class DeletePasswordConfirmDlg extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 350;
        public static final int height = 250;
    }

    private Button     b_Confirm        = null;
    private Label      l_note           = null;
    private Label      l_Header         = null;
    private TextField  tf_confirmation  = null;
    private boolean    confirmed        = false;
    private EntryField ef_passwordName  = null;

    private String     confirmationText = null;

    public DeletePasswordConfirmDlg(AbstractForm parent)
    {
        super(parent);

        confirmationText = new String("DELETE");
        l_Header = new Label(TextID.FORM_CONFIRM_DELETE_HEADER.toString());
        l_note = new Label(TextID.FORM_CONFIRM_DELETE_NOTE.toString() + "\n" + confirmationText, WINDOW.width - 100);
        l_note.setTextAlignment(TextAlignment.CENTER);
        b_Confirm = getButton(TextID.COMMON_LABEL_CANCEL.toString());
        l_note.beError();
        tf_confirmation = new TextField();
        tf_confirmation.setMaxWidth(WINDOW.width - 100);
        ef_passwordName = new EntryField(TextID.FORM_SP_LABEL_NAME.toString(), WINDOW.width - 200);
        ef_passwordName.setEditable(false);

        GridPane.setHalignment(l_Header, HPos.CENTER);
        GridPane.setHalignment(l_note, HPos.CENTER);
        GridPane.setHalignment(b_Confirm, HPos.CENTER);
        GridPane.setHalignment(tf_confirmation, HPos.CENTER);

        tf_confirmation.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (newValue.equals(confirmationText))
                {
                    b_Confirm.setText(TextID.FORM_CONFIRM_DELETE_HEADER.toString());
                    confirmed = true;
                }
                else
                {
                    b_Confirm.setText(TextID.COMMON_LABEL_CANCEL.toString());
                    confirmed = false;
                }
            }
        });

        b_Confirm.setOnAction(new EventHandler<ActionEvent>()
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

                try
                {
                    hide();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        grid.addColumn(0, l_Header, ef_passwordName.getHBoxed(), l_note, tf_confirmation, b_Confirm);
    }

    @Override
    public void onUserCloseRequest() throws Exceptions
    {
        // TODO Auto-generated method stub
        hide();
    }

    @Override
    public void hide() throws Exceptions
    {
        // TODO Auto-generated method stub
        stage.hide();
    }

    @Override
    public void show() throws Exceptions
    {
        // TODO Auto-generated method stub
        tf_confirmation.clear();
        tf_confirmation.requestFocus();
        try
        {
            ef_passwordName.setText(PasswordCollection.getInstance().getSelected().getName());
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        stage.setTitle(TextID.COMMON_LABEL_APP_NAME.toString() + " " + TextID.COMMON_LABEL_VERSION.toString());

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.show();
    }
}
