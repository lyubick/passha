/**
 *
 */
package ui;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

import languages.Texts.TextID;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import ui.Controller.FORMS;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class TrayAgent
{
    static TrayAgent self = null;

    public static TrayAgent getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    private TrayAgent(Stage primaryStage)
    {
        SystemTray tray = SystemTray.getSystemTray();

        Image image =
                Toolkit.getDefaultToolkit().getImage(
                        ClassLoader.getSystemResource("resources/tray_icon.png"));
        PopupMenu popup = new PopupMenu();
        MenuItem item = new MenuItem("Exit");

        popup.add(item);

        TrayIcon trayIcon =
                new TrayIcon(image, TextID.PROGRAM_NAME.toString() + " "
                        + TextID.VERSION.toString(), popup);

        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent arg0)
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Terminator.terminate(new Exceptions(XC.END));
                    }
                });
            }
        };

        ActionListener listenerTray = new ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent arg0)
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Controller.getInstance().switchForm(FORMS.CURRENT);
                        }
                        catch (Exceptions e)
                        {
                            Terminator.terminate(e);
                        }
                    }
                });
            }
        };

        trayIcon.addActionListener(listenerTray);
        item.addActionListener(listener);

        try
        {
            tray.add(trayIcon);
        }
        catch (Exception e)
        {
            Logger.printError("Can't add to tray");
        }
    }

    public static void initTrayAgent(Stage primaryStage) throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        if (SystemTray.isSupported())
            self = new TrayAgent(primaryStage);
        else
            Logger.printError("Tray unavailable");
    }
}
