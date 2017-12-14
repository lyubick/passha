package org.kgbt.passha.ui.elements;

import javafx.scene.Node;

public interface LabeledItem
{
    public default javafx.scene.Node getSelf()
    {
        return (Node) this;
    }

    public Label getLabel();

    public void beError();

    public void beNormal();
}
