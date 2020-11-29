package org.kgbt.passha.desktop.ui;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.common.Utilities;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.desktop.languages.Local;
import org.kgbt.passha.desktop.ui.elements.Button;
import org.kgbt.passha.desktop.ui.elements.ProgressBar;

import java.awt.TrayIcon.MessageType;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FormUpdate extends AbstractForm {

    ProgressBar pb_progress;

    private String currentVersion = null;
    private String latestVersion = null;

    private File downloaded = null;

    private final String GITHUB_RELEASE_URL = "https://github.com/lyubick/passha/releases";
    private final String GITHUB_DOWNLOAD_URL = GITHUB_RELEASE_URL + "/download";

    private final String RELEASE_VERSION_PREFIX = "/lyubick/passha/tree/";
    private final String RELEASE_VERSION_TEMPLATE = "v0.0.000";

    private final String RELEASE_EXECUTABLE_NAME = "pasSHA.jar";

    private final Button b_update;
    private final Button b_skip;

    private static final String UPDATER_SCRIPT =
            "@echo off\n" +
            ":delete\n" +
            "\tdel pasSHA.jar\n" +
            "\tif exist \"pasSHA.jar\" goto :delete\n" +
            ":copy\n" +
            "\tcopy %1 pasSHA.jar\n" +
            ":start\n" +
            "\tstart javaw -jar pasSHA.jar\n" +
            ":stop";

    private void skipUpdate() throws Exceptions {
        new FormVaultsManager();
        close();
    }

    private EventHandler<WorkerStateEvent> getOnUpdateTaskFailed() {
        return event -> {
            try {
                TrayAgent.getInstance().showNotification(
                        Local.Texts.FORM_UPDATE_LABEL_CHECK, // FIXME
                        Local.Texts.TRAY_MSG_FAILED_TO_UPDATE,
                        MessageType.WARNING);
                skipUpdate();
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }
        };
    }

    private EventHandler<WorkerStateEvent> getOnLatestVersionSucceded() {
        return event -> {
            double current = Double.parseDouble(currentVersion.substring(1, 4));
            double latest = Double.parseDouble(latestVersion.substring(1, 4));

            Logger.printDebug("Update found: " + latestVersion + ":" + latest + " current" + currentVersion + ":" + current);

            if (current + 0.01 > latest) {
                try {
                    skipUpdate();
                } catch (Exceptions exceptions) {
                    exceptions.printStackTrace();
                }
                return;
            }

            showNotification(latestVersion);
        };
    }

    private EventHandler<ActionEvent> getOnUpdateBtnAction() {
        return event -> {
            Logger.printTrace("Updating...");
            b_update.setDisable(true);
            b_skip.setVisible(false);

            // Download latest
            Task<Void> tsk_downloadLatestVersion = downloadLatestVersion();
            tsk_downloadLatestVersion.setOnFailed(getOnUpdateTaskFailed());

            pb_progress.rebind(tsk_downloadLatestVersion.progressProperty(), tsk_downloadLatestVersion.messageProperty());

            Logger.printTrace("Launching download thread...");
            new Thread(tsk_downloadLatestVersion).start();
        };
    }

    private EventHandler<ActionEvent> getOnSkipBtnAction() {
        return event -> {
            try {
                skipUpdate();
            } catch (Exceptions exceptions) {
                exceptions.printStackTrace();
            }
        };
    }

    private Task<Void> getLatestVersion() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                double maxSize;
                double curSize;

                updateMessage(Local.Texts.FORM_UPDATE_LABEL_CHECK.toString());

                try {
                    URL url = new URL(GITHUB_RELEASE_URL);
                    URLConnection conn = url.openConnection();

                    BufferedReader html = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    curSize = maxSize = conn.getContentLength();

                    while ((latestVersion = html.readLine()) != null) {
                        curSize -= latestVersion.length();

                        if (latestVersion.contains(RELEASE_VERSION_PREFIX)) {
                            int idx = latestVersion.indexOf(RELEASE_VERSION_PREFIX) + RELEASE_VERSION_PREFIX.length();
                            latestVersion = latestVersion.substring(idx, idx + RELEASE_VERSION_TEMPLATE.length());

                            // Latest version should be on top always
                            break;
                        }
                        updateProgress(1 - curSize / maxSize, 1);
                    }

                    html.close();

                } catch (MalformedURLException e) {
                    // Nothing here, due hard coded URL
                } catch (IOException e) {
                    Logger.printError("Update failed! Connection problem!");
                    this.failed();
                }

                if (latestVersion.length() == 0) {
                    Logger.printError("No release found!");
                    this.failed();
                }

                updateProgress(1, 1);

                return null;
            }
        };
    }

    private Task<Void> downloadLatestVersion() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                Logger.printDebug("Downloading...");

                updateMessage(Local.Texts.FORM_UPDATE_LABEL_DOWNLOAD.toString());

                try {
                    URL url = new URL(GITHUB_DOWNLOAD_URL + "/" + latestVersion + "/" + RELEASE_EXECUTABLE_NAME);

                    URLConnection conn = url.openConnection();

                    double maxSize = conn.getContentLength();
                    double curSize = 0.0;

                    downloaded = File.createTempFile(latestVersion, RELEASE_EXECUTABLE_NAME);

                    InputStream is = conn.getInputStream();

                    Logger.printDebug("Stream available: " + is.available() + ", temp file: " + downloaded.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(downloaded.getCanonicalPath());

                    byte[] buffer = new byte[Utilities.DEFAULT_BUFFER_SIZE];
                    int len;

                    while ((len = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                        updateProgress( curSize += len, maxSize);
                    }

                    is.close();
                    fos.close();

                } catch (FileNotFoundException e) {
                    Logger.printError("Update failed! Unable to create file! Hello Linux users !");
                    this.failed();
                } catch (IOException e) {
                    Logger.printError("Update failed! Connection problem!");
                    this.failed();
                }

                updateProgress(1, 1);
                Logger.printDebug("Download OK. Installing...");

                try {
                    Logger.printDebug("Creating 'updater.bat' script...");
                    Utilities.writeToFile("updater.bat", UPDATER_SCRIPT);
                    Logger.printDebug("Script created. Launching...");
                    Runtime.getRuntime().exec("updater.bat " + downloaded.getAbsolutePath());

                    Terminator.terminate(new Exceptions(XC.END));
                } catch (IOException e) {
                    Logger.printError("Update failed! Failed to start updater script");
                    this.failed();
                } catch (Exceptions exceptions) {
                    Logger.printError("Update failed! Failed to create updater script");
                    this.failed();
                }

                return null;
            }
        };
    }

    private EventHandler<WindowEvent> getOnShown() {
        return event -> {
            currentVersion = "v" + Properties.SOFTWARE.VERSION + ".000";

            Task<Void> tsk_LatestVersion = getLatestVersion();

            tsk_LatestVersion.setOnSucceeded(getOnLatestVersionSucceded());
            tsk_LatestVersion.setOnFailed(getOnUpdateTaskFailed());

            pb_progress.rebind(tsk_LatestVersion.progressProperty(), tsk_LatestVersion.messageProperty());

            new Thread(tsk_LatestVersion).start();
        };
    }

    private void showNotification(String newVersion) {
        b_update.setVisible(true);
        b_skip.setVisible(true);
        pb_progress.requestFocus();
        pb_progress.unbind();

        pb_progress.getLabel().setText(
                Local.Texts.FORM_UPDATE_MSG_UPDATE_AVAILABLE.toString()
                        + " "
                        + Local.Texts.LABEL_UPDATE
                        + " to "
                        + newVersion + " ?"
        );
    }

    public FormUpdate() throws Exceptions {
        super(null, Local.Texts.LABEL_UPDATE, WindowPriority.ALWAYS_ON_TOP, false);

        b_update = new Button(Local.Texts.LABEL_UPDATE);
        b_skip = new Button(Local.Texts.LABEL_SKIP);
        b_update.setDefaultButton(true);
        b_update.setVisible(false);
        b_skip.setVisible(false);

        pb_progress = new ProgressBar("", 300, 30);

        GridPane.setHalignment(pb_progress, HPos.CENTER);
        GridPane.setHalignment(b_update, HPos.RIGHT);

        grid.addVElement(pb_progress, 0);
        grid.add(b_skip, 0);
        grid.add(b_update, 0);

        stage.setOnShown(getOnShown());

        b_update.setOnAction(getOnUpdateBtnAction());
        b_skip.setOnAction(getOnSkipBtnAction());

        open();
    }

    protected void onUserCloseRequest() {
        try {
            skipUpdate();
        } catch (Exceptions e) {
            Terminator.terminate(e);
        }
    }

}
