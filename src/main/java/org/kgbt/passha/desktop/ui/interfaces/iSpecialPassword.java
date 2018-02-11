package org.kgbt.passha.desktop.ui.interfaces;

import org.kgbt.passha.core.db.SpecialPassword;

public class iSpecialPassword
{
    private final SpecialPassword origin;

    private boolean               passowordVisible = false;

    public iSpecialPassword(SpecialPassword example)
    {
        this.origin = example;
    }

    public SpecialPassword getOrigin()
    {
        return origin;
    }

    public String getName()
    {
        return origin.getName();
    }

    public String getShortcut()
    {
        return origin.getShortcut();
    }

    public String getComment()
    {
        return origin.getComment();
    }

    public String getUrl()
    {
        return origin.getUrl();
    }

    public String getPassword()
    {
        if (passowordVisible)
            return origin.getPassword();
        else
            return "*****";
    }

    public void setPasswordVisible(boolean value)
    {
        passowordVisible = value;
    }

}
