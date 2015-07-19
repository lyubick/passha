/**
 *
 */
package languages;

import main.Exceptions;
import main.Settings;
import main.Terminator;

/**
 * @author curious-odd-man
 *
 */
public class Texts
{
    //@fmt:off
    /**************************************************************************************************************
     * SYNTAX
     *
     * <SCOPE>_[<SCOPE_NAME>]_<ELEMENT>_[<ELEMENT_NAME>]
     *
     * ELEMENT : {
     *              LABEL - captions
     *              NAME - window header
     *              MSG  - text
     *           }
     *************************************************************************************************************/
    //@fmt:on
    public enum TextID
    {
        /**************************************************************************************************************
         * COMMON TEXTS
         *************************************************************************************************************/
        COMMON_APPLICATION_NAME(new String[]
        { "pasSHA", "pasSHA" }),

        COMMON_LABEL_CANCEL(new String[]
        { "Cancel", "��������" }),

        COMMON_LABEL_ERROR(new String[]
        { "ERROR!", "������!" }),

        COMMON_LABEL_NEW(new String[]
        { "New", "�����" }),

        COMMON_LABEL_OK(new String[]
        { "OK", "OK" }),

        COMMON_LABEL_SECONDS(new String[]
        { "second(s)", "������(�)" }),

        COMMON_LABEL_VERSION(new String[]
        { "v." + version, "�." + version }),

        /**************************************************************************************************************
         * CREATE PASSWORD FORM
         *************************************************************************************************************/
        FORM_CREATEPWD_LABEL_COMMENT(new String[]
        { "Comment", "�����������" }),

        FORM_CREATEPWD_LABEL_CREATE(new String[]
        { "Create", "�������" }),

        FORM_CREATEPWD_LABEL_LENGTH(new String[]
        { "Length", "������" }),

        FORM_CREATEPWD_LABEL_NAME(new String[]
        { "Name", "���" }),

        FORM_CREATEPWD_LABEL_SPECIAL_CHARACTERS(new String[]
        { "Special characters", "����������� �������" }),

        FORM_CREATEPWD_LABEL_URL(new String[]
        { "URL", "URL" }),

        FORM_CREATEPWD_MANAGER_LABEL_DELETE(new String[]
        { "Delete", "�������" }),

        FORM_CREATEPWD_MSG_MISSING_PARAM(new String[]
        {
                TextID.COMMON_LABEL_ERROR.toString() + "One or more mandatory fields are missing.",
                TextID.COMMON_LABEL_ERROR.toString()
                        + "���� ��� ����� ������������ ����� �� ���������." }),

        FORM_CREATEPWD_MSG_MUST_HAVE_SPECIAL(new String[]
        { "Must contain special characters", "������ ����� ����������� �������" }),

        FORM_CREATEPWD_MSG_MUST_HAVE_UPPER(new String[]
        { "Must have UPPER case character", "������ ����� ��������� �����" }),

        FORM_CREATEPWD_MSG_NAME_EXISTS(new String[]
        { TextID.COMMON_LABEL_ERROR.toString() + " Name already taken!",
                TextID.COMMON_LABEL_ERROR.toString() + " ��� ��� ������!" }),

        FORM_CREATEPWD_NAME(new String[]
        { "Create new password", "������� ����� ������" }),

        /**************************************************************************************************************
         * DELETE PASSWORD FORM
         *************************************************************************************************************/
        FORM_DELETEPWD_MSG_NOTE(
                new String[]
                { "Enter this text to confirm deletion:",
                        "������� ���� �����, ����� ����������� ��������:" }),

        FORM_DELETEPWD_NAME(new String[]
        { "Delete password", "������� ������" }),

        /**************************************************************************************************************
         * EDIT PASSWORD FORM
         *************************************************************************************************************/
        FORM_EDITPWD_LABEL_SHORTCUT(new String[]
        { "Shortcut", "������� �������" }),

        FORM_EDITPWD_NAME(new String[]
        { "Edit Password", "������������� ������" }),

        FORM_EDITPWD_MSG_SHORTCUT_IN_USE(new String[]
        { TextID.COMMON_LABEL_ERROR.toString() + " Shortcut is in use by: ",
                TextID.COMMON_LABEL_ERROR.toString() + " ������� ������� ������: " }),

        /**************************************************************************************************************
         * LOGIN FORM
         *************************************************************************************************************/
        FORM_LOGIN_LABEL_ENTER_PWD(new String[]
        { "Enter Password", "������� ������" }),

        FORM_LOGIN_LABEL_LOGIN(new String[]
        { "Login", "�����" }),

        FORM_LOGIN_LABEL_PASSWORD(new String[]
        { "Password", "������" }),

        FORM_LOGIN_LABEL_REGISTER(new String[]
        { "Register", "����������������" }),

