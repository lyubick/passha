package ui.elements;

public class Tab extends javafx.scene.control.Tab
{
    public void setContent_(javafx.scene.Node value)
    {
        super.setContent(value);
        ((TabContent) value).activateTab();
    }
}
