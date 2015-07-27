package utilities;

public class Cell
{
    private double width;
    private double height;

    public Cell(double w, double h)
    {
        this.width = w;
        this.height = h;
    }

    public double getWidth()
    {
        return width;
    }

    public double setWidth(double width)
    {
        return this.width = width;
    }

    public double growWidth(double wGrow)
    {
        return this.width += wGrow;
    }

    public double getHeight()
    {
        return height;
    }

    public double setHeight(double height)
    {
        return this.height = height;
    }

    public double growHeight(double hGrow)
    {
        return this.height += hGrow;
    }

    public Cell grow(Cell other)
    {
        this.growHeight(other.getHeight());
        this.growWidth(other.getWidth());
        return this;
    }

    @Override
    public String toString()
    {
        return "Cell [width=" + width + ", height=" + height + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(height);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(width);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Cell)) return false;
        Cell other = (Cell) obj;
        if (Double.doubleToLongBits(height) != Double.doubleToLongBits(other.height)) return false;
        if (Double.doubleToLongBits(width) != Double.doubleToLongBits(other.width)) return false;
        return true;
    }

}
