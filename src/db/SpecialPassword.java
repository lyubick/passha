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

public class SpecialPassword
{
    public enum PARAMS_MASK_BITS
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
    private String shortcut              = null;

    private String SALT_SPECIAL_PASSWORD = "SPECIAL";

    public SpecialPassword(HashMap<String, String> m)
    {
        Logger.printDebug("SpecialPassword constructor from Map STARTS...");

        shaCycles = Long.parseLong(m.getOrDefault("shaCycles", "0"));
        name = m.getOrDefault("name", "");
        comment = m.getOrDefault("comment", "");
        url = m.getOrDefault("url", "");
        length = Integer.parseInt(m.getOrDefault("length", "0"));
        specialChars = m.getOrDefault("specialChars", "");
        paramsMask = Utilities.getBitSet(m.getOrDefault("paramsMask", ""));
        shortcut = m.getOrDefault("shortcut", "");

        Logger.printDebug("SpecialPassword constructor from Map END");
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
        m.put("shortcut", shortcut);

        Logger.printDebug("Created map: " + m.toString());

        return m;
    }

    public SpecialPassword(String name, String comment, String url, String length,
            boolean needSpecialChars, boolean needUpperCaseChar, String specialChars,
            String shortcut) throws Exceptions
    {
        Logger.printDebug("SpecialPassword constructor... START");

        if (name.length() == 0 || length.length() == 0)
            throw new Exceptions(XC.MANDATORY_DATA_MISSING);

        BitSet paramsMask = new BitSet(PARAMS_MASK_BITS.TOTAL_COUNT.ordinal());
        paramsMask.set(0, PARAMS_MASK_BITS.TOTAL_COUNT.ordinal());

        if (!needSpecialChars)
        {
            paramsMask.clear(PARAMS_MASK_BITS.HAS_SPECIAL_CHARACTERS.ordinal());
        }
        else
        {
            if (specialChars.length() == 0) throw new Exceptions(XC.MANDATORY_DATA_MISSING);
        }

        if (!needUpperCaseChar)
        {
            paramsMask.clear(PARAMS_MASK_BITS.HAS_CAPITALS.ordinal());
        }

        this.name = name;
        this.comment = comment;
        this.url = url;
        this.length = Integer.parseInt(length);
        this.paramsMask = paramsMask;
        this.specialChars = specialChars;
        this.shortcut = shortcut;

        do
        {
            shaCycles = CryptoSystem.getInstance().randSHACycles();
        } while (!isPasswordValid());

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
        this.shortcut = other.shortcut.toString();

        do
        {
            shaCycles = CryptoSystem.getInstance().randSHACycles();
        } while (!isPasswordValid());

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

    public void setShortcut(String shortcut)
    {
        this.shortcut = shortcut;
    }

    public String getShortcut()
    {
        return shortcut;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof SpecialPassword)) return false;

        SpecialPassword otherCasted = (SpecialPassword) other;

        if (otherCasted.name.equals(this.name) == false) return false;
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

    private int getNumberFromHashAt(String hash, int idx)
    {
        final String hexArray = "0123456789abcdef";

        idx = (idx >= hash.length() - 1) ? idx % hash.length() - 1 : idx;

        return Math
                .abs(hexArray.indexOf(hash.charAt(idx)) * hexArray.indexOf(hash.charAt(idx + 1)));
    }

    /**
     * 1. Stage
     *
     * 64-byte long hash will be converted to character/number set of
     * appropriate length
     *
     * @param password
     * @param hash
     *
     * @return
     */
    private int getAlphaNumeric(StringBuilder password, String hash)
    {
        final String ALPHABETA = "0123456789abcdefghijklmnopqrstuvwxyz";

        int mapChunkLength = SHA.SHA_BYTES_RESULT / length;

        for (int i = 0; i < length; ++i)
        {
            int currIdx = i * mapChunkLength;

            password.append(ALPHABETA.charAt(Utilities
                    .hexToInt(hash.substring(currIdx, currIdx + mapChunkLength))
                    .mod(new BigInteger(Integer.toString(ALPHABETA.length()))).intValue()));
        }

        return 0;
    }

