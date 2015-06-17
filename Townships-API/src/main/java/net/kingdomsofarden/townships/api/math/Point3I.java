package net.kingdomsofarden.townships.api.math;

import com.google.gson.JsonObject;

public class Point3I {
    private int x;
    private int y;
    private int z;

    public Point3I(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Point3I fromJsonObject(JsonObject obj) {
        return new Point3I(obj.get("x").getAsInt(), obj.get("y").getAsInt(),
            obj.get("z").getAsInt());
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

    public JsonObject asJsonObject() {
        JsonObject ret = new JsonObject();
        ret.addProperty("x", x);
        ret.addProperty("y", y);
        ret.addProperty("z", z);
        return ret;
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
