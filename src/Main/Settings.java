/**
 *
 */
package Main;

import java.io.FileNotFoundException;
import java.util.Vector;

import Common.Exceptions;
import Common.FileIO;

/**
 * @author lyubick
 *
 */
public class Settings
{

    private final static String SETTINGS_FILE_NAME        = "settings.passha";

    private final static String OUT_LANG_PREFIX           = "language=";
    private final static String OUT_CLIPBOARD_TIME_PREFIX = "clipboardLiveTime=";

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

    static public void saveSettings()
    {
        Vector<String> outStrings = new Vector<String>();

        try
        {
            outStrings.add(OUT_LANG_PREFIX + language.name());
            outStrings.add(OUT_CLIPBOARD_TIME_PREFIX + clipboardLiveTime);

            FileIO.writeToFile(SETTINGS_FILE_NAME, outStrings);
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static public void loadSettings()
    {
        try
        {
            Vector<String> inSettings = FileIO.readFile(SETTINGS_FILE_NAME);

            for (String param : inSettings)
            {
                if (param.startsWith(OUT_LANG_PREFIX))
                {
                    language = LANG.valueOf(param.substring(param.indexOf("=")));
                }
                else if (param.startsWith(OUT_CLIPBOARD_TIME_PREFIX))
                {
                    clipboardLiveTime = Integer.parseInt(param.substring(param.indexOf("=")));
                }
            }
        }
        catch (Exceptions e)
        {
            // ignore - use default settings
        }
    }
}
