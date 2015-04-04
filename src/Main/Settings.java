/**
 *
 */
package Main;

/**
 * @author lyubick
 *
 */
public class Settings
{

    public enum LANG
    {
        ENGLISH,
        RUSSIAN,
    }

    private static LANG language          = LANG.ENGLISH;
    private static int  clipboardLiveTime = 5000;

    static public int getclipboardLiveTime()
    {
        return clipboardLiveTime;
    }

    static public int getLanguage()
    {
        return language.ordinal();
    }

    static public void setLanguage(LANG newLang)
    {
        language = newLang;
    }

    static public void setclipboardLiveTime(int newTime)
    {
        clipboardLiveTime = newTime;
    }
}
