package org.kgbt.passha.ui;

import org.kgbt.passha.languages.Local.Texts;
import org.kgbt.passha.main.Exceptions;
import org.kgbt.passha.main.Exceptions.XC;
import org.kgbt.passha.main.Properties;
import org.kgbt.passha.ui.elements.Button;
import org.kgbt.passha.ui.elements.EntryField;
import org.kgbt.passha.ui.elements.LabeledItem;
import org.kgbt.passha.ui.elements.EntryField.TEXTFIELD;
import org.kgbt.passha.ui.elements.Label;
import org.kgbt.passha.core.Vault;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.db.SpecialPassword;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class FormCreatePwd extends AbstractForm
{
    private final int       LABELS_COLUMN                  = 0;
    private final int       TEXT_FIELDS_COLUMN             = LABELS_COLUMN + 1;

    private final String    SPECIAL_CHARACTERS_DEFAULT_SET = "` ~!@#$%^&*()_-+={}[]\\|:;\"\'<>,.?/";

    private final int       MAX_PASSWORD_LENGTH            = 64;
    private final int       MIN_PASSWORD_LENGTH            = 8;
    private final int       DEFAULT_PASSWORD_LENGTH        = 16;

    private SpecialPassword password                       = null;

    private final Label     l_errorLabel                   = new Label("");
    private Label           l_header                       = null;

    private Button          b_ok                           = null;
    private Button          b_cancel                       = null;
    private Button          b_regeneratePassword           = null;

    private CheckBox        cb_specialChars                = null;
    private CheckBox        cb_upperCaseChar               = null;

    private EntryField      ef_name                        = null;
    private EntryField      ef_comment                     = null;
    private EntryField      ef_url                         = null;
    private EntryField      ef_length                      = null;
    private EntryField      ef_specialChars                = null;
    private EntryField      ef_passwordPreview             = null;
    private EntryField      ef_shortcut                    = null;

    private EventHandler<KeyEvent> getLengthTFFilter()
    {
        return keyEvent -> {
            String lengthFieldText = ef_length.getText();
            if (!"0123456789".contains(keyEvent.getCharacter()) || lengthFieldText.length() >= 2)
            {
                keyEvent.consume();
            }
        };
    }

    private EventHandler<KeyEvent> getSpecialCharTFFilter()
    {
        return keyEvent -> {
            if (!SPECIAL_CHARACTERS_DEFAULT_SET.contains(keyEvent.getCharacter())
                || ef_specialChars.getText().contains(keyEvent.getCharacter()))
            {
                keyEvent.consume();
            }
        };
    }

    private ChangeListener<String> getPasswordParameterListener()
    {
        return (observable, oldValue, newValue) -> showPasswordPreview();
    }

    private EventHandler<ActionEvent> getOnSpecialCharactersCBChanged()
    {
        return event -> {
            ef_specialChars.setDisable(!ef_specialChars.isDisabled());
            showPasswordPreview();
        };
    }

    private ChangeListener<Boolean> getLengthTFFocusedPropertyListener()
    {
        return (observable, oldValue, newValue) -> {
            if (newValue)
            {
                ef_length.setText("");
                b_ok.setDisable(true);
            }
            else
            {
                b_ok.setDisable(false);
                if (ef_length.getText().length() == 0)
                    ef_length.setText(Integer.toString(DEFAULT_PASSWORD_LENGTH));
                else if (Integer.parseInt(ef_length.getText()) > MAX_PASSWORD_LENGTH)
                    ef_length.setText(Integer.toString(MAX_PASSWORD_LENGTH));
                else if (Integer.parseInt(ef_length.getText()) < MIN_PASSWORD_LENGTH)
                    ef_length.setText(Integer.toString(MIN_PASSWORD_LENGTH));
            }
        };
    }

    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return arg0 -> {
            b_ok.setDisable(true);

            try
            {
                Vault activeVault = VaultManager.getInstance().getActiveVault();
                if (password != null)
                {
                    password.setAllOptionalFields(ef_comment.getText(), ef_url.getText(), ef_shortcut.getText());
                    activeVault.addPassword(password);
                }
                else
                    activeVault
                        .addPassword(new SpecialPassword(ef_name.getText(), ef_comment.getText(), ef_url.getText(),
                            ef_length.getText(), cb_specialChars.isSelected(), cb_upperCaseChar.isSelected(),
                            ef_specialChars.getText(), ef_shortcut.getText(), activeVault));
                close();
            }
            catch (Exceptions e)
            {
                if (e.getCode() == XC.PASSWORD_NAME_EXISTS)
                {
                    l_errorLabel.setText(Texts.FORM_CREATEPWD_MSG_NAME_EXISTS);
                    ef_name.beError();
                }
                else if (e.getCode() == XC.PASSWORD_SHORTCUT_IN_USE)
                {
                    l_errorLabel.setText(
                        Texts.MSG_SHORTCUT_IN_USE_BY.toString() + ((SpecialPassword) e.getObject()).getName());
                    ef_shortcut.beError();
                }

                b_ok.setDisable(false);
            }
        };
    }

    private void showPasswordPreview()
    {
        try
        {
            ef_specialChars.beNormal();
            ef_name.beNormal();
            if (ef_length.getText().length() > 0 && Integer.parseInt(ef_length.getText()) >= MIN_PASSWORD_LENGTH
                && Integer.parseInt(ef_length.getText()) <= MAX_PASSWORD_LENGTH)
            {
                password = new SpecialPassword(ef_name.getText(), ef_comment.getText(), ef_url.getText(),
                    ef_length.getText(), cb_specialChars.isSelected(), cb_upperCaseChar.isSelected(),
                    ef_specialChars.getText(), ef_shortcut.getText(), VaultManager.getInstance().getActiveVault());
                ef_passwordPreview.setText(password.getPassword());
                l_errorLabel.setText("");
                b_ok.setDisable(false);
            }
            else
            {
                b_ok.setDisable(true);
                password = null;
            }
        }
        catch (Exceptions e)
        {
            if (e.getCode() == XC.MANDATORY_DATA_MISSING)
            {
                l_errorLabel.setText(Texts.FORM_CREATEPWD_MSG_MISSING_PARAM);

                if (ef_name.getText().length() == 0)
                {
                    ef_name.beError();
                }

                if (cb_specialChars.isSelected() && ef_specialChars.getText().length() == 0)
                {
                    ef_specialChars.beError();
                }
            }
            b_ok.setDisable(true);
            password = null;
        }
    }

    public FormCreatePwd(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.FORM_CREATEPWD_NAME, WindowPriority.ONLY_ONE_OPENED, false);

        // ========== BUTTONS ========== //

        b_ok = new Button(Texts.LABEL_CREATE);
        b_cancel = new Button(Texts.LABEL_CANCEL);

        GridPane.setHalignment(b_ok, HPos.LEFT);
        GridPane.setHalignment(b_cancel, HPos.RIGHT);

        b_regeneratePassword = new Button("", Common.getRegenerateImage());

        // ========== ENTRY FIELDS ========== //

        l_header = new Label(Texts.FORM_CREATEPWD_LABEL_HEADER);
        l_header.beHeader();

        ef_name = new EntryField(Texts.LABEL_NAME.toString() + "*", TEXTFIELD.WIDTH.XXL);
        ef_comment = new EntryField(Texts.LABEL_COMMENT, TEXTFIELD.WIDTH.XXL);
        ef_url = new EntryField(Texts.LABEL_URL, TEXTFIELD.WIDTH.XXL);
        ef_length = new EntryField(Texts.LABEL_LENGTH.toString() + "*", TEXTFIELD.WIDTH.S);
        ef_specialChars = new EntryField(Texts.LABEL_SPECIAL_CHARACTERS.toString() + "*", TEXTFIELD.WIDTH.XXL);
        ef_passwordPreview = new EntryField(Texts.LABEL_PASSWORD, TEXTFIELD.WIDTH.XXL);
        ef_shortcut = new EntryField(Texts.LABEL_SHORTCUT, TEXTFIELD.WIDTH.XS);

        // ========== LABELS ========== //

        l_errorLabel.beError();

        // ========== CHECK BOXES ========== //

        cb_specialChars = new CheckBox(Texts.FORM_CREATEPWD_MSG_MUST_HAVE_SPECIAL.toString());
        cb_upperCaseChar = new CheckBox(Texts.FORM_CREATEPWD_MSG_MUST_HAVE_UPPER.toString());
        cb_specialChars.setSelected(true);
        cb_upperCaseChar.setSelected(true);

        double tmp = Label.calcLength(cb_specialChars.getText());
        cb_upperCaseChar.setMinWidth(tmp + 10);
        cb_upperCaseChar.setMinHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);
        cb_upperCaseChar.setMaxWidth(tmp);
        cb_upperCaseChar.setMaxHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);

        tmp = Label.calcLength(cb_upperCaseChar.getText());
        cb_specialChars.setMinWidth(tmp + 10);
        cb_specialChars.setMinHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);
        cb_specialChars.setMaxWidth(tmp);
        cb_specialChars.setMaxHeight(Properties.GUI.STANDARD.SIZE.HEIGHT);

        // ========== TEXTS ========== //

        ef_specialChars.setText(SPECIAL_CHARACTERS_DEFAULT_SET);
        ef_passwordPreview.setMinWidth(TEXTFIELD.WIDTH.XL);
        ef_passwordPreview.setEditable(false);

        // ========== GRID ========== //

        grid.addHElement(l_header, 0, 2);
        grid.addHElement((LabeledItem) ef_name);
        grid.addHElement((LabeledItem) ef_comment);
        grid.addHElement((LabeledItem) ef_url);
        grid.addHElement((LabeledItem) ef_length);
        grid.addHElement((LabeledItem) ef_shortcut);
        grid.addHElement(cb_upperCaseChar, TEXT_FIELDS_COLUMN);
        grid.addHElement(cb_specialChars, TEXT_FIELDS_COLUMN);
        grid.addHElement((LabeledItem) ef_specialChars);
        grid.add(b_regeneratePassword, LABELS_COLUMN);
        grid.addHElement(ef_passwordPreview, TEXT_FIELDS_COLUMN);

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(b_regeneratePassword, HPos.RIGHT);
        GridPane.setHalignment(l_errorLabel, HPos.CENTER);

        grid.add(b_ok, TEXT_FIELDS_COLUMN);
        grid.addHElement(b_cancel, TEXT_FIELDS_COLUMN);
        grid.addHElement(l_errorLabel, LABELS_COLUMN, 2);

        // ========== LISTENERS ========== //

        ef_shortcut.addEventFilter(KeyEvent.KEY_TYPED, Common.getShortcutTFFiler(ef_shortcut));

        ef_name.textProperty().addListener(getPasswordParameterListener());

        ef_specialChars.textProperty().addListener(getPasswordParameterListener());
        ef_specialChars.addEventFilter(KeyEvent.KEY_TYPED, getSpecialCharTFFilter());

        ef_length.addEventFilter(KeyEvent.KEY_TYPED, getLengthTFFilter());
        ef_length.textProperty().addListener(getPasswordParameterListener());
        ef_length.focusedProperty().addListener(getLengthTFFocusedPropertyListener());

        cb_specialChars.setOnAction(getOnSpecialCharactersCBChanged());
        cb_upperCaseChar.setOnAction(event -> showPasswordPreview());

        b_ok.setOnAction(getOnOKBtnAction());
        b_cancel.setOnAction(event -> close());
        b_regeneratePassword.setOnAction(event -> showPasswordPreview());

        b_ok.setDisable(true);
        ef_length.setText(Integer.toString(DEFAULT_PASSWORD_LENGTH));

        open();
    }

    @Override
    protected void onUserMinimizeRequest()
    {
        // Do nothing
    }
}