    /**
     * 2. Stage
     *
     * Incoming Password will be modified with special characters.
     *
     * @param password
     * @param hash
     * @param idx
     *
     * @return
     */
    private int addSpecialCharacters(StringBuilder password, String hash, int idx)
    {
        int count = getSpecialCharactersCount();

        int specialCharacterPosition = 0;
        int insertPosition = 0;

        while (count-- > 0)
        {
            do
            {
                insertPosition = (getNumberFromHashAt(hash, idx++) % password.length());
            } while (specialChars.indexOf(password.charAt(insertPosition)) != -1);

            specialCharacterPosition = (getNumberFromHashAt(hash, idx++) % specialChars.length());

            if (specialChars.length() >= getSpecialCharactersCount())
            {
                while (password.toString().indexOf(specialChars.charAt(specialCharacterPosition)) != -1)
                {
                    specialCharacterPosition = (++specialCharacterPosition % specialChars.length());
                }
            }

            password.setCharAt(insertPosition, specialChars.charAt(specialCharacterPosition));
        }

        return idx;
    }

    /**
     * 3. Stage
     *
     * @param password
     * @param specialHash
     * @param idx
     *
     * @return
     */
    private int setCapitalCharacters(StringBuilder password, String specialHash, int idx)
    {
        boolean changeCase = (specialHash.charAt(idx++) & 0x01) != 0;
        String capsString = (new String(password)).toUpperCase();

        for (int i = 0; i < password.length(); i++)
        {
            if (password.charAt(i) != capsString.charAt(i))
            {
                if (changeCase) password.setCharAt(i, capsString.charAt(i));
                changeCase = !changeCase;
            }
        }

        return idx;
    }

    public String getPassword()
    {
        String passwordHash = null;
        String specialHash = null;

        StringBuilder password = new StringBuilder("");

        int modificationIdx = 0;

        try
        {
            passwordHash = CryptoSystem.getInstance().getPassword(shaCycles, name);
            specialHash = CryptoSystem.getInstance().getHash(passwordHash, SALT_SPECIAL_PASSWORD);
        }
        catch (Exceptions e)
        {
            // Core element is down, no sense in running application
            Terminator.terminate(e);
        }

        /* STAGE 1 */
        Logger.printDebug("Password generation. STAGE 1. START");
        modificationIdx = getAlphaNumeric(password, passwordHash);
        Logger.printDebug("Password generation. STAGE 1. DONE");

        /* STAGE 2 */
        Logger.printDebug("Password generation. STAGE 2. START");
        if (paramsMask.get(PARAMS_MASK_BITS.HAS_SPECIAL_CHARACTERS.ordinal()))
        {
            modificationIdx = addSpecialCharacters(password, specialHash, modificationIdx);
        }
        Logger.printDebug("Password generation. STAGE 2. DONE");

        /* STAGE 3 */
        Logger.printDebug("Password generation. STAGE 3. START");
        if (paramsMask.get(PARAMS_MASK_BITS.HAS_CAPITALS.ordinal()))
        {
            modificationIdx = setCapitalCharacters(password, specialHash, modificationIdx);
        }
        Logger.printDebug("Password generation. STAGE 3. DONE");

        return password.toString();
    }

    private boolean isPasswordValid()
    {
        BitSet currentMaskBitSet = new BitSet(PARAMS_MASK_BITS.TOTAL_COUNT.ordinal());
        String pwd = getPassword();
        byte count = getSpecialCharactersCount();

        for (int i = 0; i < pwd.length(); i++)
        {
            if (Character.isDigit(pwd.charAt(i)))
                currentMaskBitSet.set(PARAMS_MASK_BITS.HAS_NUMBERS.ordinal());
            else if (Character.isLowerCase(pwd.charAt(i)))
                currentMaskBitSet.set(PARAMS_MASK_BITS.HAS_SMALL_LETTERS.ordinal());
            else if (Character.isUpperCase(pwd.charAt(i)))
                currentMaskBitSet.set(PARAMS_MASK_BITS.HAS_CAPITALS.ordinal());
            else if (specialChars.indexOf(pwd.charAt(i)) != -1)
            {
                count--;
                if (count == 0)
                    currentMaskBitSet.set(PARAMS_MASK_BITS.HAS_SPECIAL_CHARACTERS.ordinal());
            }
            else
                Logger.printError("Invalid character during validation.");

            if (currentMaskBitSet.equals(paramsMask)) return true;
        }

        Logger.printError("Validation Failed! Current Mask: " + currentMaskBitSet.toString()
                + " != Ethalon Mask" + paramsMask.toString());

        return false;
    }
}
