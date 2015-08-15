package ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Hyperlink;
import ui.elements.Button;
import ui.elements.GridPane;
import ui.elements.Label;
import languages.Texts.TextID;
import logger.Logger;
import main.Properties;

public class FormAbout extends AbstractForm
{
    Label             l_header         = null;
    Label             l_contacts       = null;
    Label             l_licDescription = null;
    Label             l_licCopyright   = null;
    Label             l_licFreeware    = null;
    Label             l_licWarranty    = null;
    Label             l_licGnuGpl      = null;
    Button            b_ok             = null;
    Button            b_license        = null;
    Button            b_github         = null;
    Hyperlink         e_mail_v         = null;
    Hyperlink         e_mail_a         = null;

    private final int WRAP_WIDTH       = 600;

    /* EVENT HANDLERS & CHANGE LISTENERS */
    private EventHandler<ActionEvent> getOnOKBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                close();
            }
        };
    }

    private EventHandler<ActionEvent> getOnLicenseBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    Logger.printDebug("URI" + System.getProperty("user.dir") + File.separator
                            + "GNUGPLv3.txt");
                    Desktop.getDesktop().edit(
                            new File(System.getProperty("user.dir") + File.separator
                                    + "GNUGPLv3.txt"));
                }
                catch (IOException e)
                {
                    Logger.printError("Failed to open Licence! " + e.getMessage());
                }

            }
        };
    }

    private EventHandler<ActionEvent> getOnGithubBtnAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                try
                {
                    Desktop.getDesktop().browse(new URI("https://github.com/lyubick/passha"));
                }
                catch (URISyntaxException | IOException e)
                {
                    Logger.printError("Failed to open github! " + e.getMessage());
                }
            }
        };
    }

    private EventHandler<ActionEvent> getOnHyperlinkMailAction()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                String subject =
                        Properties.SOFTWARE.NAME + " v." + Properties.SOFTWARE.VERSION
                                + "user report.";
                try
                {
                    Desktop.getDesktop().mail(
                            URI.create("mailto:" + e_mail_a.getText() + ";" + e_mail_v.getText()
                                    + "?subject=" + URLEncoder.encode(subject, "UTF-8")));
                }
                catch (IOException e)
                {
                    Logger.printError("Failed to open mail client! " + e.getMessage());
                }
            }
        };
    }

    protected FormAbout(AbstractForm parent)
    {
        super(parent, TextID.MENU_LABEL_ABOUT);
        priority = WindowPriority.ALWAYS_ON_TOP;

        l_header = new Label(Properties.SOFTWARE.NAME + " v." + Properties.SOFTWARE.VERSION);
        l_header.beHeader();
        l_contacts = new Label(TextID.FORM_ABOUT_LABEL_CONTACTS, WRAP_WIDTH);
        l_contacts.beHeader();
        l_licDescription = new Label(TextID.FORM_ABOUT_MSG_LICENSE_DESCRIPTION, WRAP_WIDTH);
        l_licCopyright = new Label(TextID.FORM_ABOUT_MSG_LICENSE_COPYRIGHT, WRAP_WIDTH);
        l_licFreeware = new Label(TextID.FORM_ABOUT_MSG_LICENSE_FREEWARE, WRAP_WIDTH);
        l_licWarranty = new Label(TextID.FORM_ABOUT_MSG_LICENSE_WARRANTY, WRAP_WIDTH);
        l_licGnuGpl = new Label(TextID.FORM_ABOUT_MSG_LICENSE_GNU_LICENCE, WRAP_WIDTH);

        b_ok = new Button(TextID.COMMON_LABEL_OK);
        b_license = new Button(TextID.FORM_ABOUT_LABEL_LINCENSE);
        b_github = new Button(TextID.FORM_ABOUT_LABEL_GITHUB);

        e_mail_v = new Hyperlink("vlad.varslavans@gmail.com");
        e_mail_a = new Hyperlink("andrejs.lubimovs@gmail.com");

        e_mail_a.setOnAction(getOnHyperlinkMailAction());
        e_mail_v.setOnAction(getOnHyperlinkMailAction());

        grid.addHElement(l_header);
        grid.addHElement(l_licDescription);
        grid.addHElement(l_licCopyright);
        grid.addHElement(l_licFreeware);
        grid.addHElement(l_licWarranty);
        grid.addHElement(l_licGnuGpl);
        grid.addHElement(l_contacts);
        grid.addHElement(e_mail_v);
        grid.addHElement(e_mail_a);

        grid.addAll(0, b_ok, b_license, b_github);

        b_ok.setOnAction(getOnOKBtnAction());
        b_license.setOnAction(getOnLicenseBtnAction());
        b_github.setOnAction(getOnGithubBtnAction());

        GridPane.setHalignment(l_header, HPos.CENTER);
        GridPane.setHalignment(l_contacts, HPos.RIGHT);
        GridPane.setHalignment(e_mail_v, HPos.RIGHT);
        GridPane.setHalignment(e_mail_a, HPos.RIGHT);
        GridPane.setHalignment(b_ok, HPos.LEFT);
        GridPane.setHalignment(b_license, HPos.CENTER);
        GridPane.setHalignment(b_github, HPos.RIGHT);

        open();
        b_ok.requestFocus();
    }
}