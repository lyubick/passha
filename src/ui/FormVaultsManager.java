package ui;

import db.iSpecialPassword;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import main.Properties;
import ui.elements.Button;
import ui.elements.Button.BUTTON.SIZE;
import ui.elements.EntryField.TEXTFIELD;
import ui.elements.LoginTabContents;
import ui.elements.VaultTabContent;

public class FormVaultsManager extends AbstractForm
{
    private final class WINDOW
    {
        public static final int width  = 900;
        public static final int height = 500;
    }

    private final int                   tabpaneMinHeight = WINDOW.height - 150;
    private final int                   tabpaneMinWidth  = WINDOW.width - 200;

    private static AbstractForm         This             = null;

    private TableView<iSpecialPassword> table            = null;
    private TextField                   tf_pass          = null;
    private Button                      b_new            = null;
    private Button                      b_delete         = null;
    private Button                      b_reset          = null;
    private Button                      b_copy           = null;
    private Button                      b_export         = null;
    private Button                      b_edit           = null;

    private Menu                        m_file           = null;
    private Menu                        m_help           = null;
    private MenuItem                    mi_about         = null;
    private MenuItem                    mi_exit          = null;
    private MenuItem                    mi_settings      = null;

    private TabPane                     tp_vaults        = null;

    static private int                  currentVaultIdx  = 1;

    public FormVaultsManager()
    {
        super(null, TextID.FORM_MANAGEPWD_NAME.toString()); // No parents it
        // is a main Form

        stage.setHeight(WINDOW.height);
        stage.setWidth(WINDOW.width);
        stage.setMinHeight(WINDOW.height);
        stage.setMinWidth(WINDOW.width);

        This = this;

        // ========== MENU ========== //
        // TODO: create class for menu
        mb_main = new MenuBar();
        m_file = new Menu(TextID.MENU_LABEL_FILE.toString());
        m_help = new Menu(TextID.MENU_LABEL_HELP.toString());

        mi_settings = new MenuItem(TextID.FORM_SETTINGS_NAME.toString());
        mi_exit = new MenuItem(TextID.MENU_LABEL_EXIT.toString());
        mi_about = new MenuItem(TextID.MENU_LABEL_ABOUT.toString());

        m_file.getItems().addAll(mi_settings, mi_exit);
        m_help.getItems().addAll(mi_about);
        mb_main.getMenus().addAll(m_file, m_help);

        mi_settings.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                new FormSettings(This);
            }
        });

        mi_exit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Terminator.terminate(new Exceptions(XC.END));
            }
        });

        mi_about.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                new FormAbout(This);
            }
        });

        // ========== REFRESH ========== //
        group.getChildren().remove(grid);
        group.getChildren().addAll(mb_main, grid);

        // ========== BUTTONS ========== //
        // FIXME LANG shortcuts
        b_new = new Button("_" + TextID.COMMON_LABEL_NEW.toString(), SIZE.L);
        b_delete = new Button("_" + TextID.FORM_CREATEPWD_MANAGER_LABEL_DELETE.toString(), SIZE.L);
        b_copy = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD.toString(), SIZE.L);
        b_export = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_EXPORT.toString(), SIZE.L);
        b_reset = new Button("_" + TextID.FORM_MANAGEPWD_LABEL_RESET.toString(), SIZE.L);
        b_edit = new Button("_" + TextID.FORM_MANAGEWD_LABEL_EDIT.toString(), SIZE.L);

        b_export.setDisable(false); // TODO: Request password on export
        b_copy.setDisable(true);
        b_reset.setDisable(false);

        GridPane.setValignment(b_new, VPos.TOP);
        GridPane.setValignment(b_delete, VPos.TOP);
        GridPane.setHalignment(b_copy, HPos.LEFT);
        GridPane.setValignment(b_export, VPos.BOTTOM);
        GridPane.setValignment(b_edit, VPos.TOP);

        GridPane.setMargin(b_edit, new Insets(40, 0, 0, 0));
        GridPane.setMargin(b_delete, new Insets(80, 0, 0, 0));
        GridPane.setMargin(b_copy, new Insets(0, 0, 0, 270));

        // ========== TEXT FIELD ========== //
        tf_pass = new TextField();
        tf_pass.setMaxWidth(TEXTFIELD.WIDTH.L);
        tf_pass.setEditable(false);

        // ========== TAB pane =========== //

        tp_vaults = new TabPane();
        tp_vaults.setMinHeight(tabpaneMinHeight);
        tp_vaults.setMinWidth(tabpaneMinWidth);

        Tab t_newTabCreator = new Tab();

        t_newTabCreator.setClosable(false);
        t_newTabCreator.setText("+");
        t_newTabCreator.setOnSelectionChanged(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                if (t_newTabCreator.isSelected()) AddTab();
                event.consume();
            }

        });
        tp_vaults.getTabs().add(t_newTabCreator);

        // ========== GRID ========== //
        grid.add(tp_vaults, 0, 0);

        grid.add(tf_pass, 0, 1);
        grid.add(b_copy, 0, 1);

        grid.add(b_export, 1, 0);
        grid.add(b_new, 1, 0);
        grid.add(b_edit, 1, 0);
        grid.add(b_delete, 1, 0);
        grid.add(b_reset, 1, 0);

        open();
    }

    // called when user presses '+' tab or at the start when auto login is off
    private void AddTab()
    {
        int vaultCount = tp_vaults.getTabs().size();

        if (vaultCount > Properties.UI.VAULT.MAX_COUNT)
            tp_vaults.getTabs().get(Properties.UI.VAULT.MAX_COUNT).setDisable(true);

        Tab tab = new Tab();
        tab.setText("Vault " + currentVaultIdx++ + ": ");

        tab.setOnClosed(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                // TODO Auto-generated method stub
                tp_vaults.getTabs().get(tp_vaults.getTabs().size() - 1).setDisable(false);
            }
        });

        tp_vaults.getTabs().add(Math.max(0, vaultCount - 1), tab);

        tp_vaults.getSelectionModel().select(tab);

        tab.setContent(new LoginTabContents(tab));

    }

    public void refresh()
    {
        b_copy.setDisable(true);
        tf_pass.clear();
        table.getSelectionModel().clearSelection();
        table.setItems(
                ((VaultTabContent) tp_vaults.getSelectionModel().getSelectedItem().getContent())
                        .getIface());
    }
}
