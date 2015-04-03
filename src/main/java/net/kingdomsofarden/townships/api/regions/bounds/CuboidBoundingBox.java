package net.kingdomsofarden.townships.api.regions.bounds;

public interface CuboidBoundingBox extends BoundingBox {
    // Bounding Methods TODO Javadocs
    int getMinX();
    int getMaxX();
    int getMinY();
    int getMaxY();
    int getMinZ();
    int getMaxZ();
}
