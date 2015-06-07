package net.kingdomsofarden.townships.api.math;

/**
 * Represents a 2-Dimensional Plane
 */
public class Plane {

    private final Axis axis;
    private final int value;

    public Plane(Axis axis, int value) {
        this.axis = axis;
        this.value = value;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Plane plane = (Plane) o;

        if (value != plane.value)
            return false;
        return axis == plane.axis;

    }

    @Override public int hashCode() {
        int result = axis != null ? axis.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }

    public Axis getAxis() {
        return axis;
    }

}
