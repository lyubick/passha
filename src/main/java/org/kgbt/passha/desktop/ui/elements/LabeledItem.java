package org.kgbt.passha.desktop.ui.elements;

import javafx.scene.Node;

public interface LabeledItem
{
    default javafx.scene.Node getSelf()
    {
        return (Node) this;
    }

    Label getLabel();

    void beError();

    void beNormal();
}
