
/**
 * // Code to download from google drive
 *
 *
 * try
 * {
 * final String fileId = "0ByKXzamHKjIqMHZMV1ZsV0xpS1U";
 *
 * final String googleDriveUrl = "https://drive.google.com/";
 * final String googleDriveDownloadCommand = "uc?export=download";
 * final String googleDriveFileId = "id=" + fileId;
 *
 * URL website = new URL(googleDriveUrl + googleDriveDownloadCommand + "&" + googleDriveFileId);
 * Files.copy(website.openStream(), Paths.get("Retrieved.txt"), StandardCopyOption.REPLACE_EXISTING);
 * }
 * catch (IOException e1)
 * {
 * fail("didnt retrieve file");
 * }
 */

// package ui;
//
// import java.awt.TrayIcon.MessageType;
// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.net.URLConnection;
// import java.util.Enumeration;
// import java.util.zip.ZipEntry;
// import java.util.zip.ZipFile;
// import javafx.concurrent.Task;
// import javafx.concurrent.WorkerStateEvent;
// import javafx.event.ActionEvent;
// import javafx.event.EventHandler;
// import javafx.geometry.HPos;
// import javafx.scene.layout.GridPane;
// import javafx.stage.WindowEvent;
// import languages.Texts.TextID;
// import logger.Logger;
// import main.Exceptions;
// import main.Properties;
// import main.Terminator;
// import main.Exceptions.XC;
// import ui.elements.Button;
// import ui.elements.ProgressBar;
// import utilities.Utilities;
//
// public class FormUpdate extends AbstractForm
// {
//
// ProgressBar pb_progress = null;
//
// private String currentVersion = null;
// private String latestVersion = null;
//
// private File downloaded = null;
// private File newExecutable = null;
//
// private final String GITHUB_RELEASE_URL =
// "https://github.com/lyubick/passha/releases";
// private final String GITHUB_ARCHIVE_URL =
// "https://github.com/lyubick/passha/archive/";
//
// private final String RELEASE_VERSION_PREFIX = "/lyubick/passha/tree/";
// private final String RELEASE_VERSION_TEMPLATE = "v0.0.000";
//
// private final String RELEASE_ARCHIVE_EXTENSION = ".zip";
// private final String RELEASE_EXECUTABLE_NAME = "pasSHA.jar";
//
// private Button b_update = null;
// private Button b_skip = null;
//
// // ***** GET LATEST VERSION ROUTINE *****//
// private void skipUpdate()
// {
// new FormLogin();
// close();
// }
//
// private EventHandler<WorkerStateEvent> getOnUpdateTaskFailed()
// {
// return new EventHandler<WorkerStateEvent>()
// {
// @Override
// public void handle(WorkerStateEvent event)
// {
// try
// {
// TrayAgent.getInstance().showNotification(TextID.FORM_UPDATE_LABEL_UPDATE,
// TextID.TRAY_MSG_FAILED_TO_UPDATE, MessageType.WARNING);
// skipUpdate();
// }
// catch (Exceptions e)
// {
// Terminator.terminate(e);
// }
// }
// };
// }
//
// private EventHandler<WorkerStateEvent> getOnLatestVersionSucceded()
// {
// return new EventHandler<WorkerStateEvent>()
// {
// @Override
// public void handle(WorkerStateEvent event)
// {
// double current = Double.parseDouble(currentVersion.substring(1, 4));
// double latest = Double.parseDouble(latestVersion.substring(1, 4));
//
// Logger.printDebug("Update found: " + latestVersion + ":" + latest + " current
// "
// + currentVersion + ":" + current);
//
// if (current + 0.01 > latest)
// {
// skipUpdate();
// return;
// }
//
// showNotification(latestVersion);
// }
// };
// }
//
// private EventHandler<ActionEvent> getOnUpdateBtnAction()
// {
// return new EventHandler<ActionEvent>()
// {
// @Override
// public void handle(ActionEvent event)
// {
// Logger.printTrace("Updating...");
// b_update.setDisable(true);
// b_skip.setVisible(false);
//
// // Download latest
// Task<Void> tsk_downloadLatestVersion = downloadLatestVersion();
// tsk_downloadLatestVersion.setOnSucceeded(getOnDownloadSucceeded());
// tsk_downloadLatestVersion.setOnFailed(getOnUpdateTaskFailed());
//
// pb_progress.rebind(tsk_downloadLatestVersion.progressProperty(),
// tsk_downloadLatestVersion.messageProperty());
//
// Logger.printTrace("Launching download thread...");
// new Thread(tsk_downloadLatestVersion).start();
// }
// };
// }
//
// private EventHandler<ActionEvent> getOnSkipBtnAction()
// {
// return new EventHandler<ActionEvent>()
// {
// @Override
// public void handle(ActionEvent event)
// {
// skipUpdate();
// }
// };
// }
//
// private EventHandler<WorkerStateEvent> getOnDownloadSucceeded()
// {
// return new EventHandler<WorkerStateEvent>()
// {
// @Override
// public void handle(WorkerStateEvent event)
// {
// pb_progress.unbind();
// close();
//
// // Install latest
// Task<Void> tsk_installLatestVersion = installLatestVersion();
// Thread install = new Thread(tsk_installLatestVersion);
// install.setDaemon(true);
// Logger.printTrace("install.start();");
// install.start();
// }
// };
// }
//
// private Task<Void> getLatestVersion()
// {
// return new Task<Void>()
// {
// @Override
// protected Void call() throws Exception
// {
// double maxSize = 0;
// double curSize = 0;
//
// updateProgress(0, 1);
// updateMessage(TextID.FORM_UPDATE_LABEL_CHECK.toString());
//
// try
// {
// URL url = new URL(GITHUB_RELEASE_URL);
// URLConnection conn = url.openConnection();
//
// BufferedReader html =
// new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
// curSize = maxSize = conn.getContentLength();
//
// while ((latestVersion = html.readLine()) != null)
// {
// curSize -= latestVersion.length();
//
// if (latestVersion.contains(RELEASE_VERSION_PREFIX))
// {
// int idx =
// latestVersion.indexOf(RELEASE_VERSION_PREFIX)
// + RELEASE_VERSION_PREFIX.length();
// latestVersion =
// latestVersion.substring(idx,
// idx + RELEASE_VERSION_TEMPLATE.length());
// // Latest version should be on top always
// break;
// }
//
// updateProgress(1 - curSize / maxSize, 1);
// }
//
// html.close();
//
// }
// catch (MalformedURLException e)
// {
// // Nothing here, due hard coded URL
// }
// catch (IOException e)
// {
// Logger.printError("Update failed! Connection problem!");
// this.failed();
// }
//
// if (latestVersion.length() == 0)
// {
// Logger.printError("No release found!");
// this.failed();
// }
//
// updateProgress(1, 1);
//
// return null;
// }
// };
// }
//
// private Task<Void> downloadLatestVersion()
// {
// return new Task<Void>()
// {
// @Override
// protected Void call() throws Exception
// {
// Logger.printTrace("Downloading...");
// double maxSize = 0;
// double curSize = 0;
//
// updateProgress(0, 1);
// updateMessage(TextID.FORM_UPDATE_LABEL_DOWNLOAD.toString());
//
// try
// {
// URL url =
// new URL(GITHUB_ARCHIVE_URL + latestVersion + RELEASE_ARCHIVE_EXTENSION);
//
// URLConnection conn = url.openConnection();
//
// maxSize = conn.getContentLength();
//
// downloaded = File.createTempFile(latestVersion, RELEASE_ARCHIVE_EXTENSION);
//
// InputStream is = conn.getInputStream();
// FileOutputStream fos = new FileOutputStream(downloaded.getCanonicalPath());
//
// byte[] buffer = new byte[Utilities.DEFAULT_BUFFER_SIZE];
// int len;
//
// while ((len = is.read(buffer)) > 0)
// {
// curSize += len;
// fos.write(buffer, 0, len);
//
// updateProgress(curSize, maxSize);
// }
//
// is.close();
// fos.close();
//
// }
// catch (FileNotFoundException e)
// {
// Logger.printError("Update failed! Unable to create file! Hello Linux
// users!");
// this.failed();
// }
// catch (IOException e)
// {
// Logger.printError("Update failed! Connection problem!");
// this.failed();
// }
//
// updateProgress(1, 1);
// Logger.printTrace("Download OK.");
//
// updateMessage(TextID.FORM_UPDATE_LABEL_INSTALL.toString());
// updateProgress(0, 1);
//
// try
// {
// Logger.printTrace("Unzipping...");
// newExecutable =
// new File(FormUpdate.class.getProtectionDomain().getCodeSource()
// .getLocation().toURI().getPath()
// + "~");
//
// ZipFile zip = new ZipFile(downloaded);
//
// Enumeration<?> enu = zip.entries();
//
// ZipEntry entry = null;
//
// // Find executable in archive
// while (enu.hasMoreElements()
// && !(entry = (ZipEntry) enu.nextElement()).toString().contains(
// RELEASE_EXECUTABLE_NAME))
// ;
//
// InputStream is = zip.getInputStream(entry);
// maxSize = entry.getSize();
// curSize = 0;
//
// FileOutputStream fos = new FileOutputStream(newExecutable);
//
// byte[] buffer = new byte[Utilities.DEFAULT_BUFFER_SIZE];
// int len;
//
// while ((len = is.read(buffer)) > 0)
// {
// curSize += len;
// updateProgress(curSize, maxSize);
// fos.write(buffer, 0, len);
// }
//
// is.close();
// fos.close();
// zip.close();
//
// }
// catch (IOException e1)
// {
// Logger.printError("Update failed! Unable to create file! Hello Linux
// users!");
// this.failed();
// }
//
// updateProgress(1, 1);
// Logger.printTrace("Unzip OK.");
//
// return null;
// }
// };
// }
//
// private Task<Void> installLatestVersion()
// {
// return new Task<Void>()
// {
// @Override
// protected Void call() throws Exception
// {
// new File(FormUpdate.class.getProtectionDomain().getCodeSource().getLocation()
// .toURI().getPath());
//
// newExecutable =
// new File(FormUpdate.class.getProtectionDomain().getCodeSource()
// .getLocation().toURI().getPath()
// + "~");
//
// Runtime.getRuntime().exec("updater.bat");
//
// Terminator.terminate(new Exceptions(XC.END));
//
// return null;
// }
// };
// }
//
// private EventHandler<WindowEvent> getOnShown()
// {
// return new EventHandler<WindowEvent>()
// {
// @Override
// public void handle(WindowEvent event)
// {
// currentVersion = "v" + Properties.SOFTWARE.VERSION + ".000";
//
// Task<Void> tsk_LatestVersion = getLatestVersion();
// tsk_LatestVersion.setOnSucceeded(getOnLatestVersionSucceded());
// tsk_LatestVersion.setOnFailed(getOnUpdateTaskFailed());
//
// pb_progress.rebind(tsk_LatestVersion.progressProperty(),
// tsk_LatestVersion.messageProperty());
//
// new Thread(tsk_LatestVersion).start();
// }
// };
//
// }
//
// private void showNotification(String newVersion)
// {
// b_update.setVisible(true);
// b_skip.setVisible(true);
// pb_progress.requestFocus();
// pb_progress.unbind();
//
// pb_progress.getLabel().setText(
// TextID.FORM_UPDATE_MSG_UPDATE_AVAILABLE.toString() + " "
// + TextID.FORM_UPDATE_LABEL_UPDATE.toString() + " to " + newVersion + " ?");
// }
//
// public FormUpdate(AbstractForm parent)
// {
// super(parent, TextID.FORM_UPDATE_LABEL_UPDATE.toString());
//
// b_update = new Button(TextID.FORM_UPDATE_LABEL_UPDATE.toString());
// b_skip = new Button(TextID.FORM_UPDATE_LABEL_SKIP.toString());
// b_update.setDefaultButton(true);
// b_update.setVisible(false);
// b_skip.setVisible(false);
//
// pb_progress = new ProgressBar("", 300, 30);
//
// GridPane.setHalignment(pb_progress, HPos.CENTER);
// GridPane.setHalignment(b_update, HPos.RIGHT);
//
// grid.addVElement(pb_progress, 0);
// grid.add(b_skip, 0);
// grid.add(b_update, 0);
//
// stage.setOnShown(getOnShown());
//
// b_update.setOnAction(getOnUpdateBtnAction());
// b_skip.setOnAction(getOnSkipBtnAction());
//
// open();
// }
//
// protected void onUserCloseRequest()
// {
// skipUpdate();
// }
//
// }
