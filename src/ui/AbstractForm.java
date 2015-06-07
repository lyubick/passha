/**
 *
 */
package ui;

import main.Exceptions;
import main.Exceptions.XC;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author curious-odd-man
 *
 */
public abstract class AbstractForm
{
    protected static final int HGAP = 10;
    protected static final int VGAP = 10;

    protected final class PADDING
    {
        public static final int bottom = 10;
        public static final int top    = 10;
        public static final int right  = 10;
        public static final int left   = 10;
    };

    protected final class WINDOW
    {
        public static final int width  = 1050;
        public static final int height = 650;
    }

    protected GridPane  grid                    = new GridPane();
    protected VBox      group                   = new VBox();
    protected Scene     scene                   = new Scene(group, WINDOW.width, WINDOW.height);

    protected MenuBar   mb_Main                 = null;

    protected final int BUTTON_HEIGHT           = 30;
    protected final int BUTTON_WIDTH            = 80;
    protected final int BUTTON_X_WIDTH          = 125;

    protected final int LABEL_WIDTH             = 100;

    protected final int FIELD_WIDTH_S           = 50;
    protected final int FIELD_WIDTH_N           = 100;
    protected final int FIELD_WIDTH_L           = 300;
    protected final int FIELD_WIDTH_PWD         = 200;

    protected final int buttonHoldTime          = 300;

    // Magic number don't lose it!
    protected final int LABEL_LENGTH_COEFICIENT = 7;

    public abstract void draw(Stage stage) throws Exceptions;

    protected final Font FONT_PRIMARY = Font.font("Consolas", FontWeight.NORMAL, 12);
    protected final Font FONT_ERROR   = Font.font("Consolas", FontWeight.BOLD, 12);

    protected AbstractForm()
    {
        grid.setGridLinesVisible(false); // FIXME Debug

        group.getChildren().addAll(grid);

        grid.setHgap(HGAP);
        grid.setVgap(VGAP);
        grid.setPadding(new Insets(PADDING.top, PADDING.right, PADDING.bottom, PADDING.left));
        grid.setAlignment(Pos.CENTER);

    }

    public class Label extends javafx.scene.control.Label
    {
        private void setUp()
        {
            this.beNormal();

            Label tmp = this;

            this.textProperty().addListener(new ChangeListener<String>()
            {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
                {
                    tmp.setMinWidth(newValue.length() * 7);
                    tmp.setMaxWidth(tmp.getMinWidth());
                }
            });
        }

        public Label()
        {
            super();
            this.setUp();
        }

        public Label(String name)
        {
            super(name);
            this.setUp();

            this.setMinWidth(name.length() * LABEL_LENGTH_COEFICIENT);
            this.setMaxWidth(this.getMinWidth());
        }

        public Label(String name, int wrapped)
        {
            super(name);
            this.setUp();

            this.setMinWidth(wrapped);
            this.setMaxWidth(this.getMinWidth());

            this.setWrapText(true);
        }

        public void beError()
        {
            this.setTextFill(Color.RED);
            this.setFont(FONT_ERROR);
        }

        public void beNormal()
        {
            this.setTextFill(Color.BLACK);
            this.setFont(FONT_PRIMARY);
        }

    }

    public class HBox extends javafx.scene.layout.HBox
    {
        public TextField getEntryTextField()
        {
            return (TextField) this.getChildren().get(1);
        }

        public Label getEntryLabel()
        {
            return (Label) this.getChildren().get(0);
        }
    }

    protected HBox getTextEntry(String name, int maxLength)
    {
        Label l_EntryName = new Label(name);
        l_EntryName.setMinWidth(LABEL_WIDTH);

        TextField tf_EntryField = new TextField();
        tf_EntryField.setMaxWidth(maxLength);
        tf_EntryField.setMinWidth(maxLength);

        HBox tmp = new HBox();

        tmp.getChildren().addAll(l_EntryName, tf_EntryField);

        return tmp;
    }

    protected Button getButton(String name)
    {
        Button tmp = new Button(name);
        tmp.setMinWidth(BUTTON_WIDTH);
        tmp.setMinHeight(BUTTON_HEIGHT);

        if (tmp.getWidth() != BUTTON_WIDTH) tmp.setMinWidth(BUTTON_X_WIDTH);

        return tmp;
    }

    protected Label getWarningLabel(String text)
    {
        Label tmp = new Label(text);
        return tmp;
    }

    // note: this should be done AFTER buttons is added to scene, else will TODO
    // throw
    protected void setButtonShortcut(final Button btn, KeyCodeCombination cmb) throws Exceptions
    {
        if (btn.getScene() == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);
        btn.getScene().getAccelerators().put(cmb, new Runnable()
        {
            @Override
            public void run()
            {
                // Do it with stile - show animation
                btn.arm();
                PauseTransition pt = new PauseTransition(Duration.millis(buttonHoldTime));
                pt.setOnFinished(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        btn.fire();
                        btn.disarm();
                    }
                });
                pt.play();
            }
        });
    }
}
