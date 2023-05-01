package org.kgbt.passha.core.common.cfg;

public final class Properties
{
    public static final class SOFTWARE
    {
        public static final String NAME    = "pasSHA";
        public static final String VERSION = "3.1";
    }

    public static final class PATHS
    {
        public static final String VAULT     = "vaults/";
        public static final String TRAY_ICON = "tray_icon.png";
    }

    public static final class EXTENSIONS
    {
        // PVP = pasSHA Vault Passwords
        public static final String VAULT  = ".pvp";
        public static final String EXPORT = ".txt";
    }

    public static final class CORE
    {
        public static final class VAULT
        {
            public static final int MAX_COUNT = 10;
        }

        public static final class SHA
        {
            public static final int ITERATION_MIN_COUNT = 10;
            public static final int ITERATION_MAX_COUNT = 255 - ITERATION_MIN_COUNT;
        }

    }

    public static final class DATABASE
    {
        public static final int SYNC_RETRY_DELAY_MS = 60 * 1000;
        public static final int MAX_RETRIES         = 5;
    }

    public static final class GUI
    {
        public static final class GAP
        {
            public static final int H = 10;
            public static final int V = 10;
        }

        public static final class PADDING
        {
            public static final int bottom = 10;
            public static final int top    = 10;
            public static final int right  = 10;
            public static final int left   = 10;
        }

        public static final class STANDARD
        {
            public static final class SIZE
            {
                public static final double WIDTH  = 300.0;
                public static final double HEIGHT = 30.0;
            }
        }
    }
}
