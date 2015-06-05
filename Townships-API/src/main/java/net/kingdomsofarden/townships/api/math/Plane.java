package net.kingdomsofarden.townships.api.math;

/**
 * Represents a 2-Dimensional Plane
 */
public class Plane {
    private Axis axis;
    private Axis perpendicular;
    private int value;

    public Plane(Axis axis, Axis perpendicular, int value) {
        this.axis = axis;
        this.perpendicular = perpendicular;
        this.value = value;
    }

    public Axis getAxis() {
        return axis;
    }

    public Axis getPerpendicularFace() {
        return perpendicular;
    }

    public int getValue() {
        return value;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Plane plane = (Plane) o;

        return value == plane.value && axis == plane.axis && perpendicular == plane.perpendicular;

    }

    @Override public int hashCode() {
        int result = axis != null ? axis.hashCode() : 0;
        result = 31 * result + (perpendicular != null ? perpendicular.hashCode() : 0);
        result = 31 * result + value;
        return result;
    }

    public enum Axis {
        X, Y, Z;
    }
}
