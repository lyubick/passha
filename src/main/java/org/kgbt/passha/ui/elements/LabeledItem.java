package org.kgbt.passha.ui.elements;

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
