package org.kgbt.passha.core.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import org.kgbt.passha.core.VaultManager;
import org.kgbt.passha.desktop.Main;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.desktop.ui.elements.Label;

public class Terminator
{
    private static boolean isExitAllowed()
    {
        try
        {
            if (!VaultManager.getInstance().isReadyToExit())
            {
                Alert alertDlg = new Alert(AlertType.CONFIRMATION);
                alertDlg.setTitle(Texts.LABEL_EXIT.toString());
                alertDlg.setHeaderText(null);
                alertDlg.getDialogPane().setContent(new Label(Texts.MSG_CONFIRM_UNSAFE_EXIT));
                alertDlg.initStyle(StageStyle.UNIFIED);
                Optional<ButtonType> response = alertDlg.showAndWait();
                return response.isPresent() && response.get() == ButtonType.OK;
            }

            return true;
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
        return false;
    }

    private static void exit(Exceptions e)
    {
        Logger.printFatal("Bye!");

        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions ignored)
        {}

        System.exit(e.getCode().ordinal());
    }

    public static void restart()
    {
        try
        {
            File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            final ArrayList<String> command = new ArrayList<>();
            command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
            command.add("-jar");
            command.add(currentJar.getPath());
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        }
        catch (URISyntaxException | IOException e)
        {
            Terminator.terminate(new Exceptions(XC.RESTART_FAILED));
        }

        Terminator.exit(new Exceptions(XC.END));
    }

    public static void terminate(Exceptions e)
    {
        if (e.getCode().equals(XC.RESTART) || e.getCode().equals(XC.END))
        {
            if (!isExitAllowed()) return;

            if (e.getCode().equals(XC.RESTART)) restart();

            if (e.getCode().equals(XC.END)) exit(e);
        }

        Logger.printFatal("TERMINATOR: FATAL ERROR OCCURED: " + e.getCode().name());

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3)
        {
            StackTraceElement element = stackTrace[2];
            Logger.printFatal("TERMINATOR: CLASS: " + element.getClassName() + " METHOD: " + element.getMethodName());
        }

        Logger.printFatal("TERMINATOR: STACK TRACE DUMP");

        StringWriter tracePrint = new StringWriter();
        e.printStackTrace(new PrintWriter(tracePrint));

        Logger.printFatal(tracePrint.toString());

        exit(e);
    }
}
