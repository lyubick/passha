/**
 *
 */
package ui;

import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import ui.Controller.FORMS;
import db.PasswordCollection;
import db.SpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class SpecialPasswordForm extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 600;
        public static final int height = 400;
    }

    private final int       LABELS_COLUMN                  = 0;
    private final int       TEXT_FIELDS_COLUMN             = 1;
    private final int       TEXT_FIELD_LENGTH_SIZE         = 40;
    private final int       TEXT_FIELDS_WIDTH              = 350;
    private final String    DEFAULT_LENGTH                 = "16";
    private final String    MIN_PASSWORD_LENGTH_TEXT       = "8";
    private final String    MAX_PASSWORD_LENGTH_TEXT       = "64";
    private final String    SPECIAL_CHARACTERS_DEFAULT_SET = "` ~!@#$%^&*()_-+={}[]\\|:;\"\'<>,.?/";
    private final int       MAX_PASSWORD_LENGTH            = 64;
    private final int       MIN_PASSWORD_LENGTH            = 8;

    private SpecialPassword password                       = null;

    private final Label     l_errorLabel                   = getWarningLabel("");

    private TextField       tf_name                        = null;
    private TextField       tf_comment                     = null;
    private TextField       tf_url                         = null;
    private TextField       tf_length                      = null;
    private TextField       tf_specialChars                = null;
    private TextField       tf_passwordPreview             = null;
    private Button          b_OK                           = null;
    private Button          b_cancel                       = null;
    private Button          b_regeneratePassword           = null;
    private Label           l_name                         = null;
    private Label           l_comment                      = null;
    private Label           l_url                          = null;
    private Label           l_Length                       = null;
    private Label           l_SpecialChars                 = null;
    private Label           l_passwordPreview              = null;
    private CheckBox        cb_specialChars                = null;
    private CheckBox        cb_upperCaseChar               = null;
    private HBox            buttonsBox                     = null;

    private EventHandler<KeyEvent> numFilter()
    {
        EventHandler<KeyEvent> aux = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                String lengthFieldText = tf_length.getText();
                if (!"0123456789".contains(keyEvent.getCharacter()) || lengthFieldText.length() >= 2)
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

    private void showPasswordPreview()
    {
        try
        {
            if (tf_length.getText().length() > 0 && Integer.parseInt(tf_length.getText()) >= MIN_PASSWORD_LENGTH
                    && Integer.parseInt(tf_length.getText()) <= MAX_PASSWORD_LENGTH)
            {
                password =
                        new SpecialPassword(tf_name.getText(), tf_comment.getText(), tf_url.getText(),
                                tf_length.getText(), cb_specialChars.isSelected(), cb_upperCaseChar.isSelected(),
                                tf_specialChars.getText());
                tf_passwordPreview.setText(password.getPassword());
                l_errorLabel.setText("");
                l_name.beNormal();
                l_SpecialChars.beNormal();
                b_OK.setDisable(false);
            }
            else
            {
                b_OK.setDisable(true);
                password = null;
            }
        }
        catch (Exceptions e)
        {
            if (e.getCode() == XC.MANDATORY_DATA_MISSING)
            {
                l_errorLabel.setText(TextID.FORM_SP_LABEL_ERROR_MISSING_PARAM.toString());

                l_name.beError();
                l_SpecialChars.beError();
            }
            b_OK.setDisable(true);
            password = null;
        }
    }

    public SpecialPasswordForm()
    {
        int currentGridLine = 0;

        // ========== BUTTONS ========== //

        b_OK = getButton(TextID.FORM_SP_LABEL_CREATE.toString());
        b_cancel = getButton(TextID.COMMON_LABEL_CANCEL.toString());
        ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream("/resources/regenerate.png")));
        imgView.setStyle("-fx-background-color:transparent");
        b_regeneratePassword = new Button("", imgView);
        b_regeneratePassword.setMaxSize(27, 24);

        buttonsBox = new HBox();

        buttonsBox.setSpacing(HGAP);
        buttonsBox.setAlignment(Pos.BASELINE_CENTER);
        buttonsBox.getChildren().addAll(b_OK, b_cancel);

        HBox.setHgrow(b_OK, Priority.ALWAYS);
        HBox.setHgrow(b_cancel, Priority.ALWAYS);
        b_OK.setMaxWidth(Double.MAX_VALUE);
        b_cancel.setMaxWidth(Double.MAX_VALUE);

        // ========== LABELS ========== //

        l_name = new Label(TextID.FORM_SP_LABEL_NAME.toString() + "*");
        l_comment = new Label(TextID.FORM_SP_LABEL_COMMENT.toString());
        l_url = new Label(TextID.FORM_SP_LABEL_URL.toString());
        l_Length = new Label(TextID.FORM_SP_LABEL_LENGTH.toString());
        l_SpecialChars = new Label(TextID.FORM_SP_LABEL_SPECIAL_CHARACTERS.toString() + "*");
        l_passwordPreview = new Label(TextID.FORM_LOGIN_LABEL_PASSWORD.toString());
        l_errorLabel.beError();

        // ========== CHECK BOXES ========== //

        cb_specialChars = new CheckBox(TextID.FORM_SP_LABEL_MUST_HAVE_SPECIAL.toString());
        cb_upperCaseChar = new CheckBox(TextID.FORM_SP_LABEL_MUST_HAVE_UPPER.toString());
        cb_specialChars.setSelected(true);
        cb_upperCaseChar.setSelected(true);

        // ========== TEXTS ========== //

        tf_name = new TextField();
        tf_comment = new TextField();
        tf_url = new TextField();
        tf_length = new TextField();
        tf_specialChars = new TextField();
        tf_passwordPreview = new TextField();

        tf_length.setMaxWidth(TEXT_FIELD_LENGTH_SIZE);
        tf_name.setMinWidth(TEXT_FIELDS_WIDTH);
        tf_specialChars.setText(SPECIAL_CHARACTERS_DEFAULT_SET);
        tf_specialChars.addEventFilter(KeyEvent.KEY_TYPED, specialCharactersFieldFilter());
        tf_length.addEventFilter(KeyEvent.KEY_TYPED, numFilter());
        tf_passwordPreview.setMinWidth(TEXT_FIELDS_WIDTH);
        tf_passwordPreview.setEditable(false);

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

        grid.add(l_Length, LABELS_COLUMN, currentGridLine);
        grid.add(tf_length, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(cb_upperCaseChar, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(cb_specialChars, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(l_SpecialChars, LABELS_COLUMN, currentGridLine);
        grid.add(tf_specialChars, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(l_passwordPreview, LABELS_COLUMN, currentGridLine);
        grid.add(b_regeneratePassword, LABELS_COLUMN, currentGridLine);
        GridPane.setHalignment(b_regeneratePassword, HPos.RIGHT);
        grid.add(tf_passwordPreview, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(buttonsBox, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        grid.add(l_errorLabel, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        // ========== LISTENERS ========== //

        tf_name.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                showPasswordPreview();
            }
        });

        tf_specialChars.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                showPasswordPreview();
            }
        });

        tf_length.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                showPasswordPreview();
            }
        });

        cb_specialChars.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                tf_specialChars.setDisable(!tf_specialChars.isDisabled());
                showPasswordPreview();
            }
        });

        cb_upperCaseChar.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                showPasswordPreview();
            }
        });

        tf_length.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue)
                {
                    tf_length.setText("");
                    b_OK.setDisable(true);
                }
                else
                {
                    b_OK.setDisable(false);
                    if (tf_length.getText().length() == 0) tf_length.setText(DEFAULT_LENGTH);
                    if (Integer.parseInt(tf_length.getText()) > MAX_PASSWORD_LENGTH)
                        tf_length.setText(MAX_PASSWORD_LENGTH_TEXT);

                    if (Integer.parseInt(tf_length.getText()) < MIN_PASSWORD_LENGTH)
                        tf_length.setText(MIN_PASSWORD_LENGTH_TEXT);
                }
            }
        });

        b_OK.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                b_OK.setDisable(true);

                try
                {
                    if (password != null)
                        PasswordCollection.getInstance().addPassword(password);
                    else
                        PasswordCollection.getInstance().addPassword(
                                new SpecialPassword(tf_name.getText(), tf_comment.getText(), tf_url.getText(),
                                        tf_length.getText(), cb_specialChars.isSelected(), cb_upperCaseChar
                                                .isSelected(), tf_specialChars.getText()));

                    Controller.getInstance().switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    b_OK.setDisable(false);
                    if (e.getCode() == XC.MANDATORY_DATA_MISSING)
                    {
                        l_errorLabel.setText(TextID.FORM_SP_LABEL_ERROR_MISSING_PARAM.toString());

                        l_name.beError();
                        l_SpecialChars.beError();

                    }
                    else if (e.getCode() == XC.PASSWORD_NAME_ALREADY_EXISTS)
                        l_errorLabel.setText(TextID.FORM_SP_LABEL_ERROR_NAME_EXISTS.toString());
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
                    Controller.getInstance().switchForm(FORMS.MANAGE_PWDS);
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        });

        b_regeneratePassword.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                showPasswordPreview();
            }
        });
    }

    @Override
    public void draw(Stage stage)
    {
        l_errorLabel.setText("");
        l_name.beNormal();
        l_SpecialChars.beNormal();
        b_OK.setDisable(false);

        // maybe we can somehow clean all field in a loop??
        tf_name.clear();
        tf_comment.clear();
        tf_url.clear();
        tf_length.setText(DEFAULT_LENGTH);
        tf_passwordPreview.clear();

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        stage.setScene(scene);
        stage.show();
    }
}
