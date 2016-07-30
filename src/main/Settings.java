package main;

import java.util.HashMap;

import main.Exceptions.XC;
import utilities.Utilities;

public class Settings
{
    private static Settings self            = null;

    private boolean         restartRequired = false;

    public static class ENV_VARS
    {
        public static int SINGLE_INSTANCE_CHECK_TIMEOUT = 2000;
        public static int SINGLE_INSTANCE_CHECK_WAIT    = 3000;
    }

    private final String SETTINGS_FILE_NAME = "settings.passha";

    private enum PREFIX
    {
        LANGUAGE(new String("language=")),
        CLIPBOARD_TIME(new String("clipboardLiveTime=")),

        ;

        private final String text;

        private PREFIX(final String text)
        {
            this.text = text;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    public enum LANGUAGE
    {
        ENGLISH,
        RUSSIAN,
    }

    private HashMap<String, Object> settings = new HashMap<String, Object>();

    private Settings()
    {
        settings.put(PREFIX.LANGUAGE.toString(), LANGUAGE.ENGLISH.name());
        settings.put(PREFIX.CLIPBOARD_TIME.toString(), 5000);
    }

    public static Settings init() throws Exceptions
    {
        if (self != null) throw new Exceptions(XC.INSTANCE_ALREADY_EXISTS);

        return self = new Settings();
    }

    public static Settings getInstance() throws Exceptions
    {
        if (self == null) throw new Exceptions(XC.INSTANCE_DOES_NOT_EXISTS);

        return self;
    }

    // LANGUAGE
    public int getLanguage()
    {
        return LANGUAGE.valueOf((String) settings.get(PREFIX.LANGUAGE.toString())).ordinal();
    }

    public void setLanguage(String newLang)
    {
        if (!settings.get(PREFIX.LANGUAGE.toString()).equals(newLang)) setRestartRequired(true);

        settings.put(PREFIX.LANGUAGE.toString(), newLang);

    }

    public boolean isRestartRequired()
    {
        return restartRequired;
    }

    public void setRestartRequired(boolean required)
    {
        restartRequired = required;
    }

    // CLIPBOARD
    public int getClipboardLiveTime()
    {
        return (Integer) settings.get(PREFIX.CLIPBOARD_TIME.toString());
    }

    public void setClipboardLiveTime(String newTime)
    {
        // We keeping time in milliseconds
        settings.put(PREFIX.CLIPBOARD_TIME.toString(), Integer.parseInt(newTime + "000"));
    }

    public void saveSettings() throws Exceptions
    {
        Utilities.writeToFile(SETTINGS_FILE_NAME, Utilities.objectToBytes(settings));
    }

    @SuppressWarnings("unchecked")
    public void loadSettings() throws Exceptions
    {
        try
        {
            settings =
                (HashMap<String, Object>) Utilities.bytesToObject(Utilities.readBytesFromFile(SETTINGS_FILE_NAME));
        }
        catch (Exceptions e)
        {
            throw new Exceptions(XC.DEFAULT_SETTINGS_USED);
        }

    }
}
