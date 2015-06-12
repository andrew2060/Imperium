package net.kingdomsofarden.townships.api.math;

public class Rectangle {
    private Point3I point1;
    private Point3I point2;
    private int startX;
    private int endX;
    private int startZ;
    private int endZ;

    public Rectangle(Point3I point1, Point3I point2) {
        this.startX = Math.min(point1.getX(), point2.getX());
        this.endX = Math.max(point1.getX(), point2.getX());
        this.startZ = Math.min(point1.getZ(), point2.getZ());
        this.endZ = Math.max(point1.getZ(), point2.getZ());
        this.point1 = new Point3I(startX, 0, startZ);
        this.point2 = new Point3I(endX, 0, endZ);
    }

    public Point3I getPoint1() {
        return point1;
    }

    public Point3I getPoint2() {
        return point2;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Rectangle rectangle = (Rectangle) o;

        if (point1 != null ? !point1.equals(rectangle.point1) : rectangle.point1 != null)
            return false;
        return !(point2 != null ? !point2.equals(rectangle.point2) : rectangle.point2 != null);

    }

    @Override public int hashCode() {
        int result = point1 != null ? point1.hashCode() : 0;
        result = 31 * result + (point2 != null ? point2.hashCode() : 0);
        return result;
    }

    public int getStartX() {
        return startX;
    }

    public int getEndX() {
        return endX;
    }

    public int getStartZ() {
        return startZ;
    }

    public int getEndZ() {
        return endZ;
    }
}
