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

    // TODO: Use String.split to parse CSV file with texts
    public enum TextID
    {
        ADD_SPECIAL_PASSWORD(new String[]
        { "Add Special Password", "" }),

        CANCEL(new String[]
        { "Cancel", "Отменить" }),

        CHANGE_PWD_HEADER(new String[]
        { "Password reset", "" }),

        CHANGE_PWD_WARNING(
                new String[]
                {
                        "By pressing OK you will substitute old password with new one. Thus old password will be permanently lost",
                        "" }),

        COMMENT(new String[]
        { "Comment", "" }),

        COPY_CLIPBOARD(new String[]
        { "Copy To Clipboard", "" }),

        CREATE(new String[]
        { "Create", "" }),

        DELETE(new String[]
        { "Delete", "Удалить" }),

        DISCARD(new String[]
        { "Discard", "" }),

        ENABLED(new String[]
        { "Enabled", "" }),

        ENTER_PASSWORD(new String[]
        { "Enter Password", "" }),

        ERR_MISSING_PASSWORD_NAME(new String[]
        { "Error! Password name must be entered!", "" }),

        ERR_NAME_ALREADY_TAKEN(new String[]
        { "Error! Name already taken!", "" }),

        EXPORT(new String[]
        { "Export", "Экспортировать" }),

        GREETING(new String[]
        { "Welcome to pasSHA!", "Добро пожаловать в pasSHA!" }),

        LENGTH(new String[]
        { "Length", "" }),

        LOGIN(new String[]
        { "Login", "" }),

        MANAGE(new String[]
        { "Manage", "Управление" }),

        MUST_CONTAIN_UPPER_CASE_CHAR(new String[]
        { "Must have UPPER case character", "" }),

        MUST_CONTAINT_SPECIAL_CHARS(new String[]
        { "Must contain special characters", "" }),

        NAME(new String[]
        { "Name", "" }),

        NEW(new String[]
        { "New", "Добавить" }),

        NEW_PWD(new String[]
        { "New", "" }),

        OK(new String[]
        { "OK", "Подтвердить" }),

        OLD_PWD(new String[]
        { "Old", "" }),

        PASSWORD_INCORRECT(new String[]
        { "Password incorrect!", "" }),

        PASSWORDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "" }),

        PROGRAM_NAME(new String[]
        { "pasSHA", "" }),

        PWD(new String[]
        { "Password", "" }),

        PWD_NAME(new String[]
        { "Password name", "" }),

        REGISTER(new String[]
        { "Register", "" }),

        RESET_PASSWORD(new String[]
        { "Reset password", "" }),

        SAVE(new String[]
        { "Save", "" }),

        SHORTCUT(new String[]
        { "Shortcut", "" }),

        SPECIAL_CHARACTERS(new String[]
        { "Special characters", "" }),

        URL(new String[]
        { "URL", "" }),

        VERSION(new String[]
        { "v." + version, "в." + version }),

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

    static String           version  = "dev";
}
