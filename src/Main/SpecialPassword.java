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

    public String getHotKey()
    {
        return hotKey;
    }

    public void setHotKey(String hotKey)
    {
        this.hotKey = hotKey;
    }

    private String name;
    private String comment;
    private int shaCount;
    private String hotKey;


}
