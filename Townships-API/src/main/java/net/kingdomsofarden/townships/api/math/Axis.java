package net.kingdomsofarden.townships.api.math;

/**
 * Represents an Axis
 */
public enum Axis {
    X(1), Y(2), Z(3);

    private final int intRep;

    Axis(int intRep) {
        this.intRep = intRep;
    }

    public static Axis fromIntValue(int val) {
        return val == 1 ? X : val == 2 ? Y : Z;
    }

    public int asIntValue() {
        return intRep;
    }
}
