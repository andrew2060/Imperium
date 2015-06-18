package net.kingdomsofarden.townships.api.math;

import java.util.Arrays;

/**
 * Represents a maskable segment, that is to say a segment whose endpoints can be temporarily
 * changed so long as they are on the same line
 */
public class Segment3IMaskable extends Segment3I {
    private final Point3I actualPoint1;
    private final Point3I actualPoint2;
    private final double[] vector;


    public Segment3IMaskable(Point3I point1, Point3I point2) {
        super(point1, point2);
        actualPoint1 = this.point1;
        actualPoint2 = this.point2;
        if (point1.equals(point2)) {
            throw new IllegalArgumentException();
        }
        vector = new double[] {point2.getX() - point1.getX(), point2.getY() - point1.getY(),
            point2.getZ() - point1.getZ()};
        // Normalize
        double length = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] *
            vector[2]);
        vector[0] /= length;
        vector[1] /= length;
        vector[2] /= length;
    }

    public Point3I getActualPoint1() {
        return actualPoint1;
    }

    public Point3I getActualPoint2() {
        return actualPoint2;
    }

    /**
     * Masks an endpoint with a new endpoint
     *
     * @param side The side of the line to mask
     * @param mask The masked item
     * @return The previous endpoint on the parameter's side
     */
    public Point3I mask(Point3I side, Point3I mask) {
        double[] testVec = new double[] {mask.getX() - actualPoint1.getX(), mask.getY() -
            actualPoint1.getY(), mask.getZ() - actualPoint1.getZ()};
        double length = Math.sqrt(testVec[0] * testVec[0] + testVec[1] * testVec[1] + testVec[2] *
            testVec[2]);
        testVec[0] /= length;
        testVec[1] /= length;
        testVec[2] /= length;
        if (!Arrays.equals(testVec, vector)) {
            throw new IllegalArgumentException("Provided point must be on the line");
        }

        // Verify constraints (that mask is actually on the segment)
        Point3I ret;
        if (side.equals(actualPoint1)) {
            ret = point1;
            point1 = mask;
        } else if (side.equals(actualPoint2)) {
            ret = point2;
            point2 = mask;
        } else {
            throw new IllegalArgumentException();
        }
        recheckOrdering();
        return ret;
    }

    private void recheckOrdering() {
        boolean order = true; // Current order is correct
        if (point1.getX() == point2.getX()) {
            if (point1.getZ() == point2.getZ()) {
                if (point1.getY() > point2.getY()) {
                    order = false;
                }
            } else if (point1.getZ() > point2.getZ()) {
                order = false;
            }
        } else if (point1.getX() > point2.getX()) {
            order = false;
        }
        if (!order) {
            Point3I temp = point1;
            point1 = point2;
            point2 = temp;
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        Segment3IMaskable that = (Segment3IMaskable) o;

        return actualPoint1.equals(that.actualPoint1) && actualPoint2.equals(that.actualPoint2);

    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + actualPoint1.hashCode();
        result = 31 * result + actualPoint2.hashCode();
        return result;
    }
}
