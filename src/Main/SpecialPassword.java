/**
 *
 */
package Main;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.BitSet;

import Common.Exceptions;
import Common.Exceptions.XC;
import Common.Return;
import Common.Return.RC;
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

        TOTAL_COUNT,
    }

    private String            name;
    private String            comment;
    private String            url;
    private final byte        length;
    private final String      specialChars;
    private BitSet            paramsMask       = null;
    private long              shaCycles;              // generated in
                                                       // PasswordCollection;

    private static final long serialVersionUID = 1L;

    public SpecialPassword()
    {
        Logger.printDebug("SpecialPassword DEFAULT constructor... START");

        this.name = "EMPTY";
        this.comment = "COMMENT";
        this.url = "URL";
        this.length = 16;
        this.paramsMask = new BitSet(ParamsMaskBits.TOTAL_COUNT.ordinal());
        this.paramsMask.xor(this.paramsMask); // set all bits to true
        this.specialChars = "!@#$%^&*";

        this.shaCycles = 1;

        Logger.printDebug("SpecialPassword DEFAULT constructor... DONE!");
    }

    public SpecialPassword(String name, String comment, String url, byte length, BitSet paramsMask,
            String specialChars) throws Exceptions
    {
        Logger.printDebug("SpecialPassword constructor... START");

        if (name.length() == 0) throw new Exceptions(XC.MISSING_MANDATORY_DATA);

        this.name = name;
        this.comment = comment;
        this.url = url;
        this.length = length;
        this.paramsMask = paramsMask;
        this.specialChars = specialChars;

        shaCycles = 0; // generated in PasswordCollection;

        Logger.printDebug("SpecialPassword constructor... DONE!");
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

    /**
     * @param shaCycles
     *            the shaCycles to set
     */
    public void setShaCycles(long shaCycles)
    {
        this.shaCycles = shaCycles;
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
        return RC.NONEXISTING_FUNCTION_CALL.ordinal();
    }

    // TODO: speparate method for password generation
    // TODO: check that password meets rules and do re-hash if not;

    public String getPassword()
    {
        try
        {
            String hash = CryptoSystem.getInstance().getPassword(this.shaCycles);

            // take first @a this.length chars from hash
            StringBuilder clearPass = new StringBuilder(hash.substring(0, this.length));

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

            if (paramsMask.get(ParamsMaskBits.HAS_CAPITALS.ordinal()) && specialChars.length() != 0)
            {
                byte specialCharacterPosition = (byte) hash.charAt(hash.length() - 2);
                byte insertPosition = (byte) hash.charAt(hash.length() - 3);

                insertPosition = (byte) (insertPosition % clearPass.length());
                specialCharacterPosition =
                        (byte) (specialCharacterPosition % specialChars.length());

                clearPass.setCharAt(insertPosition, specialChars.charAt(specialCharacterPosition));
            }

            return clearPass.toString();
        }
        catch (Exceptions e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

}
