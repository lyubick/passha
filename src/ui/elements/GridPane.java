package ui.elements;

import javafx.scene.Node;

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

    public void add(Node child, int columnIndex)
    {
        super.add(child, columnIndex, nextLine);
    }

    public GridPane addHElement(Node child, int columnIdx)
    {
        this.add(child, columnIdx, nextLine);
        nextLine++;
        return this;
    }

    public GridPane addHElement(Node child)
    {
        return addHElement(child, 0);
    }

    public GridPane addHElements(int columnIndex, Node... childs)
    {
        for (Node child : childs)
            add(child, columnIndex++);

        nextLine++;

        return this;
    }

    public GridPane addHElement(EntryField fld)
    {
        this.add(fld.getLabel(), 0, nextLine);
        this.add(fld, 1, nextLine);
        nextLine++;
        return this;
    }

    public GridPane addVElement(ProgressBar pb, Integer columnIndex)
    {
        this.add(pb.getLabel(), columnIndex, nextLine++);
        this.add(pb, columnIndex, nextLine++);

        return this;
    }
}
