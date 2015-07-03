package ui;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import javafx.application.Platform;
import languages.Texts.TextID;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;

public class TrayAgent
{
    private static TrayAgent self     = null;
    private TrayIcon         trayIcon = null;

    public static TrayAgent getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    private TrayAgent() throws Exceptions
    {
        SystemTray sysTray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("resources/tray_icon.png"));
        PopupMenu popup = new PopupMenu();
        MenuItem itemExit = new MenuItem(TextID.FORM_MENU_LABEL_EXIT.toString());

        popup.add(itemExit);

        trayIcon =
                new TrayIcon(image, TextID.COMMON_LABEL_APP_NAME.toString() + " "
                        + TextID.COMMON_LABEL_VERSION.toString(), popup);

        trayIcon.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent arg0)
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ManagePasswordsForm.maximize();
                    }
                });
            }
        });

        itemExit.addActionListener(new ActionListener()
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
        });

        try
        {
            sysTray.add(trayIcon);
        }
        catch (Exception e)
        {
            throw new Exceptions(XC.INIT_FAILURE);
        }
    }

    public static TrayAgent init() throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        if (SystemTray.isSupported())
            return self = new TrayAgent();
        else
            throw new Exceptions(XC.INIT_FAILURE);
    }

    public void showNotification(String title, String msg, MessageType type)
    {
        trayIcon.displayMessage(title, msg, type);
    }
}
