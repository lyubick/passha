/**
 *
 */
package Languages;

/**
 * @author curious-odd-man
 *
 */
public class Texts
{
    public enum Language
    {
        ENGLISH,
        RUSSIAN,
    }

    static String version = "dev";

    // TODO: Use String.split to parse CSV file with texts
    public enum TextID
    {
        ADD_SPECIAL_PASSWORD(new String[]
        { "Add Special Password", "" }),

        CANCEL(new String[]
        { "Cancel", "��������" }),

        COMMENT(new String[]
        { "Comment", "" }),

        CREATE(new String[]
        { "Create", "" }),

        DELETE(new String[]
        { "Delete", "�������" }),

        ENABLED(new String[]
        { "Enabled", "" }),

        ENTER_PASSWORD(new String[]
        { "Enter Password", "" }),

        ERR_MISSING_PASSWORD_NAME(new String[]
        { "Error! Password name must be entered!", "" }),

        ERR_NAME_ALREADY_TAKEN(new String[]
        { "Error! Name already taken!", "" }),

        EXPORT(new String[]
        { "Export", "��������������" }),

        GREETING(new String[]
        { "Welcome to pasSHA!", "����� ���������� � pasSHA!" }),

        LENGTH(new String[]
        { "Length", "" }),

        MANAGE(new String[]
        { "Manage", "����������" }),

        MUST_CONTAIN_UPPER_CASE_CHAR(new String[]
        { "Must have UPPER case character", "" }),

        MUST_CONTAINT_SPECIAL_CHARS(new String[]
        { "Must contain special characters", "" }),

        NAME(new String[]
        { "Name", "" }),

        NEW(new String[]
        { "New", "��������" }),

        OK(new String[]
        { "OK", "�����������" }),

        PWD(new String[]
        { "Password", "" }),

        PWD_NAME(new String[]
        { "Password name", "" }),

        SHORTCUT(new String[]
        { "Shortcut", "" }),

        SPECIAL_CHARACTERS(new String[]
        { "Special characters", "" }),

        URL(new String[]
        { "URL", "" }),

        VERSION(new String[]
        { "v." + version, "�." + version}),

        LOGIN(new String[]
        { "Login", "" }),

        REGISTER(new String[]
        { "Register", "" }),

        PROGRAM_NAME(new String[]
        { "pasSHA", "" }),

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
            // mb add log to see language and received text??
            return text[language.ordinal()];
        }
    }

    private static Language language = Language.ENGLISH;
}
