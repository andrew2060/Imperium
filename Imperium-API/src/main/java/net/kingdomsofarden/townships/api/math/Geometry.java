package net.kingdomsofarden.townships.api.math;

import java.util.Collection;

/**
 * Represents some geometric structure
 */
public interface Geometry {
    /**
     * @return Vertices in sorted order
     */
    Collection<Point3I> getVertices();

    /**
     * @return The edges associated with a given vertex, represented as {@link Segment3I}
     */
    Collection<Segment3I> getEdges(Point3I vertex);

    /**
     * @return All the edges within this geometry, represented as {@link Segment3I}
     */
    Collection<Segment3I> getAllEdges();

    /**
     * @return The faces associated with this geometry
     */
    Collection<Face> getFaces();

    /**
     * @return A collection (unsorted) of all the rectangles (can be singular or plural) that
     * make up this geometry (a 2d representation of the elements of this bounding region)
     */
    Collection<Rectangle> getBaseRectangles();
}
