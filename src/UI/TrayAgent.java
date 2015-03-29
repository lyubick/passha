/**
 * 
 */
package UI;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

import Common.Exceptions;
import Logger.Logger;
import Main.ABEND;
import Main.Main;
import UI.Controller.FORMS;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * @author curious-odd-man
 *
 */
public class TrayAgent
{
    public static void addTray(Stage primaryStage)
    {
        if (SystemTray.isSupported())
        {
            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage("resources/tray_icon.png");
            PopupMenu popup = new PopupMenu();
            MenuItem item = new MenuItem("Exit");

            popup.add(item);

            TrayIcon trayIcon = new TrayIcon(image, "Amr_Trial", popup);

            ActionListener listener = new ActionListener()
            {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent arg0)
                {
                    // TODO Auto-generated method stub
                    System.exit(0);
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
                System.err.println("Can't add to tray");
            }
        }
        else
        {
            System.err.println("Tray unavailable");
        }
    }
}