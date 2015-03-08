/**
 *
 */
package Main;

import java.io.Serializable;
import java.lang.reflect.Field;

import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public class SpecialPassword implements Serializable
{

    private String            name;
    private String            comment;
    private String            url;
    private long              shaCycles;

    private static final long serialVersionUID = 1L;

    public SpecialPassword()
    {
        Logger.printDebug("SpecialPassword constructor... START");

        this.name = "EMPTY";
        this.comment = "COMMENT";
        this.url = "URL";

        this.shaCycles = 1;

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
            } catch (IllegalAccessException e)
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
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof SpecialPassword))
            return false;

        SpecialPassword otherCasted = (SpecialPassword) other;

        if (otherCasted.name.equals(this.name) == false)
            return false;
        if (otherCasted.comment.equals(this.comment) == false)
            return false;
        if (otherCasted.url.equals(this.url) == false)
            return false;
        if (otherCasted.shaCycles != this.shaCycles)
            return false;

        return true;
    }
}
