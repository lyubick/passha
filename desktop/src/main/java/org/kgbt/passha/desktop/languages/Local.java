package org.kgbt.passha.desktop.languages;

import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Terminator;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.common.cfg.Settings;

public class Local {
    public enum Texts {
        // LABEL SINGLE-WORD
        LABEL_ABOUT(new String[]{"About " + Properties.SOFTWARE.NAME}),
        LABEL_AUTOLOGIN(new String[]{"Autologin"}),
        LABEL_CANCEL(new String[]{"Cancel"}),
        LABEL_COMMENT(new String[]{"Comment"}),
        LABEL_CONTACTS(new String[]{"Contacts"}),
        LABEL_CREATE(new String[]{"Create"}),
        LABEL_DELAY(new String[]{"Delay"}),
        LABEL_DELETE(new String[]{"Delete"}),
        LABEL_EDIT(new String[]{"Edit"}),
        LABEL_EMPTY(new String[]{""}),
        LABEL_ERROR(new String[]{"ERROR! "}),
        LABEL_EXIT(new String[]{"Exit"}),
        LABEL_FILE(new String[]{"File"}),
        LABEL_GITHUB(new String[]{"Github"}),
        LABEL_HELP(new String[]{"Help"}),
        LABEL_LENGTH(new String[]{"Length"}),
        LABEL_LINCENSE(new String[]{"License"}),
        LABEL_LOGIN(new String[]{"Login"}),
        LABEL_NAME(new String[]{"Name"}),
        LABEL_NEW(new String[]{"New"}),
        LABEL_OK(new String[]{"OK"}),
        LABEL_PASSWORD(new String[]{"Password"}),
        LABEL_PATH(new String[]{"Path"}),
        LABEL_REGISTER(new String[]{"Register"}),
        LABEL_RETYPE(new String[]{"Re-type"}),
        LABEL_SECONDS(new String[]{"Second(s)"}),
        LABEL_SETTINGS(new String[]{"Settings"}),
        LABEL_SHORTCUT(new String[]{"Shortcut"}),
        LABEL_SKIP(new String[]{"Skip"}),
        LABEL_SPECIAL_CHARACTERS(new String[]{"Special characters"}),
        LABEL_UNNAMED(new String[]{"Unnamed"}),
        LABEL_UPDATE(new String[]{"Update"}), // FIXME: Why not used?
        LABEL_URL(new String[]{"URL"}),
        LABEL_VAULT(new String[]{"Vault"}),
        LABEL_VAULT_WITH_COLLS(new String[]{"Vault: "}),
        LABEL_VERSION(new String[]{"v." + Properties.SOFTWARE.VERSION}),


        // LABEL MULTI-WORD
        LABEL_ENTER_PASSWORD(new String[]{"Enter password"}),
        LABEL_INVALID_PASSWORD(new String[]{"Password is incorrect!"}),
        LABEL_INVALID_PATH(new String[]{"Path is missing!"}),
        LABEL_MIGRATION_TITLE(new String[]{"User file migration"}),
        LABEL_UNKNOWN_USER(new String[]{"Unknown user"}),
        LABEL_LOGGING_IN(new String[]{"Logging user in..."}),

        // MESSAGES

        // MIGRATION
        MSG_MIGRATION_OCCURED(new String[]{"Legacy user file found."}),
        MSG_MIGRATION_ERRORS(new String[]{" password(s) failed to migrate."}),
        MSG_MIGRATION_SUCCESS(new String[]{"Successfully migrated user file."}),

        // ABOUT
        MSG_COPYRIGHT(new String[]{"Copyright (C) 2015  Andrejs Ļubimovs, Vladislavs Varslavāns"}),
        MSG_LICENSE_1(new String[]{
                "This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or(at your option) any later version."
        }),
        MSG_LICENSE_2(new String[]{
                "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details."
        }),
        MSG_LICENSE_3(new String[]{
                "You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>."
        }),

        MSG_PROGRAM_DESCRIPTION(new String[]{
                "This program is a perfectly safe Password Manager and Generator, that will allow User, while remembering only one Password, maintain different Passwords for all the WEB and beyond."
        }),

        // OTHER
        MSG_CONFIRM_UNSAFE_EXIT(new String[]{"There is unsaved passwords, exit?",}),
        MSG_DB_SYNC_FAILED(new String[]{"Database synchronization failed.",}),
        MSG_DB_SYNC_SUCCESS(new String[]{"Database is synchronized.",}),
        MSG_DB_SYNC_UNAVAILABLE(new String[]{"N/A"}),
        MSG_SHORTCUT_IN_USE_BY(new String[]{Texts.LABEL_ERROR.toString() + " Shortcut is in use by: "}),
        MSG_SHORTCUTS_MISSING(new String[]{"No shortcuts!"}),

        // NOTE: not used currently
        MSG_SHORTCUTS_MISSING_ACTION(new String[]{"To use 'shortcuts menu', it's necessary to bind at least one shortcut in a vault.",}),
        MSG_VAULT_ALREADY_OPENED(new String[]{"Vault already opened!"}),
        MSG_VAULTS_MISSING(new String[]{"There is no open vaults!"}),
        MSG_VAULTS_MISSING_ACTION(new String[]{"To use 'shortcuts menu' it's necessary to login to at least one vault.",}),


