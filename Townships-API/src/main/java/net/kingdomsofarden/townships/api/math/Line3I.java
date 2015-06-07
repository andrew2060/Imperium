package net.kingdomsofarden.townships.api.math;

import org.bukkit.util.Vector;

/**
 * Represents a line segment between two {@link Point3I}, commonly used for bounding region edges.
 * Note that lines are limited to traveling on only one axis (axis-aligned)
 */
public class Line3I {
    private final Point3I point1;
    private final Point3I point2;

    public Line3I(Point3I point1, Point3I point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public Point3I getPoint1() {
        return point1;
    }

    public Point3I getPoint2() {
        return point2;
    }


    /**
     * @param other The line to compute an intersection with
     * @return A {@link Point3I} representing the intersection point between this line and other, or null if no such
     * intersection exists
     */
    public Point3I getIntersection(Line3I other) {
        // Determine which coordinates are static since this specific case of lines only operates on 1 dimension
        Point3I other1 = other.getPoint1();
        Point3I other2 = other.getPoint2();
        boolean x1 = point1.getX() != point2.getX();
        boolean y1 = point1.getY() != point2.getY();
        boolean x2 = other1.getX() != other2.getX();
        boolean y2 = other1.getY() != other2.getY();

        // Construct intersection
        // Verify that the lines are not parallel, i.e. the two lines don't have the same non-statics
        if (x1 && x2 || y1 && y2 || (!(x1) && !(y1) && !(x2) && !(y2))) {
            return null;
        }
        // 1: We know that at least 1 coordinate is the same in both, otherwise it would not intersect
        // 2: Assuming the lines are not parallel: the non-static point on the other line can be ascertained from
        //    our original point1 for that same axis (since it is static)
        int x, y, z;
        if (x1) {
            x = other1.getX();
            y = point1.getY();
            z = point1.getZ();
        } else if (y1) {
            x = point1.getX();
            y = other1.getY();
            z = point1.getZ();
        } else {
            x = point1.getX();
            y = point1.getY();
            z = other1.getZ();
        }
        // Now we must verify that the lines actually intersect with our determined x/y/z points
        boolean valid;
        if (x1) {
            valid = point1.getX() <= x && x <= point2.getX();
        } else if (y1) {
            valid = point1.getY() <= y && y <= point2.getY();
        } else {
            valid = point1.getZ() <= z && z <= point2.getZ();
        }
        return valid ? new Point3I(x, y, z) : null;
    }

    public Axis getAxisOfTravel() {
        if (point1.getX() != point2.getX()) {
            return Axis.X;
        } else if (point1.getY() != point2.getY()) {
            return Axis.Y;
        } else {
            return Axis.Z; // Axis aligned, has to be one of three
        }
    }

    public Vector asVector(Point3I origin) {
        if (origin.equals(point1)) {
            return new Vector(point2.getX() - point1.getX(), point2.getY() - point1.getY(),
                point2.getZ() - point1.getZ());
        } else if (origin.equals(point2)) {
            return new Vector(point1.getX() - point2.getX(), point1.getY() - point2.getY(),
                point1.getZ() - point2.getZ());
        } else {
            return null;
        }
    }

}
