/**
 *
 */
package UI;

import Common.Exceptions;
import Common.Exceptions.XC;
import Common.Return.RC;
import Languages.Texts.TextID;
import Logger.Logger;
import Main.PasswordCollection;
import Main.SpecialPassword;
import UI.Controller.FORMS;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class SpecialPasswordForm extends AbstractForm
{
    private final String       FORM_NAME              = TextID.ADD_SPECIAL_PASSWORD.toString();
    private final int          LABELS_COLUMN          = 0;
    private final int          TEXT_FIELDS_COLUMN     = 1;
    private final int          TEXT_FIELD_LENGTH_SIZE = 40;
    private final int          ERROR_TEXT_LINE        = 7;
    private final int          TEXT_FIELDS_WIDTH      = 300;

    private final Label        l_errorLabel           = new Label("");

    private final TextField    tf_name                = new TextField();
    private final TextField    tf_comment             = new TextField();
    private final TextField    tf_url                 = new TextField();
    private final TextField    tf_generatedPassword   = new TextField();
    private final TextField    tf_length              = new TextField();

    private PasswordCollection pc                     = null;

    public SpecialPasswordForm()
    {
        int currentGridLine = 0;

        try
        {
            pc = PasswordCollection.getInstance();
        }
        catch (Exceptions e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Button b_OK = new Button(TextID.CREATE.toString());
        Button b_cancel = new Button(TextID.CANCEL.toString());
        // Button b_incLength = new Button("+");
        // Button b_decLength = new Button("-");

        Text l_name = new Text(TextID.NAME.toString());
        Label l_comment = new Label(TextID.COMMENT.toString());
        Label l_url = new Label(TextID.URL.toString());
        Label lLength = new Label(TextID.LENGTH.toString());

        // CheckBox cb_specialChars = new
        // CheckBox(TextID.MUST_CONTAINT_SPECIAL_CHARS.toString());
        // CheckBox cb_upperCaseChar = new
        // CheckBox(TextID.MUST_CONTAIN_UPPER_CASE_CHAR.toString());

        tf_length.setMaxWidth(TEXT_FIELD_LENGTH_SIZE);
        tf_name.setMaxWidth(TEXT_FIELDS_WIDTH);
        tf_name.setMinWidth(TEXT_FIELDS_WIDTH);

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

        grid.add(l_errorLabel, TEXT_FIELDS_COLUMN, ERROR_TEXT_LINE);

        HBox buttonsBox = new HBox();

        buttonsBox.setSpacing(HGAP);
        buttonsBox.setAlignment(Pos.BASELINE_CENTER);
        buttonsBox.getChildren().addAll(b_OK, b_cancel);

        HBox.setHgrow(b_OK, Priority.ALWAYS);
        HBox.setHgrow(b_cancel, Priority.ALWAYS);
        b_OK.setMaxWidth(Double.MAX_VALUE);
        b_cancel.setMaxWidth(Double.MAX_VALUE);

        grid.add(buttonsBox, TEXT_FIELDS_COLUMN, currentGridLine);
        currentGridLine++;

        b_OK.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                Logger.printDebug("Fields: " + tf_name.getText() + tf_comment.getText()
                        + tf_url.getText() + tf_length.getText() + tf_generatedPassword.getText());

                try
                {
                    if (pc.addPassword(new SpecialPassword(tf_name.getText(), tf_comment.getText(),
                            tf_url.getText())) == RC.OK)
                    {
                        ctrl.switchForm(FORMS.MAN_PWD);
                    }
                    else
                    {
                        l_errorLabel.setText(TextID.ERR_NAME_ALREADY_TAKEN.toString());
                    }
                }
                catch (Exceptions e)
                {
                    if (e.getCode() == XC.MISSING_MANDATORY_DATA)
                        l_errorLabel.setText(TextID.ERR_MISSING_PASSWORD_NAME.toString());
                    else
                        System.exit(RC.ABEND.ordinal()); // TODO
                }

                // TODO remove
                pc.save();
            }
        });

        b_cancel.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                ctrl.switchForm(FORMS.MAN_PWD);
            }

        });
    }

    @Override
    public void draw(Stage stage)
    {
        l_errorLabel.setText("");

        tf_name.clear();
        tf_comment.clear();
        tf_url.clear();
        tf_generatedPassword.clear();
        tf_length.clear();

        stage.setScene(scene);
        stage.show();
    }
}
