/**
 *
 */
package languages;

import main.Settings;

/**
 * @author curious-odd-man
 *
 */
public class Texts
{
    // TODO: Use String.split to parse CSV file with texts
    public enum TextID
    {
        ADD_SPECIAL_PASSWORD(new String[]
        { "Add Special Password", "�������� ����� ������" }),

        CALCULATING(new String[]
        { "Calculating...", "���������..." }),

        CANCEL(new String[]
        { "Cancel", "��������" }),

        CHANGE_PWD_HEADER(new String[]
        { "Password reset", "���������� ������" }),

        CHANGE_PWD_WARNING(
                new String[]
                {
                        "By pressing OK you will substitute Current Password with New one.\n Thus Current Password will be permanently lost!",
                        "����� \"�����������\" �� �������� ��������� ������ �����.\n � ����������� ��������� ������ ����� ������������ ������!" }),

        COMMENT(new String[]
        { "Comment", "�����������" }),

        COPY_CLIPBOARD(new String[]
        { "Copy To Clipboard", "����������� � ����� ������" }),

        CREATE(new String[]
        { "Create", "�������" }),

        CURRENT_PWD(new String[]
        { "Current", "���������" }),

        DELAY(new String[]
        { "Delay", "��������" }),

        DELETE(new String[]
        { "Delete", "�������" }),

        DISCARD(new String[]
        { "Discard", "��������" }),

        ENTER_PASSWORD(new String[]
        { "Enter Password", "������� ������" }),

        ERR_MISSING_PASSWORD_NAME(new String[]
        { "Error! Password name must be entered!", "������! ���������� ������ ��� ������!" }),

        ERR_NAME_ALREADY_TAKEN(new String[]
        { "Error! Name already taken!", "������! ��� ��� ������!" }),

        EXIT(new String[]
        { "Exit", "�����" }),

        EXPORT(new String[]
        { "Export", "��������������" }),

        FILE(new String[]
        { "File", "����" }),

        GREETING(new String[]
        { "Welcome to pasSHA!", "����� ���������� � pasSHA!" }),

        LANGUAGE(new String[]
        { "Language", "����" }),

        LENGTH(new String[]
        { "Length", "������" }),

        LOADING(new String[]
        { "Loading", "��������" }),

        LOGIN(new String[]
        { "Login", "�����������" }),

        MANAGE(new String[]
        { "Manage", "����������" }),

        MS(new String[]
        { "milliseconds", "������������" }),

        MUST_CONTAIN_UPPER_CASE_CHAR(new String[]
        { "Must have UPPER case character", "������ ����� ��������� �����" }),

        MUST_CONTAINT_SPECIAL_CHARS(new String[]
        { "Must contain special characters", "������ ����� ����������� �������" }),

        NAME(new String[]
        { "Name", "���" }),

        NEW(new String[]
        { "New", "��������" }),

        OK(new String[]
        { "OK", "�����������" }),

        PASSWORD_COPIED(new String[]
        { "Password copied to Clipboard", "������ ������� � ����� ������" }),

        PASSWORD_INCORRECT(new String[]
        { "Password incorrect!", "�� ������ ������!" }),

        PASSWORD_REMOVED(new String[]
        { "Password removed from Clipboard", "������ ����� �� ������ ������" }),

        PASSWORDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "������ �� ���������" }),

        PROGRAM_NAME(new String[]
        { "pasSHA", "pasSHA" }),

        PWD(new String[]
        { "Password", "������" }),

        PWD_NAME(new String[]
        { "Password name", "��� ������" }),

        REGISTER(new String[]
        { "Register", "����������������" }),

        RESET_PASSWORD(new String[]
        { "Reset password", "�������� ������" }),

        RETYPE(new String[]
        { "Re-type", "���������" }),

        S(new String[]
        { "second(s)", "������(�)" }),

        SAVE(new String[]
        { "Save", "���������" }),

        SAVE_CONFIRMATION(new String[]
        { "There are unsaved changes. Save before exit?",
                "���� ������������ ���������. ���������?" }),

        SAVING(new String[]
        { "Saving", "����������" }),

        SETTINGS(new String[]
        { "Settings", "���������" }),

        SHORTCUT(new String[]
        { "Shortcut", "������� �������" }),

        SPECIAL_CHARACTERS(new String[]
        { "Special characters", "����������� �������" }),

        TIME_LEFT(new String[]
        { "Time left", "������� ��������" }),

        URL(new String[]
        { "URL", "URL" }),

        VERSION(new String[]
        { "v." + version, "�." + version }),

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
            return text[Settings.getLanguage()];
        }
    }

    static String version = "2.0.000";
}
