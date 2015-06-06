package net.kingdomsofarden.townships.api.math;

public class Point3I {
    private int x;
    private int y;
    private int z;

    public Point3I(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Point3I)) {
            return false;
        } else {
            Point3I v = (Point3I) o;
            return v.x == x && v.y == y && v.z == z;
        }
    }

}
