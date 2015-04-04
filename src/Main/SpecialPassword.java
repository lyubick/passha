/**
 *
 */
package Main;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.BitSet;

import javafx.concurrent.Task;
import Common.Exceptions;
import Common.Exceptions.XC;
import CryptoSystem.CryptoSystem;
import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public class SpecialPassword implements Serializable
{
    public enum ParamsMaskBits
    {
        HAS_SPECIAL_CHARACTERS,
        HAS_CAPITALS,

        HAS_SMALL_LETTERS,
        HAS_NUMBERS,

        TOTAL_COUNT,
    }

    String                    asyncronouslyGeneratedPassword = "";
    private String            name;
    private String            comment;
    private String            url;
    private int               length;
    private String            specialChars;
    private BitSet            paramsMask                     = null;
    private long              shaCycles;                            // generated
                                                                     // in
                                                                     // PasswordCollection;

    private static final long serialVersionUID               = 1L;

    public SpecialPassword()
    {
        Logger.printDebug("SpecialPassword DEFAULT constructor... START");

        this.name = "EMPTY";
        this.comment = "COMMENT";
        this.url = "URL";
        this.length = 16;
        this.paramsMask = new BitSet(ParamsMaskBits.TOTAL_COUNT.ordinal());
        this.paramsMask.set(0, ParamsMaskBits.TOTAL_COUNT.ordinal());
        this.specialChars = "!@#$%^&*";

        this.shaCycles = 1;

        Logger.printDebug("SpecialPassword DEFAULT constructor... DONE!");
    }

    public SpecialPassword(String name, String comment, String url, int length, BitSet paramsMask,
            String specialChars) throws Exceptions
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

        do
        {
            shaCycles = CryptoSystem.getInstance().randSHACycles();
        } while (!isPasswordValid());

        Logger.printDebug("SpecialPassword copy-constructor... DONE!");
    }

    public void dump()
    {
        Field[] properties = this.getClass().getDeclaredFields();
        StringBuilder template = new StringBuilder();

        template.append("SpecialPassword: ");

        for (Field property : properties)
        {
            template.append(property.getName());
            template.append(": ");

            try
            {
                template.append(property.get(this));
            }
            catch (IllegalAccessException e)
            {
                Logger.printError(e.toString());
            }

            template.append("; ");
        }

        Logger.printDebug(template.toString());
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the comment
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * @return the shaCycles
     */
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
        Logger.printError("Illegal call of hashCode.");
        assert false : "Illegal call of hashCode.";
        Terminator.terminate(new Exceptions(XC.INIT_FAILURE));
        return 0;
    }

    // TODO: speparate method for password generation
    // TODO: check that password meets rules and do re-hash if not;

    public String getPassword(Task<Void> passwordCalculation)
    {
        try
        {
            String hash =
                    CryptoSystem.getInstance().getPassword(this.shaCycles, this.getName(),
                            passwordCalculation);

            // take first @a this.length chars from hash
            StringBuilder clearPass = new StringBuilder(hash.substring(0, this.length));

            // set special characters
            // todo more special characters
            if (paramsMask.get(ParamsMaskBits.HAS_SPECIAL_CHARACTERS.ordinal())
                    && specialChars.length() != 0)
            {
                Logger.printDebug("use special characters");
                byte specialCharacterPosition = (byte) hash.charAt(hash.length() - 2);
                byte insertPosition = (byte) hash.charAt(hash.length() - 3);

                insertPosition = (byte) (insertPosition % clearPass.length());
                specialCharacterPosition =
                        (byte) (specialCharacterPosition % specialChars.length());

                clearPass.setCharAt(insertPosition, specialChars.charAt(specialCharacterPosition));
            }

            // set CAPITALS.
            if (paramsMask.get(ParamsMaskBits.HAS_CAPITALS.ordinal()))
            {
                Logger.printDebug("use capitals");
                // determine what chars changes case
                boolean changeCase = (hash.charAt(hash.length() - 1) & 0x01) != 0;
                // get string with capitals for easier processing
                String capsString = (new String(clearPass)).toUpperCase();

                for (int i = 0; i < clearPass.length(); i++)
                {
                    // different chars means different case
                    if (clearPass.charAt(i) != capsString.charAt(i))
                    {
                        if (changeCase)
                        {
                            clearPass.setCharAt(i, capsString.charAt(i));
                        }

                        changeCase = !changeCase;
                    }
                }
            }

            return clearPass.toString();
        }
        catch (Exceptions e)
        {
            Terminator.terminate(e);
        }
        return "";
    }

    public String getPassword()
    {
        return getPassword(null);
    }

    public boolean isPasswordValid()
    {
        BitSet currentMaskBitSet = new BitSet(ParamsMaskBits.TOTAL_COUNT.ordinal());
        String pwd = getPassword();

        for (int i = 0; i < pwd.length(); i++)
        {
            if (Character.isDigit(pwd.charAt(i)))
                currentMaskBitSet.set(ParamsMaskBits.HAS_NUMBERS.ordinal());
            else if (Character.isLowerCase(pwd.charAt(i)))
                currentMaskBitSet.set(ParamsMaskBits.HAS_SMALL_LETTERS.ordinal());
            else if (Character.isUpperCase(pwd.charAt(i)))
                currentMaskBitSet.set(ParamsMaskBits.HAS_CAPITALS.ordinal());
            else if (specialChars.indexOf(pwd.charAt(i)) != -1)
                currentMaskBitSet.set(ParamsMaskBits.HAS_SPECIAL_CHARACTERS.ordinal());
            else
                Logger.printError("How can it really happen???");

            if (currentMaskBitSet.equals(paramsMask)) return true;
            Logger.printDebug("currentMaskBitSet " + currentMaskBitSet.toString() + "; paramsMask"
                    + paramsMask.toString());
        }

        return false;
    }
}
