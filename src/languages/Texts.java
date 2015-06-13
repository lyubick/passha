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
    // TODO: Use String.split to parse CSV file with texts
    public enum TextID
    {
        COMMON_LABEL_APP_NAME(new String[]
        { "pasSHA", "pasSHA" }),

        COMMON_LABEL_CANCEL(new String[]
        { "Cancel", "��������" }),

        COMMON_LABEL_ERROR(new String[]
        { "ERROR!", "������!" }),

        COMMON_LABEL_OK(new String[]
        { "OK", "OK" }),

        COMMON_LABEL_SECONDS(new String[]
        { "second(s)", "������(�)" }),

        COMMON_LABEL_VERSION(new String[]
        { "v." + version, "�." + version }),

        FORM_LOGIN_ERROR_PWD_INCORRECT(new String[]
        { "Password incorrect!", "�������� ������!" }),

        FORM_LOGIN_ERROR_PWDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "������ �� ���������!" }),

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

        FORM_MENU_LABEL_EXIT(new String[]
        { "Exit", "�����" }),

        FORM_MENU_LABEL_FILE(new String[]
        { "File", "����" }),

        FORM_PWD_CHANGE_LABEL_CURRENT(new String[]
        { "Current", "��������" }),

        FORM_PWD_CHANGE_LABEL_NEW(new String[]
        { "New", "�����" }),

        FORM_PWD_CHANGE_NAME(new String[]
        { "Update Password", "�������� ������" }),

        FORM_PWD_CHANGE_WARNING(
                new String[]
                {
                        "By pressing \'OK\' you will substitute current Password with the new Password.\n Thus current Password will be permanently lost!",
                        "����� \'OK\' �� �������� ������������ ������ �� �����.\n � ����������� ������������ ������ ����� ������������ ������!" }),

        FORM_PWD_MANAGER_LABEL_COPY_TO_CLIPBOARD(new String[]
        { "Copy To Clipboard", "����������� � ����� ������" }),

        FORM_PWD_MANAGER_LABEL_EXPORT(new String[]
        { "Export", "��������������" }),

        FORM_PWD_MANAGER_LABEL_RESET(new String[]
        { "Reset password", "�������� ������" }),

        FORM_PWD_MANAGER_TABLE_LABEL_PWD_NAME(new String[]
        { "Password name", "�������� ������" }),

        FORM_SETTINGS_LABEL_DELAY(new String[]
        { "Delay", "��������" }),

        FORM_SETTINGS_LABEL_LANGUAGE(new String[]
        { "Language", "����" }),

        FORM_SETTINGS_NAME(new String[]
        { "Settings", "���������" }),

        FORM_SP_LABEL_COMMENT(new String[]
        { "Comment", "�����������" }),

        FORM_SP_LABEL_CREATE(new String[]
        { "Create", "�������" }),

        FORM_SP_LABEL_ERROR_MISSING_PARAM(
                new String[]
                { "Error! One or more mandatory fields are missing.",
                        "������! ���� ��� ����� ������������ ����� �� ���������." }),

        FORM_SP_LABEL_ERROR_NAME_EXISTS(new String[]
        { "Error! Name already taken!", "������! ��� ��� ������!" }),

        FORM_SP_LABEL_LENGTH(new String[]
        { "Length", "������" }),

        FORM_SP_LABEL_MUST_HAVE_SPECIAL(new String[]
        { "Must contain special characters", "������ ����� ����������� �������" }),

        FORM_SP_LABEL_MUST_HAVE_UPPER(new String[]
        { "Must have UPPER case character", "������ ����� ��������� �����" }),

        FORM_SP_LABEL_NAME(new String[]
        { "Name", "���" }),

        FORM_SP_LABEL_SPECIAL_CHARACTERS(new String[]
        { "Special characters", "����������� �������" }),

        FORM_SP_LABEL_URL(new String[]
        { "URL", "URL" }),

        FORM_SP_MANAGER_LABEL_DELETE(new String[]
        { "Delete", "�������" }),

        FORM_SP_NAME(new String[]
        { "Create new password", "������� ����� ������" }),

        TRAY_MESSAGE_ERROR_FAILED_LOAD_SETTINGS(new String[]
        { "FAILED to load settings. Using DEFAULTS!",
                "�� ������� ��������� ���������. ������������ ��������� ��-���������!" }),

        TRAY_MESSAGE_INFO_COPIED_TO_CLIPBOARD(new String[]
        { "Password copied to Clipboard", "������ ������� � ����� ������" }),

        TRAY_MESSAGE_INFO_PWD_REMOVED_FROM_CLIPBOARD(new String[]
        { "Password removed from Clipboard", "������ ����� �� ������ ������" }),

        TRAY_MESSAGE_TIME_LEFT(new String[]
        { "Time left", "������� ��������" }),

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

    static String version = "2.0.000";
}
