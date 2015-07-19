package ui;

import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import languages.Texts;
import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import ui.elements.ProgressBar;

public class FormUpdate extends AbstractForm
{

    // TODO make WINDOW private when override
    private static final class WINDOW
    {
        public static final int width  = 300;
        public static final int height = 150;
    }

    ProgressBar          pb_Progress               = null;

    private String       currentVersion            = null;
    private String       latestVersion             = null;

    private File         downloaded                = null;
    private File         executable                = null;

    private final String GITHUB_RELEASE_URL        = "https://github.com/lyubick/passha/releases";
    private final String GITHUB_ARCHIVE_URL        = "https://github.com/lyubick/passha/archive/";

    private final String RELEASE_VERSION_PREFIX    = "/lyubick/passha/tree/";
    private final String RELEASE_VERSION_TEMPLATE  = "v0.0.000";

    private final String RELEASE_ARCHIVE_EXTENSION = ".zip";
    private final String RELEASE_EXECUTABLE_NAME   = "pasSHA.jar";

    private int          BUFFER_SIZE               = 4096;

    // ***** GET LATEST VERSION ROUTINE *****//
    private EventHandler<WorkerStateEvent> getOnUpdateTaskFailed()
    {
        return new EventHandler<WorkerStateEvent>()
        {
            @Override
            public void handle(WorkerStateEvent event)
            {
                try
                {
                    TrayAgent.getInstance().showNotification("Update", "FAIELD",
                            MessageType.WARNING);

                    new FormLogin();
                    close();
                }
                catch (Exceptions e)
                {
                    Terminator.terminate(e);
                }
            }
        };
    }

    private EventHandler<WorkerStateEvent> getOnLatestVersionSucceded()
    {
        return new EventHandler<WorkerStateEvent>()
        {
            @Override
            public void handle(WorkerStateEvent event)
            {
                Logger.printDebug("Update found: " + latestVersion + " current " + currentVersion);

                // No update required continue
                if (currentVersion.equals(latestVersion))
                {
                    new FormLogin();
                    close();
                    return;
                }

                // Download latest
                Task<Void> tsk_downloadLatestVersion = downloadLatestVersion();
                tsk_downloadLatestVersion.setOnSucceeded(getOnDownloadSucceeded());
                tsk_downloadLatestVersion.setOnFailed(getOnUpdateTaskFailed());

                pb_Progress.rebind(tsk_downloadLatestVersion.progressProperty(),
                        tsk_downloadLatestVersion.messageProperty());

                new Thread(tsk_downloadLatestVersion).start();
            }
        };
    }

    private EventHandler<WorkerStateEvent> getOnDownloadSucceeded()
    {
        return new EventHandler<WorkerStateEvent>()
        {
            @Override
            public void handle(WorkerStateEvent event)
            {
                pb_Progress.unbind();
                close();

                // Install latest
                Task<Void> tsk_installLatestVersion = installLatestVersion();
                Thread install = new Thread(tsk_installLatestVersion);
                install.setDaemon(true);
                install.start();

                Terminator.terminate(new Exceptions(XC.END));
            }
        };
    }

