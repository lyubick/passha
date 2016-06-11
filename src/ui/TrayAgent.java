package ui;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

import javafx.application.Platform;
import languages.Local.TextID;
import logger.Logger;
import main.Exceptions;
import main.Properties;
import main.Terminator;
import main.Exceptions.XC;

public class TrayAgent
{
    public class ClickListener extends MouseAdapter implements ActionListener
    {
        MouseEvent lastEvent;
        Timer      timer;

        public ClickListener()
        {
            timer = new Timer((int) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval"), this);
        }

        public void mouseClicked(MouseEvent e)
        {
            if (e.getClickCount() > 2) return;

            lastEvent = e;

            if (timer.isRunning())
            {
                timer.stop();
                doubleClick(lastEvent);
            }
            else
            {
                timer.restart();
            }
        }

        public void singleClick(MouseEvent e)
        {

        }

        public void doubleClick(MouseEvent e)
        {
        }

        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            timer.stop();
            singleClick(lastEvent);
        }
    }

    private static TrayAgent self     = null;
    private TrayIcon         trayIcon = null;

    public static TrayAgent getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    ClickListener lmbHandler()
    {
        return new ClickListener()
        {
            @Override
            public void singleClick(MouseEvent e)
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            FormVaultsManager.getInstance();// TODO: whyyy???
                            new FormShortcuts(null);
                            // FIXME unhandled exception
                        }
                        catch (Exceptions e)
                        {
                            // do nothing
                        }
                    }
                });
            }

            @Override
            public void doubleClick(MouseEvent e)
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            FormVaultsManager.getInstance().maximize();
                        }
                        catch (Exceptions e)
                        {
                            Logger.printDebug("Manage Passwords not yet created");
                        }
                    }
                });
            }
        };
    }

    private ActionListener onExit()
    {
        return new ActionListener()
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
    }

    private TrayAgent() throws Exceptions
    {
        SystemTray sysTray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("resources/tray_icon.png"));
        PopupMenu popup = new PopupMenu();
        MenuItem itemExit = new MenuItem(TextID.MENU_LABEL_EXIT.toString());

        popup.add(itemExit);

        trayIcon = new TrayIcon(image, Properties.SOFTWARE.NAME + " " + TextID.COMMON_LABEL_VERSION.toString(), popup);

        trayIcon.addMouseListener(lmbHandler());

        itemExit.addActionListener(onExit());

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

    @Deprecated
    public void showNotification(String title, String msg, MessageType type)
    {
        trayIcon.displayMessage(title, msg, type);
    }

    public void showNotification(TextID title, MessageType type)
    {
        trayIcon.displayMessage(title.toString(), "", type);
    }

    public void showNotification(TextID title, TextID msg, MessageType type)
    {
        trayIcon.displayMessage(title.toString(), msg.toString(), type);
    }

    public void showNotification(TextID title, TextID msg, String addition, MessageType type)
    {
        trayIcon.displayMessage(title.toString(), msg.toString() + addition, type);
    }
}
