package net.kingdomsofarden.townships.api.math;

/**
 * Represents a 2-Dimensional Plane
 */
public class Plane {

    private final Axis axis;

    public Plane(Axis axis) {
        this.axis = axis;
    }

    public Axis getNormalAxis() {
        return axis;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Plane plane = (Plane) o;

        return axis == plane.axis;

    }

    @Override public int hashCode() {
        return axis != null ? axis.hashCode() : 0;
    }
}
