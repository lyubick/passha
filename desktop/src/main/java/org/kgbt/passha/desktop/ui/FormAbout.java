package org.kgbt.passha.desktop.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Hyperlink;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.desktop.languages.Local.Texts;
import org.kgbt.passha.desktop.ui.elements.Button;
import org.kgbt.passha.desktop.ui.elements.GridPane;
import org.kgbt.passha.desktop.ui.elements.Label;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

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
    private EventHandler<ActionEvent> getOnLicenseBtnAction()
    {
        return event -> {
            try
            {
                Logger.printDebug("URI: '" + System.getProperty("user.dir") + File.separator + "GNUGPLv3.txt'");
                Desktop.getDesktop()
                    .edit(new File(System.getProperty("user.dir") + File.separator + "GNUGPLv3.txt"));
            }
            catch (IOException e)
            {
                Logger.printError("Failed to open Licence! " + e.getMessage());
            }

        };
    }

    private EventHandler<ActionEvent> getOnGithubBtnAction()
    {
        return event -> {
            try
            {
                Desktop.getDesktop().browse(new URI("https://github.com/lyubick/passha"));
            }
            catch (URISyntaxException | IOException e)
            {
                Logger.printError("Failed to open github! " + e.getMessage());
            }
        };
    }

    private EventHandler<ActionEvent> getOnHyperlinkMailAction()
    {
        return event -> {
            String subject = Properties.SOFTWARE.NAME + " v." + Properties.SOFTWARE.VERSION + "user report.";
            try
            {
                Desktop.getDesktop().mail(URI.create("mailto:" + e_mail_a.getText() + ";" + e_mail_v.getText()
                    + "?subject=" + URLEncoder.encode(subject, "UTF-8")));
            }
            catch (IOException e)
            {
                Logger.printError("Failed to open mail client! " + e.getMessage());
            }
        };
    }

    protected FormAbout(AbstractForm parent) throws Exceptions
    {
        super(parent, Texts.LABEL_ABOUT, WindowPriority.ALWAYS_ON_TOP, false);

        l_header = new Label(Properties.SOFTWARE.NAME + " v." + Properties.SOFTWARE.VERSION);
        l_header.beHeader();
        l_contacts = new Label(Texts.LABEL_CONTACTS, WRAP_WIDTH);
        l_contacts.beHeader();
        l_licDescription = new Label(Texts.MSG_PROGRAM_DESCRIPTION, WRAP_WIDTH);
        l_licCopyright = new Label(Texts.MSG_COPYRIGHT, WRAP_WIDTH);
        l_licFreeware = new Label(Texts.MSG_LICENSE_1, WRAP_WIDTH);
        l_licWarranty = new Label(Texts.MSG_LICENSE_2, WRAP_WIDTH);
        l_licGnuGpl = new Label(Texts.MSG_LICENSE_3, WRAP_WIDTH);

        b_ok = new Button(Texts.LABEL_OK);
        b_license = new Button(Texts.LABEL_LINCENSE);
        b_github = new Button(Texts.LABEL_GITHUB);

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

        b_ok.setOnAction(event -> close());
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