    private Task<Void> getLatestVersion()
    {
        return new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                double maxSize = 0;
                double curSize = 0;

                updateProgress(0, 1);
                updateMessage(TextID.FORM_UPDATE_LABEL_CHECK.toString());

                try
                {
                    URL url = new URL(GITHUB_RELEASE_URL);
                    URLConnection conn = url.openConnection();

                    BufferedReader html =
                            new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    curSize = maxSize = conn.getContentLength();

                    while ((latestVersion = html.readLine()) != null)
                    {
                        curSize -= latestVersion.length();

                        if (latestVersion.contains(RELEASE_VERSION_PREFIX))
                        {
                            int idx =
                                    latestVersion.indexOf(RELEASE_VERSION_PREFIX)
                                            + RELEASE_VERSION_PREFIX.length();
                            latestVersion =
                                    latestVersion.substring(idx,
                                            idx + RELEASE_VERSION_TEMPLATE.length());
                            // Latest version should be on top always
                            break;
                        }

                        updateProgress(1 - curSize / maxSize, 1);
                    }

                    html.close();

                }
                catch (MalformedURLException e)
                {
                    // Nothing here, due hard coded URL
                }
                catch (IOException e)
                {
                    Logger.printError("Update failed! Connection problem!");
                    this.failed();
                }

                if (latestVersion.length() == 0)
                {
                    Logger.printError("No release found!");
                    this.failed();
                }

                updateProgress(1, 1);

                return null;
            }
        };
    }

    private Task<Void> downloadLatestVersion()
    {
        return new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                double maxSize = 0;
                double curSize = 0;

                updateProgress(0, 1);
                updateMessage(TextID.FORM_UPDATE_LABEL_DOWNLOAD.toString());

                try
                {
                    URL url =
                            new URL(GITHUB_ARCHIVE_URL + latestVersion + RELEASE_ARCHIVE_EXTENSION);

                    URLConnection conn = url.openConnection();

                    maxSize = conn.getContentLength();

                    downloaded = File.createTempFile(latestVersion, RELEASE_ARCHIVE_EXTENSION);

                    InputStream is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(downloaded.getCanonicalPath());

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;

                    while ((len = is.read(buffer)) > 0)
                    {
                        curSize += len;
                        fos.write(buffer, 0, len);

                        updateProgress(curSize, maxSize);
                    }

                    is.close();
                    fos.close();

                }
                catch (FileNotFoundException e)
                {
                    Logger.printError("Update failed! Unable to create file! Hello Linux users!");
                    this.failed();
                }
                catch (IOException e)
                {
                    Logger.printError("Update failed! Connection problem!");
                    this.failed();
                }

                updateProgress(1, 1);
                return null;
            }
        };
    }

    private Task<Void> installLatestVersion()
    {
        return new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                executable =
                        new File(FormUpdate.class.getProtectionDomain().getCodeSource()
                                .getLocation().toURI().getPath());

                try
                {
                    ZipFile zip = new ZipFile(downloaded);

                    Enumeration<?> enu = zip.entries();

                    ZipEntry entry = null;

                    // Find executable in archive
                    while (enu.hasMoreElements()
                            && !(entry = (ZipEntry) enu.nextElement()).toString().contains(
                                    RELEASE_EXECUTABLE_NAME))
                        ;

                    InputStream is = zip.getInputStream(entry);

                    // DANGER ZONE
                    executable.delete();

                    FileOutputStream fos = new FileOutputStream(executable);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;

                    while ((len = is.read(buffer)) > 0)
                        fos.write(buffer, 0, len);

                    is.close();
                    fos.close();
                    zip.close();

                }
                catch (IOException e1)
                {
                    Logger.printError("Update failed! Unable to create file! Hello Linux users!");
                    this.failed();
                }

                Terminator.terminate(new Exceptions(XC.RESTART));

                return null;
            }
        };
    }

    private EventHandler<WindowEvent> getOnShown()
    {
        return new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                currentVersion = "v" + Texts.getVersion() + ".000";

                Task<Void> tsk_LatestVersion = getLatestVersion();
                tsk_LatestVersion.setOnSucceeded(getOnLatestVersionSucceded());
                tsk_LatestVersion.setOnFailed(getOnUpdateTaskFailed());

                pb_Progress.rebind(tsk_LatestVersion.progressProperty(),
                        tsk_LatestVersion.messageProperty());

                new Thread(tsk_LatestVersion).start();
            }
        };

    }

    public FormUpdate(AbstractForm parent)
    {
        super(parent, "");

        stage.initStyle(StageStyle.UNDECORATED);
        stage.centerOnScreen();

        stage.setWidth(WINDOW.width);
        stage.setHeight(WINDOW.height);

        pb_Progress = new ProgressBar("");

        grid.addVElement(pb_Progress, 0);

        stage.setOnShown(getOnShown());

        stage.show();

    }

}
