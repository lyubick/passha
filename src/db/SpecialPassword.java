package db;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashMap;

import sha.SHA;
import utilities.Utilities;
import cryptosystem.CryptoSystem;
import logger.Logger;
import main.Exceptions;
import main.Terminator;
import main.Exceptions.XC;
import javafx.concurrent.Task;

public class SpecialPassword
{
    public enum ParamsMaskBits
    {
        HAS_SPECIAL_CHARACTERS,
        HAS_CAPITALS,

        HAS_SMALL_LETTERS,
        HAS_NUMBERS,

        TOTAL_COUNT,
    }

    private long   shaCycles             = 0;
    private String name                  = null;
    private String comment               = null;
    private String url                   = null;
    private int    length                = 0;
    private String specialChars          = null;
    private BitSet paramsMask            = null;

    private String SALT_SPECIAL_PASSWORD = "SPECIAL";

    public SpecialPassword(HashMap<String, String> m)
    {
        Logger.printDebug("SpecialPassword constructor from Map... START");
        Logger.printDebug("Received map: " + m.toString());

        shaCycles = Long.parseLong(m.getOrDefault("shaCycles", "0"));
        name = m.getOrDefault("name", "");
        comment = m.getOrDefault("comment", "");
        url = m.getOrDefault("url", "");
        length = Integer.parseInt(m.getOrDefault("length", "0"));
        specialChars = m.getOrDefault("specialChars", "");
        paramsMask = Utilities.getBitSet(m.getOrDefault("paramsMask", ""));

        Logger.printDebug("SpecialPassword constructor from Map... END");
    }

    public HashMap<String, String> getMap()
    {

        HashMap<String, String> m = new HashMap<String, String>();
        m.put("shaCycles", "" + shaCycles);
        m.put("name", name);
        m.put("comment", comment);
        m.put("url", url);
        m.put("length", "" + length);
        m.put("specialChars", specialChars);
        m.put("paramsMask", paramsMask.toString());

        Logger.printDebug("Created map: " + m.toString());

        return m;
    }

    public SpecialPassword(String name, String comment, String url, int length, BitSet paramsMask, String specialChars)
            throws Exceptions
    {
        Logger.printDebug("SpecialPassword constructor... START");

        if (name.length() == 0) throw new Exceptions(XC.MANDATORY_DATA_MISSING);

        this.name = name;
        this.comment = comment;
        this.url = url;
        this.length = length;
        this.paramsMask = paramsMask;
        this.specialChars = specialChars;

        do
        {
            shaCycles = CryptoSystem.getInstance().randSHACycles();
        }
        while (!isPasswordValid());

        Logger.printDebug("SpecialPassword constructor... DONE!");
    }

    public SpecialPassword(SpecialPassword other) throws Exceptions
    {
        Logger.printDebug("SpecialPassword copy-constructor... START");

        if (other == null) throw new Exceptions(XC.MANDATORY_DATA_MISSING);

        this.name = other.name.toString();
        this.comment = other.comment.toString();
        this.url = other.url.toString();
        this.length = other.length;
        this.paramsMask = (BitSet) other.paramsMask.clone();
        this.specialChars = other.specialChars.toString();

        do
        {
            shaCycles = CryptoSystem.getInstance().randSHACycles();
        }
        while (!isPasswordValid());

        Logger.printDebug("SpecialPassword copy-constructor... DONE!");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public long getShaCycles()
    {
        return shaCycles;
    }

    public void setShaCycles(long sc)
    {
        shaCycles = sc;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof SpecialPassword)) return false;

        SpecialPassword otherCasted = (SpecialPassword) other;

        if (otherCasted.name.equals(this.name) == false) return false;
        if (otherCasted.comment.equals(this.comment) == false) return false;
        if (otherCasted.url.equals(this.url) == false) return false;
        if (otherCasted.shaCycles != this.shaCycles) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    private byte getSpecialCharactersCount()
    {
        return (byte) ((this.length) >>> 2);
    }

    private int getRandomNumberFromHex(String hash, int idx)
    {
        final String hexArray = "0123456789abcdef";
        if (idx >= hash.length() - 1)
        {
            idx %= hash.length() - 1;
        }

        return Math.abs(hexArray.indexOf(hash.charAt(idx)) * hexArray.indexOf(hash.charAt(idx + 1)));
    }

