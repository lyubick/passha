/**
 *
 */
package db;

import javafx.beans.property.SimpleStringProperty;
import logger.Logger;

/**
 * @author lyubick
 *
 */
public class iSpecialPassword
{
    private SimpleStringProperty  name;
    private SimpleStringProperty  comment;
    private SimpleStringProperty  url;

    private final SpecialPassword origin;

    public iSpecialPassword(SpecialPassword example)
    {
        Logger.printDebug("iSpecialPassword constructor STARTS...");

        this.name = new SimpleStringProperty(example.getName());
        this.comment = new SimpleStringProperty(example.getComment());
        this.url = new SimpleStringProperty(example.getUrl());

        this.origin = example;

        Logger.printDebug("iSpecialPassword constructor END");
    }

    public SpecialPassword getOrigin()
    {
        return origin;
    }

    public String getPassword()
    {
        return origin.getPassword();
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
        this.origin.setName(name.get());
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
        this.origin.setComment(comment.get());
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
        this.origin.setUrl(url.get());
    }

}
