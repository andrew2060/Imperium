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
     * @return The edges associated with a given vertex, represented as {@link Line3I}
     */
    Collection<Line3I> getEdges(Point3I vertex);

    /**
     * @return The faces associated
     */
    Collection<Face3I> getFaces();
}
