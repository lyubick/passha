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
import main.Exceptions;
import ui.Controller.FORMS;
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

            TrayIcon trayIcon =
                    new TrayIcon(image, TextID.PROGRAM_NAME.toString() + " "
                            + TextID.VERSION.toString(), popup);

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
                            try
                            {
                                Controller.getInstance().switchForm(FORMS.CURRENT);
                            }
                            catch (Exceptions e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
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
                System.err.println("Can't add to tray");
            }
        }
        else
        {
            System.err.println("Tray unavailable");
        }
    }
}