    public String getPassword(Task<Void> passwordCalculation)
    {
        int idx = 0;
        String alphabeta = "0123456789abcdefghijklmnopqrstuvwxyz";

        String passwordHash = null;
        String specialHash = null;

        StringBuilder password = new StringBuilder("");

        /*
         * 1. Stage - 64-byte long hash will be converted to character/number
         * set of appropriate length
         */
        Logger.printDebug("Password generation. STAGE 1. START");

        try
        {
            passwordHash = CryptoSystem.getInstance().getPassword(shaCycles, name, passwordCalculation);
            specialHash = CryptoSystem.getInstance().getHash(passwordHash, SALT_SPECIAL_PASSWORD);
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }

        int mapChunkLength = SHA.SHA_BYTES_RESULT / length;

        for (int i = 0; i < length; ++i)
        {
            int currIdx = i * mapChunkLength;
            password.append(alphabeta.charAt(Utilities
                    .hexToInt(passwordHash.substring(currIdx, currIdx + mapChunkLength))
                    .mod(new BigInteger(Integer.toString(alphabeta.length()))).intValue()));
        }

        Logger.printDebug("Password generation. STAGE 1. DONE");

        /*
         * 2. Stage - Result of Stage 1 (appropriate length password) will be
         * modified with special characters.
         */
        if (paramsMask.get(ParamsMaskBits.HAS_SPECIAL_CHARACTERS.ordinal()) && specialChars.length() != 0)
        {

            Logger.printDebug("Password generation. STAGE 2. START");

            int count = getSpecialCharactersCount();
            int specialCharacterPosition = 0;
            int insertPosition = 0;

            while (count > 0)
            {
                do
                {
                    insertPosition = (getRandomNumberFromHex(specialHash, idx++) % password.length());
                }
                while (specialChars.indexOf(password.charAt(insertPosition)) != -1);

                specialCharacterPosition = (getRandomNumberFromHex(specialHash, idx++) % specialChars.length());

                if (specialChars.length() >= getSpecialCharactersCount())
                {
                    while (password.toString().indexOf(specialChars.charAt(specialCharacterPosition)) != -1)
                    {
                        specialCharacterPosition = (++specialCharacterPosition % specialChars.length());
                    }
                }

                Logger.printDebug("Special characters count left = " + count + "; insertPosition = " + insertPosition);

                password.setCharAt(insertPosition, specialChars.charAt(specialCharacterPosition));
                count--;
            }

            Logger.printDebug("Password generation. STAGE 2. DONE");
        }

        /*
         * 3. Stage - Final stage where random characters will be capitalized.
         */
        if (paramsMask.get(ParamsMaskBits.HAS_CAPITALS.ordinal()))
        {
            Logger.printDebug("Password generation. STAGE 3. START");

            Logger.printDebug("use capitals");
            boolean changeCase = (specialHash.charAt(idx++) & 0x01) != 0;
            String capsString = (new String(password)).toUpperCase();

            for (int i = 0; i < password.length(); i++)
            {
                if (password.charAt(i) != capsString.charAt(i))
                {
                    if (changeCase)
                    {
                        password.setCharAt(i, capsString.charAt(i));
                    }
                    changeCase = !changeCase;
                }
            }
        }
        Logger.printDebug("Password generation. STAGE 3. DONE");

        return password.toString();
    }

    public String getPassword()
    {
        return getPassword(null);
    }

    public boolean isPasswordValid()
    {
        BitSet currentMaskBitSet = new BitSet(ParamsMaskBits.TOTAL_COUNT.ordinal());
        String pwd = getPassword();
        byte count = getSpecialCharactersCount();

        for (int i = 0; i < pwd.length(); i++)
        {
            if (Character.isDigit(pwd.charAt(i)))
                currentMaskBitSet.set(ParamsMaskBits.HAS_NUMBERS.ordinal());
            else if (Character.isLowerCase(pwd.charAt(i)))
                currentMaskBitSet.set(ParamsMaskBits.HAS_SMALL_LETTERS.ordinal());
            else if (Character.isUpperCase(pwd.charAt(i)))
                currentMaskBitSet.set(ParamsMaskBits.HAS_CAPITALS.ordinal());
            else if (specialChars.indexOf(pwd.charAt(i)) != -1)
            {
                count--;
                if (count == 0) currentMaskBitSet.set(ParamsMaskBits.HAS_SPECIAL_CHARACTERS.ordinal());
            }
            else
                Logger.printError("How can it really happen???");

            if (currentMaskBitSet.equals(paramsMask)) return true;
            Logger.printDebug("currentMaskBitSet " + currentMaskBitSet.toString() + "; paramsMask"
                    + paramsMask.toString());
        }

        return false;
    }
}
