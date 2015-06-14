package net.kingdomsofarden.townships.api.math;

import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Represents a face of a polyhedron
 */
public class Face {
    private Plane plane;
    private Collection<Line3I> edges;
    private Collection<Point3I> vertices;

    public Face(Line3I edge1, Line3I edge2) {
        Axis a1 = edge1.getAxisOfTravel();
        Axis a2 = edge2.getAxisOfTravel();
        plane = new Plane(Axis.fromIntValue(6 - (a1.asIntValue() + a2.asIntValue())));
        edges = new LinkedHashSet<>();
        vertices = new LinkedHashSet<>();
        edges.add(edge1);
        edges.add(edge2);
        if (a1 == a2) {
            if (isAxisAligned(edge1.getPoint1(), edge2.getPoint1()) && isAxisAligned(
                edge1.getPoint2(), edge2.getPoint2())) {
                edges.add(new Line3I(edge1.getPoint1(), edge2.getPoint1()));
                edges.add(new Line3I(edge1.getPoint2(), edge2.getPoint2()));
            } else if (isAxisAligned(edge1.getPoint1(), edge2.getPoint2()) && isAxisAligned(
                edge1.getPoint2(), edge2.getPoint1())) {
                edges.add(new Line3I(edge1.getPoint1(), edge2.getPoint2()));
                edges.add(new Line3I(edge1.getPoint2(), edge2.getPoint1()));
            } else {
                throw new IllegalArgumentException(
                    "Supplied edges are parallel and not " + "axis-aligned to each other!");
            }
            vertices.add(edge1.getPoint1());
            vertices.add(edge2.getPoint1());
            vertices.add(edge1.getPoint2());
            vertices.add(edge2.getPoint2());
        } else {
            vertices.add(edge1.getPoint1());
            vertices.add(edge1.getPoint2());
            Point3I common;
            Point3I other;
            if (vertices.contains(edge2.getPoint1())) {
                common = edge2.getPoint1();
                other = edge2.getPoint2();
            } else if (vertices.contains(edge2.getPoint2())) {
                common = edge2.getPoint2();
                other = edge2.getPoint1();
            } else {
                throw new IllegalArgumentException("Supplied edges do not share a common vertex!");
            }
            Vector v = edge1.asVector(common);
            Point3I lastVertex =
                new Point3I(other.getX() + v.getBlockX(), other.getY() + v.getBlockY(),
                    other.getZ() + v.getBlockZ());
            vertices.add(other);
            vertices.add(lastVertex);
            Point3I line1Other =
                edge1.getPoint1().equals(common) ? edge1.getPoint2() : edge1.getPoint1();
            edges.add(new Line3I(line1Other, lastVertex));
            edges.add(new Line3I(other, lastVertex));
        }
    }

    private boolean isAxisAligned(Point3I point1, Point3I point2) {
        int sum = 0;
        if (point1.getX() == point2.getX()) {
            sum++;
        }
        if (point1.getY() == point2.getY()) {
            sum++;
        }
        if (point1.getZ() == point2.getZ()) {
            sum++;
        }
        return sum >= 2;
    }

    public Plane getPlane() {
        return plane;
    }

    public Collection<Point3I> getVertices() {
        return vertices;
    }

    public Collection<Line3I> getEdges() {
        return edges;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Face face = (Face) o;
        return plane.equals(face.plane) && edges.equals(face.edges);
    }

    @Override public int hashCode() {
        int result = plane.hashCode();
        result = 31 * result + edges.hashCode();
        return result;
    }
}
