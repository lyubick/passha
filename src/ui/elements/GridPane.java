package ui.elements;

public class GridPane extends javafx.scene.layout.GridPane
{
    private int nextLine = 0;

    public void setNextLine(int nextLine)
    {
        this.nextLine = nextLine;
    }

    public void skipLines(int count)
    {
        this.nextLine += count;
    }

    public int getNextLine()
    {
        return nextLine;
    }

    public GridPane addNextEntryField(EntryField fld)
    {
        this.add(fld.getLabel(), 0, nextLine);
        this.add(fld, 1, nextLine);
        nextLine++;
        return this;
    }
}
