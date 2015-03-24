/**
 *
 */
package UI;

import java.util.BitSet;

import Common.Exceptions;
import Languages.Texts.TextID;
import Logger.Logger;
import Main.ABEND;
import Main.PasswordCollection;
import Main.SpecialPassword;
import Main.SpecialPassword.ParamsMaskBits;
import UI.Controller.FORMS;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class SpecialPasswordForm extends AbstractForm
{
    private final int    LABELS_COLUMN                  = 0;
    private final int    TEXT_FIELDS_COLUMN             = 1;
    private final int    TEXT_FIELD_LENGTH_SIZE         = 40;
    private final int    ERROR_TEXT_LINE                = 7;
    private final int    TEXT_FIELDS_WIDTH              = 300;
    private final String DEFAULT_LENGTH                 = "16";
    private final String MIN_PASSWORD_LENGTH            = "8";
    private final String MAX_PASSWORD_LENGTH            = "64";
    private final String SPECIAL_CHARACTERS_DEFAULT_SET = "` ~!@#$%^&*()_-+={}[]\\|:;\"\'<>,.?/";

    private final Label  l_errorLabel                   = new Label("");

    private TextField    tf_name                        = null;
    private TextField    tf_comment                     = null;
    private TextField    tf_url                         = null;
    private TextField    tf_length                      = null;
    private TextField    tf_specialChars                = null;
    private Button       b_OK                           = null;
    private Button       b_cancel                       = null;
    private Label        l_name                         = null;
    private Label        l_comment                      = null;
    private Label        l_url                          = null;
    private Label        lLength                        = null;
    private Label        lSpecialChars                  = null;
    private CheckBox     cb_specialChars                = null;
    private CheckBox     cb_upperCaseChar               = null;
    private HBox         buttonsBox                     = null;

    private EventHandler<KeyEvent> numFilter()
    {
        EventHandler<KeyEvent> aux = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                String lengthFieldText = tf_length.getText();
                if (!"0123456789".contains(keyEvent.getCharacter())
                        || lengthFieldText.length() >= 2)
                {
                    keyEvent.consume();
                }
            }
        };
        return aux;
    }

    private EventHandler<KeyEvent> specialCharactersFieldFilter()
    {
        EventHandler<KeyEvent> aux = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                if (!SPECIAL_CHARACTERS_DEFAULT_SET.contains(keyEvent.getCharacter())
                        || tf_specialChars.getText().contains(keyEvent.getCharacter()))
                {
                    keyEvent.consume();
                }
            }
        };
        return aux;
    }

    // TODO add text field for special characters and show check box too
    public SpecialPasswordForm()
    {
        int currentGridLine = 0;

        // ========== BUTTONS ========== //

        b_OK = getButton(TextID.CREATE.toString());
        b_cancel = getButton(TextID.CANCEL.toString());

        buttonsBox = new HBox();

        buttonsBox.setSpacing(HGAP);
        buttonsBox.setAlignment(Pos.BASELINE_CENTER);
        buttonsBox.getChildren().addAll(b_OK, b_cancel);

        HBox.setHgrow(b_OK, Priority.ALWAYS);
        HBox.setHgrow(b_cancel, Priority.ALWAYS);
        b_OK.setMaxWidth(Double.MAX_VALUE);
        b_cancel.setMaxWidth(Double.MAX_VALUE);

        // ========== LABELS ========== //

        l_name = new Label(TextID.NAME.toString());
        l_comment = new Label(TextID.COMMENT.toString());
        l_url = new Label(TextID.URL.toString());
        lLength = new Label(TextID.LENGTH.toString());
        lSpecialChars = new Label(TextID.SPECIAL_CHARACTERS.toString());

        // ========== CHECK BOXES ========== //

        cb_specialChars = new CheckBox(TextID.MUST_CONTAINT_SPECIAL_CHARS.toString());
        cb_upperCaseChar = new CheckBox(TextID.MUST_CONTAIN_UPPER_CASE_CHAR.toString());
        cb_specialChars.setSelected(true);
        cb_upperCaseChar.setSelected(true);

        // ========== TEXTS ========== //

        tf_name = new TextField();
        tf_comment = new TextField();
        tf_url = new TextField();
        tf_length = new TextField();
        tf_specialChars = new TextField();

        tf_length.setMaxWidth(TEXT_FIELD_LENGTH_SIZE);
        tf_name.setMaxWidth(TEXT_FIELDS_WIDTH);
        tf_name.setMinWidth(TEXT_FIELDS_WIDTH);
        tf_specialChars.setText(SPECIAL_CHARACTERS_DEFAULT_SET);
        tf_specialChars.addEventFilter(KeyEvent.KEY_TYPED, specialCharactersFieldFilter());
        tf_length.addEventFilter(KeyEvent.KEY_TYPED, numFilter());

        // ========== GRID ========== //

        grid.add(l_name, LABELS_COLUMN, currentGridLine);
        grid.add(tf_name, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(l_comment, LABELS_COLUMN, currentGridLine);
        grid.add(tf_comment, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(l_url, LABELS_COLUMN, currentGridLine);
        grid.add(tf_url, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(lLength, LABELS_COLUMN, currentGridLine);
        grid.add(tf_length, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(cb_upperCaseChar, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(cb_specialChars, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(lSpecialChars, LABELS_COLUMN, currentGridLine);
        grid.add(tf_specialChars, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(l_errorLabel, TEXT_FIELDS_COLUMN, ERROR_TEXT_LINE);

        grid.add(buttonsBox, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        // ========== LISTENERS ========== //

        tf_length.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                if (newValue)
                    tf_length.setText("");
                else
                {
                    if (tf_length.getText().length() == 0) tf_length.setText(DEFAULT_LENGTH);
                    if (Integer.parseInt(tf_length.getText()) > 64)
                        tf_length.setText(MAX_PASSWORD_LENGTH);

                    if (Integer.parseInt(tf_length.getText()) < 8)
                        tf_length.setText(MIN_PASSWORD_LENGTH);
                }

            }
        });

        b_OK.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("Fields: name " + tf_name.getText() + "; comment "
                        + tf_comment.getText() + "; url " + tf_url.getText() + "; length"
                        + tf_length.getText());

                BitSet paramsMask = new BitSet(ParamsMaskBits.TOTAL_COUNT.ordinal());
                int passLength = Integer.parseInt(tf_length.getText());

                if (cb_specialChars.isSelected())
                {
                    paramsMask.set(ParamsMaskBits.HAS_SPECIAL_CHARACTERS.ordinal());
                }

                if (cb_upperCaseChar.isSelected())
                {
                    paramsMask.set(ParamsMaskBits.HAS_CAPITALS.ordinal());
                }

                try
                {
                    PasswordCollection.getInstance().addPassword(
                            new SpecialPassword(tf_name.getText(), tf_comment.getText(), tf_url
                                    .getText(), passLength, paramsMask, tf_specialChars.getText()));
                    ctrl.switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    l_errorLabel.setText(TextID.ERR_NAME_ALREADY_TAKEN.toString());
                }
            }
        });

        b_cancel.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                try
                {
                    ctrl.switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    ABEND.terminate(e);
                }
            }

        });

        cb_specialChars.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                tf_specialChars.setDisable(!tf_specialChars.isDisabled());
            }
        });
    }

    @Override
    public void draw(Stage stage)
    {
        l_errorLabel.setText("");

        // maybe we can somehow clean all field in a loop??
        tf_name.clear();
        tf_comment.clear();
        tf_url.clear();
        tf_length.setText(DEFAULT_LENGTH);

        stage.setScene(scene);
        stage.show();
    }
}
