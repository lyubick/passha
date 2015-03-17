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

        COMMENT(new String[]
        { "Comment", "" }),

        CREATE(new String[]
        { "Create", "" }),

        DELETE(new String[]
        { "Delete", "Удалить" }),

        ENABLED(new String[]
        { "Enabled", "" }),

        EXPORT(new String[]
        { "Export", "Экспортировать" }),

        ERR_NAME_ALREADY_TAKEN(new String[]
        { "Error! Name already taken!", "" }),

        ERR_MISSING_PASSWORD_NAME(new String[]
        { "Error! Password name must be entered!", "" }),

        GREETING(new String[]
        { "Welcome to pasSHA!", "Добро пожаловать в pasSHA!" }),

        LENGTH(new String[]
        { "Length", "" }),

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

        OK(new String[]
        { "OK", "Подтвердить" }),

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
        { "Version", "Версия" }),

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
