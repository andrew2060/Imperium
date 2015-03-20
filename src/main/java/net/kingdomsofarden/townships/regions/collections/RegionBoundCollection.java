package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class RegionBoundCollection implements Area {

    // Bounds:
    protected int minX;
    protected int maxX;
    protected int minZ;
    protected int maxZ;

    // Inheritance
    protected RegionBoundCollection parent;
    protected int quadrant;




    @Override
    public boolean add(Region region) {
        return add(region.getBounds());
    }

    protected abstract boolean add(BoundingBox bound);

    @Override
    public boolean addAll(Collection<? extends Region> c) {
        boolean ret = false;
        // Check bounds
        for (Region r : c) {
            if (!isInBounds(r.getBounds())) {
                throw new IllegalArgumentException("A specified region " + r + " was not contained in some part of this " +
                        "collection's bounds");
            }
            if (add(r.getBounds())) {
                ret = true;
            }
        }
        return ret;
    }

    public boolean isInBounds(Location loc) {
        return (checkBounds(loc.getX(), minX, maxX) && checkBounds(loc.getZ(), minZ, maxZ));
    }

    /**
     * @param b A bounding box to check
     * @return True if some portion of b falls within the area managed by this collection
     */
    protected boolean isInBounds(BoundingBox b) {
        boolean xMinBound = checkBounds(b.getMinX(), minX, maxX);
        boolean zMinBound = checkBounds(b.getMinZ(), minZ, maxZ);
        boolean xMaxBound = checkBounds(b.getMaxX(), minX, maxX);
        boolean zMaxBound = checkBounds(b.getMaxZ(), minZ, maxZ);
        boolean encapsulatesX = b.getMinX() < minX && b.getMaxX() > maxX;
        boolean encapsulatesZ = b.getMinZ() < minZ && b.getMaxZ() > maxZ;

        return ((xMinBound || xMaxBound) && (zMinBound || zMaxBound)) // Standard case, one corners
                // Completely contained
                || (encapsulatesX && encapsulatesZ)
                // Partially contained
                || (((xMinBound || xMaxBound) && encapsulatesZ) || ((zMinBound || zMaxBound) && encapsulatesX)) ;
    }

    protected boolean checkBounds(double x, int l, int u) {
        return l <= x && x <= u;
    }

    @Override
    public Collection<Region> getContents() {
        Set<Region> ret = new HashSet<Region>();
        constructContainedRegions(ret);
        return ret;
    }

    @Override
    public Optional<Area> getNeighbor(int direction) {
        if (parent == null) {
            return Optional.absent();
        }
        direction = direction % 8;
        switch(quadrant) {
            case 0:
                switch (direction) {
                    case 0: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(0);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(1));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 1: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(0);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            o = r.getNeighbor(2);
                            if (o.isPresent()) {
                                return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(3));
                            } else {
                                return Optional.absent();
                            }
                        } else {
                            o = parent.getNeighbor(2);
                            if (o.isPresent()) {
                                r = (RegionBoundCollection) o.get();
                                o = r.getNeighbor(0);
                                if (o.isPresent()) {
                                    return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(3));
                                } else {
                                    return Optional.absent();
                                }
                            } else {
                                return Optional.absent();
                            }
                        }
                    }
                    case 2: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(2);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(2));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 3: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(2);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(3));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 4:
                        return Optional.fromNullable(parent.getQuadrant(1));
                    case 5: {
                        return Optional.fromNullable(parent.getQuadrant(3));
                    }
                    case 6:
                        return Optional.fromNullable(parent.getQuadrant(2));
                    case 7: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(0);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(1));
                        } else {
                            return Optional.absent();
                        }
                    }
                }
            case 1:
                switch (direction) {
                    case 0:
                        return Optional.fromNullable(parent.getQuadrant(0));
                    case 1: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(2);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(2));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 2: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(2);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(3));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 3: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(2);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            o = r.getNeighbor(4);
                            if (o.isPresent()) {
                                return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(2));
                            } else {
                                return Optional.absent();
                            }
                        } else {
                            o = parent.getNeighbor(4);
                            if (o.isPresent()) {
                                r = (RegionBoundCollection) o.get();
                                o = r.getNeighbor(2);
                                if (o.isPresent()) {
                                    return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(2));
                                } else {
                                    return Optional.absent();
                                }
                            } else {
                                return Optional.absent();
                            }
                        }
                    }
                    case 4: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(4);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(0));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 5: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(4);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(2));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 6:
                        return Optional.fromNullable(parent.getQuadrant(3));
                    case 7:
                        return Optional.fromNullable(parent.getQuadrant(2));
                }
            case 2:
                switch (direction) {
                    case 0: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(0);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(3));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 1: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(0);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(1));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 2:
                        return Optional.fromNullable(parent.getQuadrant(0));
                    case 3:
                        return Optional.fromNullable(parent.getQuadrant(1));
                    case 4:
                        return Optional.fromNullable(parent.getQuadrant(3));
                    case 5: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(6);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(1));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 6: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(0);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(0));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 7: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(0);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            o = r.getNeighbor(6);
                            if (o.isPresent()) {
                                return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(1));
                            } else {
                                return Optional.absent();
                            }
                        } else {
                            o = parent.getNeighbor(6);
                            if (o.isPresent()) {
                                r = (RegionBoundCollection) o.get();
                                o = r.getNeighbor(0);
                                if (o.isPresent()) {
                                    return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(1));
                                } else {
                                    return Optional.absent();
                                }
                            } else {
                                return Optional.absent();
                            }
                        }
                    }
                }
            case 3:
                switch (direction) {
                    case 0:
                        return Optional.fromNullable(parent.getQuadrant(2));
                    case 1:
                        return Optional.fromNullable(parent.getQuadrant(0));
                    case 2:
                        return Optional.fromNullable(parent.getQuadrant(1));
                    case 3: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(4);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(0));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 4: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(4);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(2));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 5: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(4);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            o = r.getNeighbor(6);
                            if (o.isPresent()) {
                                return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(0));
                            } else {
                                return Optional.absent();
                            }
                        } else {
                            o = parent.getNeighbor(6);
                            if (o.isPresent()) {
                                r = (RegionBoundCollection) o.get();
                                o = r.getNeighbor(4);
                                if (o.isPresent()) {
                                    return Optional.fromNullable(((RegionBoundCollection) o.get()).getQuadrant(0));
                                } else {
                                    return Optional.absent();
                                }
                            } else {
                                return Optional.absent();
                            }
                        }
                    }
                    case 6: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(6);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(1));
                        } else {
                            return Optional.absent();
                        }
                    }
                    case 7: {
                        RegionBoundCollection r;
                        Optional<Area> o = parent.getNeighbor(6);
                        if (o.isPresent()) {
                            r = (RegionBoundCollection) o.get();
                            return Optional.fromNullable(r.getQuadrant(0));
                        } else {
                            return Optional.absent();
                        }
                    }
                }
        }
        return Optional.absent();
    }

    protected abstract Area getQuadrant(int quad);

    protected abstract void constructContainedRegions(Set<Region> regions);

    public abstract Optional<Area> getBoundingArea(int x, int z);
}