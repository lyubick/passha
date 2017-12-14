package org.kgbt.passha.ui.elements;

public interface TabContent
{
    public void activateTab();

    public void closeTab();

    public void reload();

    public void setName(String name);

    public String getName();
}
