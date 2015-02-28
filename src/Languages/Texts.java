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
        { "Version", "������" }),

        GREETING(new String[]
        { "Welcome to pasSHA!", "����� ���������� � pasSHA!" }),

        OK(new String[]
        { "OK", "�����������" }),

        CANCEL(new String[]
        { "CANCEL", "��������" }),

        MANAGE(new String[]
        { "Manage", "����������" }),

        NEW(new String[]
        { "NEW", "��������" }),

        DELETE(new String[]
        { "DELETE", "�������" }),

        EXPORT(new String[]
        { "Export", "��������������" }),

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