        FORM_LOGIN_LABEL_RETYPE(new String[]
        { "Re-type", "��������" }),

        FORM_LOGIN_MSG_INCORRECT_PWD(new String[]
        { "Password incorrect!", "�������� ������!" }),

        FORM_LOGIN_MSG_PWDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "������ �� ���������!" }),

        FORM_LOGIN_NAME(new String[]
        { "Login", "����" }),

        /**************************************************************************************************************
         * MANAGE PASSWORDS FORM
         *************************************************************************************************************/
        FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD(new String[]
        { "Copy To Clipboard", "��������� � ����� ������" }),

        FORM_MANAGEPWD_LABEL_EXPORT(new String[]
        { "Export", "��������������" }),

        FORM_MANAGEPWD_LABEL_PWD_NAME(new String[]
        { "Password name", "�������� ������" }),

        FORM_MANAGEPWD_LABEL_RESET(new String[]
        { "Reset password", "�������� ������" }),

        FORM_MANAGEPWD_NAME(new String[]
        { "Password Manager", "���������� ��������" }),

        FORM_MANAGEWD_LABEL_EDIT(new String[]
        { "Edit", "�������������" }),

        /**************************************************************************************************************
         * RESET PASSWORD FORM
         *************************************************************************************************************/
        FORM_RESETPWD_MSG_WARNING(
                new String[]
                {
                        "By pressing \'OK\' you will substitute current Password with the new Password.\n Thus current Password will be permanently lost!",
                        "����� \'OK\' �� �������� ������� ������ �� �����.\n � ����������� ������� ������ ����� ������������ ������!" }),

        FORM_RESETPWD_NAME(new String[]
        { "Reset password", "�������� ������" }),

        FORM_RESETWD_LABEL_CURRENT(new String[]
        { "Current", "�������" }),

        /**************************************************************************************************************
         * SETTINGS PASSWORD FORM
         *************************************************************************************************************/
        FORM_SETTINGS_LABEL_DELAY(new String[]
        { "Delay", "��������" }),

        FORM_SETTINGS_LABEL_LANGUAGE(new String[]
        { "Language", "����" }),

        FORM_SETTINGS_NAME(new String[]
        { "Settings", "���������" }),

        /**************************************************************************************************************
         * UPDATE FORM
         *************************************************************************************************************/
        FORM_UPDATE_LABEL_CHECK(new String[]
        { "Searching for available updates...", "����� ��������� ����������..." }),

        FORM_UPDATE_LABEL_DOWNLOAD(new String[]
        { "Downloading updates...", "�������� ����������..." }),

        FORM_UPDATE_LABEL_INSTALL(new String[]
        { "Installing...", "���������..." }),

        FORM_UPDATE_LABEL_RESTART(new String[]
        { "YOUR TEXT HERE", "YOUR TEXT HERE" }), // TODO

        FORM_UPDATE_LABEL_UPDATE(new String[]
        { "Update", "��������" }),

        FORM_UPDATE_LABEL_SKIP(new String[]
        { "Skip", "����������" }),

        FORM_UPDATE_MSG_UPDATE_AVAILABLE(new String[]
        { "New version available.", "�������� ����� ������." }),

        /**************************************************************************************************************
         * MENU FORM
         *************************************************************************************************************/
        MENU_LABEL_EXIT(new String[]
        { "Exit", "�����" }),

        MENU_LABEL_FILE(new String[]
        { "File", "����" }),

        /**************************************************************************************************************
         * TRAY FORM
         *************************************************************************************************************/
        TRAY_MSG_FAILED_LOAD_SETTINGS(new String[]
        { "FAILED to load settings. Using DEFAULTS!",
                "�� ������� ��������� ���������. ������������ ��������� ��-���������!" }),

        TRAY_MSG_PWD_COPIED_TO_CLIPBOARD(new String[]
        { "Password copied to Clipboard", "������ ������� � ����� ������" }),

        TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD(new String[]
        { "Password removed from Clipboard", "������ ����� �� ������ ������" }),

        TRAY_MSG_TIME_LEFT(new String[]
        { "Time left", "������� ��������" }),

        TRAY_MSG_FAILED_TO_UPDATE(new String[]
        { "Update failed.", "���������� �� �������." }),

        /**************************************************************************************************************
         * TEMPLATE
         *************************************************************************************************************/
        Z_ETHALON(new String[]
        { "", "" }),

        ;

        private final String text[];

        private TextID(final String[] text)
        {
            this.text = text;
        }

        @Override
        public String toString()
        {
            try
            {
                return text[Settings.getInstance().getLanguage()];
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }

            return "CRASH";
        }
    }

    static private String version = "1.2";

    public static String getVersion()
    {
        return version;
    }

}
