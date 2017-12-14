package org.kgbt.passha.ui.elements;

public interface TabContent
{
    void activateTab();

    void closeTab();

    void reload();

    void setName(String name);

    String getName();
}
