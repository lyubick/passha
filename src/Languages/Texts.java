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

    private static Language language = Language.ENGLISH;

    public enum TextID
    {
        VERSION(new String[]
        { "Version", "Версия" }),

        GREETING(new String[]
        { "Welcome to pasSHA!", "Добро пожаловать в pasSHA!" }),

        OK(new String[]
        { "OK", "Подтвердить" }),

        CANCEL(new String[]
        { "CANCEL", "Отменить" }),

        MANAGE(new String[]
        { "Manage", "Управление" }),

        NEW(new String[]
        { "NEW", "Добавить" }),

        DELETE(new String[]
        { "DELETE", "Удалить" }),

        EXPORT(new String[]
        { "Export", "Экспортировать" }),

        PWD_NAME(new String[]
        { "Password name", "" }),

        PWD(new String[]
        { "Password", "" }),

        COMMENT(new String[]
        { "Comment", "" }),

        SHORTCUT(new String[]
        { "Shortcut", "" }),

        ENABLED(new String[]
        { "Enabled", "" }),

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
}
