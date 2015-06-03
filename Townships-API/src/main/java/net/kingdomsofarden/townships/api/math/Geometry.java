package net.kingdomsofarden.townships.api.math;

import java.util.Collection;

/**
 * Represents some geometric structure
 */
public interface Geometry {
    /**
     * @return Vectors in sorted order
     */
    Collection<Vector3I> getVertices();
}
