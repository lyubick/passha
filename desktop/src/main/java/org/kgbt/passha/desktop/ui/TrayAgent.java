package org.kgbt.passha.desktop.ui;

import javafx.application.Platform;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.desktop.languages.Local.Texts;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
                Platform.runLater(() -> {
                    try
                    {
                        new FormShortcuts(null);
                    }
                    catch (Exceptions e12)
                    {
                        if (e12.getCode() == XC.VAULTS_NOT_FOUND)
                            self.showNotification(Texts.MSG_VAULTS_MISSING, Texts.MSG_VAULTS_MISSING_ACTION,
                                MessageType.WARNING);
                        else
                            Terminator.terminate(e12);
                    }
                });
            }

            @Override
            public void doubleClick(MouseEvent e)
            {
                Platform.runLater(() -> {
                    try
                    {
                        FormVaultsManager.getInstance().onUserRestoreRequest();
                    }
                    catch (Exceptions e1)
                    {
                        Logger.printFatal("Manage Passwords not yet created");
                    }
                });
            }
        };
    }

    private ActionListener onExit()
    {
        return arg0 -> Platform.runLater(() -> Terminator.terminate(new Exceptions(XC.END)));
    }

    private TrayAgent() throws Exceptions
    {
        SystemTray sysTray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource(Properties.PATHS.TRAY_ICON));
        PopupMenu popup = new PopupMenu();
        MenuItem itemExit = new MenuItem(Texts.LABEL_EXIT.toString());

        popup.add(itemExit);

        trayIcon = new TrayIcon(image, Properties.SOFTWARE.NAME + " " + Texts.LABEL_VERSION.toString(), popup);

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

    public void showNotification(Texts title, MessageType type)
    {
        trayIcon.displayMessage(title.toString(), "", type);
    }

    public void showNotification(Texts title, Texts msg, MessageType type)
    {
        trayIcon.displayMessage(title.toString(), msg.toString(), type);
    }

    public void showNotification(Texts title, Texts msg, String addition, MessageType type)
    {
        trayIcon.displayMessage(title.toString(), msg.toString() + addition, type);
    }
}
