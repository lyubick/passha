/**
 *
 */
package Main;

import Common.Exceptions;
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
    private final SimpleStringProperty password;

    private final SpecialPassword      origin;

    public iSpecialPassword(SpecialPassword example)
    {
        Logger.printDebug("iSpecialPassword constructor... START");

        this.name = new SimpleStringProperty(example.getName());
        this.comment = new SimpleStringProperty(example.getComment());
        this.url = new SimpleStringProperty(example.getUrl());
        this.password = new SimpleStringProperty("SHOW");

        this.origin = example;

        Logger.printDebug("iSpecialPassword constructor... DONE!");
    }

    public SpecialPassword getOrigin()
    {
        return origin;
    }

    public String getPassword()
    {
        return password.get();
    }

    public void showPassword(boolean show)
    {
        Logger.printDebug("" + show);
        if (show)
        {
            password.set(origin.getPassword());
        }
        else
        {
            password.set("SHOW");
        }
        Logger.printDebug("" + password.get());
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