        /**************************************************************************************************************
         * CREATE PASSWORD FORM
         *************************************************************************************************************/
        FORM_CREATEPWD_LABEL_HEADER(new String[]{"Enter new password data"}),
        FORM_CREATEPWD_MSG_MISSING_PARAM(new String[]{Texts.LABEL_ERROR.toString() + "One or more mandatory fields are missing."}),
        FORM_CREATEPWD_MSG_MUST_HAVE_SPECIAL(new String[]{"Must contain $peci@l characters"}),
        FORM_CREATEPWD_MSG_MUST_HAVE_UPPER(new String[]{"Must have UPPER case character"}),
        FORM_CREATEPWD_MSG_NAME_EXISTS(new String[]{Texts.LABEL_ERROR.toString() + " Name already taken!"}),
        FORM_CREATEPWD_NAME(new String[]{"Create new password"}),

        /**************************************************************************************************************
         * DELETE PASSWORD FORM
         *************************************************************************************************************/
        FORM_DELETEPWD_MSG_NOTE(new String[]{"Enter this text to confirm deletion:"}),
        FORM_DELETEPWD_NAME(new String[]{"Delete password"}),
        FORM_DELETEPWD_MSG_CONFIRMATION(new String[]{"DELETE"}),

        /**************************************************************************************************************
         * EDIT PASSWORD FORM
         *************************************************************************************************************/
        FORM_EDITPWD_LABEL_HEADER(new String[]{"Edit password data"}),
        FORM_EDITPWD_NAME(new String[]{"Edit Password"}),

        /**************************************************************************************************************
         * LOGIN FORM
         *************************************************************************************************************/

        FORM_LOGIN_MSG_INCORRECT_PWD(new String[]{"Password incorrect!"}),
        FORM_LOGIN_MSG_PWDS_DONT_MATCH(new String[]{"Passwords don't match!"}),

        /**************************************************************************************************************
         * MANAGE PASSWORDS FORM
         *************************************************************************************************************/
        FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD(new String[]{"Copy to clipboard"}),
        FORM_MANAGEPWD_LABEL_AUTO_TYPE(new String[]{"Auto-Type"}),
        FORM_MANAGEPWD_LABEL_EXPORT(new String[]{"Export"}),
        FORM_MANAGEPWD_LABEL_PWD_NAME(new String[]{"Password name"}),
        FORM_MANAGEPWD_LABEL_RESET(new String[]{"Reset password"}),
        FORM_MANAGEPWD_NAME(new String[]{"Password Manager"}),

        /**************************************************************************************************************
         * RESET PASSWORD FORM
         *************************************************************************************************************/
        FORM_RESETPWD_MSG_WARNING(new String[]{"By pressing 'Ok' you will substitute current Password with the new Password. Thus current Password will be permanently lost!"}),
        FORM_RESETPWD_NAME(new String[]{"Reset password"}),
        FORM_RESETWD_LABEL_CURRENT(new String[]{"Current"}),

        /**************************************************************************************************************
         * SETTINGS PASSWORD FORM
         *************************************************************************************************************/

        /**************************************************************************************************************
         * UPDATE FORM
         *************************************************************************************************************/
        FORM_UPDATE_LABEL_CHECK(new String[]{"Searching for available updates..."}),
        FORM_UPDATE_LABEL_DOWNLOAD(new String[]{"Downloading updates..."}),
        FORM_UPDATE_LABEL_INSTALL(new String[]{"Installing..."}),
        FORM_UPDATE_MSG_UPDATE_AVAILABLE(new String[]{"New version available."}),

        /**************************************************************************************************************
         * MENU FORM
         *************************************************************************************************************/

        /**************************************************************************************************************
         * TRAY FORM
         *************************************************************************************************************/
        TRAY_MSG_FAILED_LOAD_SETTINGS(new String[]{"FAILED to load settings. Using DEFAULTS!"}),
        TRAY_MSG_PWD_COPIED_TO_CLIPBOARD(new String[]{"Password copied to Clipboard"}),
        TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD(new String[]{"Password removed from Clipboard"}),
        TRAY_MSG_TIME_LEFT(new String[]{"Time left"}),
        TRAY_MSG_FAILED_TO_AUTOLOGIN(new String[]{"Autologin failed."}),
        TRAY_MSG_FAILED_TO_UPDATE(new String[]{"Update failed."}),

        /**************************************************************************************************************
         * EXPORT FORM
         *************************************************************************************************************/
        FORM_EXPORT_LABEL_EXPORT(new String[]{"Export"}),
        FORM_EXPORT_MSG_SELECT_PATH(new String[]{"Select path to save export file."}),

        /**************************************************************************************************************
         * TEMPLATE
         *************************************************************************************************************/
        Z_ETHALON(new String[]
                {"", ""}),

        ;

        private final String[] text;

        Texts(final String[] text) {
            this.text = text;
        }

        @Override
        public String toString() {
            try {
                return text[Settings.getInstance().getLanguage()];
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }

            return "CRASH";
        }
    }
}
