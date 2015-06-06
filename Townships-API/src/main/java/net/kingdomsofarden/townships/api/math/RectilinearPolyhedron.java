package net.kingdomsofarden.townships.api.math;


import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;

public class RectilinearPolyhedron {

    public void add(BoundingArea bounds) {

    }


    private abstract class RectilinearPolyhedronTreeStructure {
        public abstract void add(BoundingArea bounds);
    }


    private class RectilinearPolyhedronTreeRoot extends RectilinearPolyhedronTreeStructure {

        @Override public void add(BoundingArea bounds) {

        }
    }


    private class RectilinearPolyhedronNode extends RectilinearPolyhedronTreeStructure {

        // x|z|y
        private RectilinearPolyhedronTreeStructure[][][] children =
            new RectilinearPolyhedronTreeStructure[2][2][2];
        private int zero;
        private int offsetX;
        private int offsetZ;
        private int offsetY;

        public RectilinearPolyhedronNode(int width, int x, int y, int z) {
            this.zero = width;
            this.offsetX = x;
            this.offsetZ = z;
            this.offsetY = y;
        }

        @Override public void add(BoundingArea bounds) {
            int[][][] touched = new int[2][2][2];
            for (Point3I vec : bounds.getVertices()) {
                int x = 0;
                int y = 0;
                int z = 0;
                if (vec.getX() + offsetX > zero) {
                    x = 1;
                }
                if (vec.getZ() + offsetZ > zero) {
                    z = 1;
                }
                if (vec.getY() + offsetY > zero) {
                    y = 1;
                }
                if (touched[x][z][y] != 1) {
                    touched[x][z][y] = 1;
                    if (children[x][z][y] == null) {
                        if (zero <= 8) { // Box width of one chunk, insert leaf
                            children[x][z][y] = new RectilinearPolyhedronLeaf(zero);
                        } else {
                            int xCenter = offsetX;
                            int zCenter = offsetZ;
                            int yCenter = offsetY;
                            if (x == 0) {
                                xCenter -= zero / 2;
                            } else {
                                xCenter += zero / 2;
                            }
                            if (z == 0) {
                                zCenter -= zero / 2;
                            } else {
                                zCenter += zero / 2;
                            }
                            if (y == 0) {
                                yCenter -= zero / 2;
                            } else {
                                yCenter += zero / 2;
                            }
                            children[x][z][y] =
                                new RectilinearPolyhedronNode(zero / 2, xCenter, yCenter, zCenter);
                        }
                    }
                    children[x][z][y].add(bounds);
                }
            }
        }

    }


    private class RectilinearPolyhedronLeaf extends RectilinearPolyhedronTreeStructure {

        public RectilinearPolyhedronLeaf(int width) {
            int volume = 8 * (int) Math.pow(width, 3);
            int size2d = 4 * (int) Math.pow(width, 2);

        }

        @Override public void add(BoundingArea bounds) {

        }
    }

}
