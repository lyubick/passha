package ui;

import java.io.File;

import core.Vault;
import core.VaultManager;
import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import languages.Local.Texts;
import main.Exceptions;
import main.Properties;
import main.Exceptions.XC;
import sha.SHA;
import ui.elements.Button;
import ui.elements.EntryField;
import ui.elements.Label;
import ui.elements.LabeledItem;
import ui.elements.PasswordEntryField;
import ui.elements.EntryField.TEXTFIELD;

public class FormExport extends AbstractForm
{
    private Button             b_ok               = null;
    private Button             b_cancel           = null;
    private Button             b_findFolder       = null;
    private Label              l_header           = null;
    private EntryField         ef_vaultName       = null;
    private EntryField         ef_path            = null;
    private PasswordEntryField pef_password       = null;
    private Vault              currentActiveVault = null;

    public FormExport(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.FORM_EXPORT_LABEL_EXPORT, WindowPriority.ALWAYS_ON_TOP);
        stage.setResizable(false);

        currentActiveVault = VaultManager.getInstance().getActiveVault();
        if (currentActiveVault == null) throw new Exceptions(XC.NULL);

        l_header = new Label(Texts.FORM_EXPORT_LABEL_EXPORT);
        l_header.setTextAlignment(TextAlignment.CENTER);
        l_header.beHeader();

        b_ok = new Button(Texts.LABEL_OK);
        b_ok.setDisable(true);
        b_cancel = new Button(Texts.LABEL_CANCEL);
        b_findFolder = new Button("", Common.getFindFolderImage());

        ef_vaultName = new EntryField(Texts.LABEL_VAULT, TEXTFIELD.WIDTH.XXL);
        ef_vaultName.setText(currentActiveVault.getName());
        ef_vaultName.setEditable(false);
        ef_path = new EntryField(Texts.LABEL_PATH, TEXTFIELD.WIDTH.XXL);
        ef_path.setPromptText(Texts.LABEL_INVALID_PATH.toString());
        ef_path.beError();
        pef_password = new PasswordEntryField(Texts.LABEL_PASSWORD, TEXTFIELD.WIDTH.XXL);
        pef_password.setPromptText(Texts.LABEL_INVALID_PASSWORD.toString());

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(b_cancel, HPos.RIGHT);
        GridPane.setHalignment(b_findFolder, HPos.RIGHT);

        grid.addHElement(l_header, 0, 2);
        grid.addHElement((LabeledItem) ef_vaultName);
        grid.addHElement((LabeledItem) pef_password);
        grid.add(b_findFolder, 0);
        grid.addHElement((LabeledItem) ef_path);
        grid.addHElements(0, b_ok, b_cancel);

        b_findFolder.setOnAction(event ->
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Texts.FORM_EXPORT_MSG_SELECT_PATH.toString());
            fileChooser.setInitialFileName(currentActiveVault.getName() + Properties.EXTENSIONS.EXPORT);
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = fileChooser.showSaveDialog(new Stage(StageStyle.UNIFIED));
            if (file != null) ef_path.setText(file.getAbsolutePath());
        });

        ef_path.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue.isEmpty())
                ef_path.beNormal();
            else
                ef_path.beError();
        });

        b_cancel.setOnAction(event -> close());

        b_ok.setOnAction(event ->
        {
            currentActiveVault.export(ef_path.getText());
            close();
        });

        b_ok.disableProperty()
            .bind(pef_password.isValidProperty().not()              // Password must be valid
                .or(ef_path.textProperty().isEmpty())               // Path must be defined
                .or(pef_password.focusedProperty()));               // Focus must not be on password field

        pef_password.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            // On lost focus check if password is correct
            if (newValue == false)
            {
                pef_password
                    .setValid(currentActiveVault.initializedFrom(SHA.getHashBytes(pef_password.getText().getBytes())));
                if (!pef_password.isValid()) pef_password.clear();
            }
        });

        pef_password.requestFocus();
        open();
    }
}
