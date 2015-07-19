/**
 *
 */
package ui;

import languages.Texts.TextID;
import main.Exceptions;
import main.Exceptions.XC;
import ui.elements.EntryField;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.Label;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author curious-odd-man
 *
 */
public class FormCreatePwd extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 600;
        public static final int height = 400;
    }

    private final int       LABELS_COLUMN                  = 0;
    private final int       TEXT_FIELDS_COLUMN             = 1;

    private final String    SPECIAL_CHARACTERS_DEFAULT_SET = "` ~!@#$%^&*()_-+={}[]\\|:;\"\'<>,.?/";

    private final int       MAX_PASSWORD_LENGTH            = 64;
    private final int       MIN_PASSWORD_LENGTH            = 8;
    private final int       DEFAULT_PASSWORD_LENGTH        = 16;

    private SpecialPassword password                       = null;

    private final Label     l_errorLabel                   = new Label("");

    private Button          b_OK                           = null;
    private Button          b_cancel                       = null;
    private Button          b_regeneratePassword           = null;

    private CheckBox        cb_specialChars                = null;
    private CheckBox        cb_upperCaseChar               = null;

    private HBox            buttonsBox                     = null;

    private EntryField      ef_name                        = null;
    private EntryField      ef_comment                     = null;
    private EntryField      ef_url                         = null;
    private EntryField      ef_length                      = null;
    private EntryField      ef_specialChars                = null;
    private EntryField      ef_passwordPreview             = null;
    private EntryField      ef_shortcut                    = null;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<KeyEvent> getLengthTFFilter()
    {
        return new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                String lengthFieldText = ef_length.getText();
                if (!"0123456789".contains(keyEvent.getCharacter())
                        || lengthFieldText.length() >= 2)
                {
                    keyEvent.consume();
                }
            }
        };
    }

    private EventHandler<KeyEvent> getSpecialCharTFFilter()
    {
        return new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                if (!SPECIAL_CHARACTERS_DEFAULT_SET.contains(keyEvent.getCharacter())
                        || ef_specialChars.getText().contains(keyEvent.getCharacter()))
                {
                    keyEvent.consume();
                }
            }
        };
    }

    private ChangeListener<String> getPasswordParameterListener()
    {
        return new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                showPasswordPreview();
            }
        };
    }

    private EventHandler<ActionEvent> getOnSpecialCharactersCBChanged()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                ef_specialChars.setDisable(!ef_specialChars.isDisabled());
                showPasswordPreview();
            }
        };
    }

    private EventHandler<ActionEvent> getOnUpperCaseCBChanged()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                showPasswordPreview();
            }
        };
    }

    private ChangeListener<Boolean> getLengthTFFocusedPropertyListener()
    {
        return new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue)
            {
                if (newValue)
                {
                    ef_length.setText("");
                    b_OK.setDisable(true);
                }
                else
                {
                    b_OK.setDisable(false);
                    if (ef_length.getText().length() == 0)
                        ef_length.setText(Integer.toString(DEFAULT_PASSWORD_LENGTH));
                    if (Integer.parseInt(ef_length.getText()) > MAX_PASSWORD_LENGTH)
                        ef_length.setText(Integer.toString(MAX_PASSWORD_LENGTH));

                    if (Integer.parseInt(ef_length.getText()) < MIN_PASSWORD_LENGTH)
                        ef_length.setText(Integer.toString(MIN_PASSWORD_LENGTH));
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                b_OK.setDisable(true);

                try
                {
                    if (password != null)
                    {
                        password.setAllOptionalFields(ef_comment.getText(), ef_url.getText(),
                                ef_shortcut.getText());
                        PasswordCollection.getInstance().addPassword(password);
                    }
                    else
                        PasswordCollection.getInstance().addPassword(
                                new SpecialPassword(ef_name.getText(), ef_comment.getText(), ef_url
                                        .getText(), ef_length.getText(), cb_specialChars
                                        .isSelected(), cb_upperCaseChar.isSelected(),
                                        ef_specialChars.getText(), ef_shortcut.getText()));
                    close();
                }
                catch (Exceptions e)
                {
                    if (e.getCode() == XC.PASSWORD_NAME_ALREADY_EXISTS)
                        l_errorLabel.setText(TextID.FORM_CREATEPWD_MSG_NAME_EXISTS.toString());

                    b_OK.setDisable(false);
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnCancelBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                close();
            }
        };
    }

    private EventHandler<ActionEvent> getOnRegenerateBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                showPasswordPreview();
            }
        };
    }

    /* PRIVATE ROUTINE */
    private void showPasswordPreview()
    {
        try
        {
            if (ef_length.getText().length() > 0
                    && Integer.parseInt(ef_length.getText()) >= MIN_PASSWORD_LENGTH
                    && Integer.parseInt(ef_length.getText()) <= MAX_PASSWORD_LENGTH)
            {
                password =
                        new SpecialPassword(ef_name.getText(), ef_comment.getText(),
                                ef_url.getText(), ef_length.getText(),
                                cb_specialChars.isSelected(), cb_upperCaseChar.isSelected(),
                                ef_specialChars.getText(), ef_shortcut.getText());
                ef_passwordPreview.setText(password.getPassword());
                l_errorLabel.setText("");
                ef_name.beNormal();
                ef_length.beNormal();
                ef_specialChars.beNormal();
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
                l_errorLabel.setText(TextID.FORM_CREATEPWD_MSG_MISSING_PARAM.toString());

                ef_name.beError();
                ef_specialChars.beError();
                ef_length.beError();
            }
            b_OK.setDisable(true);
            password = null;
        }
    }

    /* PUBLIC ROUTINE */
    public FormCreatePwd(AbstractForm parent)
    {
        super(parent, TextID.FORM_CREATEPWD_NAME.toString());

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);

        priority = WindowPriority.ONLY_ONE_OPENED;
        // ========== BUTTONS ========== //

        b_OK = new Button(TextID.FORM_CREATEPWD_LABEL_CREATE.toString());
        b_cancel = new Button(TextID.COMMON_LABEL_CANCEL.toString());
        ImageView imgView =
                new ImageView(
                        new Image(getClass().getResourceAsStream("/resources/regenerate.png")));
        imgView.setStyle("-fx-background-color:transparent");
        b_regeneratePassword = new Button("", imgView);
        b_regeneratePassword.setMaxSize(27, 24);

        buttonsBox = new HBox();

        buttonsBox.setSpacing(GAP.H);
        buttonsBox.setAlignment(Pos.BASELINE_CENTER);
        buttonsBox.getChildren().addAll(b_OK, b_cancel);

        HBox.setHgrow(b_OK, Priority.ALWAYS);
        HBox.setHgrow(b_cancel, Priority.ALWAYS);
        b_OK.setMaxWidth(Double.MAX_VALUE);
        b_cancel.setMaxWidth(Double.MAX_VALUE);

        // ========== ENTRY FIELDS ========== //

        ef_name =
                new EntryField(TextID.FORM_CREATEPWD_LABEL_NAME.toString() + "*",
                        TEXTFIELD.WIDTH.XXL);
        ef_comment = new EntryField(TextID.FORM_CREATEPWD_LABEL_COMMENT, TEXTFIELD.WIDTH.XXL);
        ef_url = new EntryField(TextID.FORM_CREATEPWD_LABEL_URL, TEXTFIELD.WIDTH.XXL);
        ef_length =
                new EntryField(TextID.FORM_CREATEPWD_LABEL_LENGTH.toString() + "*",
                        TEXTFIELD.WIDTH.S);
        ef_specialChars =
                new EntryField(TextID.FORM_CREATEPWD_LABEL_SPECIAL_CHARACTERS.toString() + "*",
                        TEXTFIELD.WIDTH.XXL);
        ef_passwordPreview = new EntryField(TextID.FORM_LOGIN_LABEL_PASSWORD, TEXTFIELD.WIDTH.XXL);
        ef_shortcut = new EntryField(TextID.FORM_EDITPWD_LABEL_SHORTCUT, TEXTFIELD.WIDTH.XS);

        // ========== LABELS ========== //

        l_errorLabel.beError();

        // ========== CHECK BOXES ========== //

        cb_specialChars = new CheckBox(TextID.FORM_CREATEPWD_MSG_MUST_HAVE_SPECIAL.toString());
        cb_upperCaseChar = new CheckBox(TextID.FORM_CREATEPWD_MSG_MUST_HAVE_UPPER.toString());
        cb_specialChars.setSelected(true);
        cb_upperCaseChar.setSelected(true);

        // ========== TEXTS ========== //

        ef_specialChars.setText(SPECIAL_CHARACTERS_DEFAULT_SET);
        ef_passwordPreview.setMinWidth(TEXTFIELD.WIDTH.XL);
        ef_passwordPreview.setEditable(false);

        // ========== GRID ========== //

        grid.addHElement(ef_name);
        grid.addHElement(ef_comment);
        grid.addHElement(ef_url);
        grid.addHElement(ef_length);
        grid.addHElement(ef_shortcut);
        grid.addHElement(cb_upperCaseChar, TEXT_FIELDS_COLUMN);
        grid.addHElement(cb_specialChars, TEXT_FIELDS_COLUMN);
        grid.addHElement(ef_specialChars);
        grid.add(b_regeneratePassword, LABELS_COLUMN);
        grid.addHElement(ef_passwordPreview, TEXT_FIELDS_COLUMN);

        GridPane.setHalignment(b_regeneratePassword, HPos.RIGHT);

        grid.addHElement(buttonsBox, TEXT_FIELDS_COLUMN);
        grid.addHElement(l_errorLabel, TEXT_FIELDS_COLUMN);

        // ========== LISTENERS ========== //

        ef_shortcut.addEventFilter(KeyEvent.KEY_TYPED,
                CommonEventHandlers.getShortcutTFFiler(ef_shortcut));

        ef_name.textProperty().addListener(getPasswordParameterListener());

        ef_specialChars.textProperty().addListener(getPasswordParameterListener());
        ef_specialChars.addEventFilter(KeyEvent.KEY_TYPED, getSpecialCharTFFilter());

        ef_length.addEventFilter(KeyEvent.KEY_TYPED, getLengthTFFilter());
        ef_length.textProperty().addListener(getPasswordParameterListener());
        ef_length.focusedProperty().addListener(getLengthTFFocusedPropertyListener());

        cb_specialChars.setOnAction(getOnSpecialCharactersCBChanged());
        cb_upperCaseChar.setOnAction(getOnUpperCaseCBChanged());

        b_OK.setOnAction(getOnOKBtnAction());
        b_cancel.setOnAction(getOnCancelBtnAction());
        b_regeneratePassword.setOnAction(getOnRegenerateBtnAction());

        b_OK.setDisable(true);
        ef_length.setText(Integer.toString(DEFAULT_PASSWORD_LENGTH));
        open();
    }

    /* OVERRIDE */
    @Override
    protected void onUserMinimizeRequest()
    {
        // do nothing
    }

}
