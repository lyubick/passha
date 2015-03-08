/**
 *
 */
package Main;

import Logger.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author lyubick
 *
 */
public class iSpecialPassword
{
    private SimpleStringProperty name;
    private SimpleStringProperty comment;
    private SimpleStringProperty url;

    private SpecialPassword      base;

    public iSpecialPassword(SpecialPassword example)
    {
        Logger.printDebug("iSpecialPassword constructor... START");

        this.name    = new SimpleStringProperty(example.getName());
        this.comment = new SimpleStringProperty(example.getComment());
        this.url     = new SimpleStringProperty(example.getUrl());

        this.base = example;

        Logger.printDebug("iSpecialPassword constructor... DONE!");
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name.get();
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(SimpleStringProperty name)
    {
        this.name = name;
        this.base.setName(name.get());
    }

    /**
     * @return the comment
     */
    public String getComment()
    {
        return comment.get();
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(SimpleStringProperty comment)
    {
        this.comment = comment;
        this.base.setComment(comment.get());
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url.get();
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(SimpleStringProperty url)
    {
        this.url = url;
        this.base.setUrl(url.get());
    }

}
