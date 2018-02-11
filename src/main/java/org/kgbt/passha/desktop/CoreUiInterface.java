package org.kgbt.passha.desktop;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.kgbt.passha.core.GenericUI;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.Label;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

// TODO: Review class name
public class CoreUiInterface extends GenericUI
{
    @Override
    public boolean confirmUnsafeExit()
    {
        Alert alertDlg = new Alert(AlertType.CONFIRMATION);
        alertDlg.setTitle(Texts.LABEL_EXIT.toString());
        alertDlg.setHeaderText(null);
        alertDlg.getDialogPane().setContent(new Label(Texts.MSG_CONFIRM_UNSAFE_EXIT));
        alertDlg.initStyle(StageStyle.UNIFIED);
        Optional<ButtonType> response = alertDlg.showAndWait();
        return response.isPresent() && response.get() == ButtonType.OK;
    }

    @Override
    public void restart() throws IOException, URISyntaxException
    {
        File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        final ArrayList<String> command = new ArrayList<>();
        command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        command.add("-jar");
        command.add(currentJar.getPath());
        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
    }

    @Override
    public void reportMigrationErrors(int numberOfErrors)
    {
        Alert alertDlg = new Alert(Alert.AlertType.ERROR);
        alertDlg.setTitle(Texts.LABEL_MIGRATION_TITLE.toString());
        alertDlg.setHeaderText(null);

        alertDlg.getDialogPane().setContent(new VBox(new Label(Texts.MSG_MIGRATION_OCCURED),
            new Label("" + numberOfErrors + Texts.MSG_MIGRATION_ERRORS.toString())));
        alertDlg.initStyle(StageStyle.UNIFIED);
        alertDlg.showAndWait();
    }

    @Override
    public void reportMigrationSuccess()
    {
        Alert alertDlg = new Alert(AlertType.INFORMATION);
        alertDlg.setTitle(Texts.LABEL_MIGRATION_TITLE.toString());
        alertDlg.setHeaderText(null);

        alertDlg.getDialogPane()
            .setContent(new VBox(new Label(Texts.MSG_MIGRATION_OCCURED), new Label(Texts.MSG_MIGRATION_SUCCESS)));

        alertDlg.initStyle(StageStyle.UNIFIED);
        alertDlg.showAndWait();
    }
}
