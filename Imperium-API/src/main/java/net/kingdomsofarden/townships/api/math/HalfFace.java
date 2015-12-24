package net.kingdomsofarden.townships.api.math;

import org.bukkit.util.Vector;

/**
 * Represents a half face, by which we have a point and 2 vertices
 */
public class HalfFace {
    private Plane plane;
    private Vector v1;
    private Vector v2;

    public HalfFace(Plane plane, Vector v1, Vector v2) {
        this.plane = plane;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        HalfFace halfFace = (HalfFace) o;

        return !(plane != null ? !plane.equals(halfFace.plane) : halfFace.plane != null) && !(
            v1 != null ? !v1.equals(halfFace.v1) : halfFace.v1 != null) && !(v2 != null ?
            !v2.equals(halfFace.v2) :
            halfFace.v2 != null);

    }

    @Override public int hashCode() {
        int result = plane != null ? plane.hashCode() : 0;
        result = 31 * result + (v1 != null ? v1.hashCode() : 0);
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }
}
