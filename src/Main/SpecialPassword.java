/**
 *
 */
package Main;

import java.io.Serializable;

/**
 * @author curious-odd-man
 *
 */
public class SpecialPassword implements Serializable
{

    private String            name             = "";
    private String            comment          = "";
    private String            url              = "";
    private int               shaCount         = 0;

    private static final long serialVersionUID = 1L;

    public SpecialPassword()
    {
        this.name = "TEST";
        this.comment = "COMMNET";
        this.url = "URL";
        this.shaCount = 1;
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

    public int getShaCount()
    {
        return shaCount;
    }

    public void setShaCount(int shaCount)
    {
        this.shaCount = shaCount;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    /* curious`s Mother Fucker Useless Function Set */
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
        if (otherCasted.shaCount != this.shaCount)
            return false;

        return true;
    }
}
