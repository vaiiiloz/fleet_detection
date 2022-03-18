package Entity;

public class Face {

    protected static final int DELETED = 3;
    protected static final int NON_CONVEX = 2;
    protected static final int VISIBLE = 1;

    protected double area;
    protected HalfEdge he0;
    protected int mask = VISIBLE;
    protected Face next;
    protected int numVerts;
    protected Vertex outside;
    protected double planeOffset;
    private Point3D centroid;
    private Vector3D normal;

    public Face() {
        normal = new Vector3D();
        centroid = new Point3D();
        mask = VISIBLE;
    }

    public static Face create(Vertex[] vtxArray, int[] indices) {
        return null;
    }

    public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2) {
        return null;
    }

    /**
     * Constructs a triangule Face from vertices v0, v1, and v2.
     *
     * @param v0      first vertex
     * @param v1      second vertex
     * @param v2      third vertex
     * @param minArea
     * @return
     */
    public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2, double minArea) {
        Face face = new Face();
        HalfEdge he0 = new HalfEdge(v0, face);
        HalfEdge he1 = new HalfEdge(v1, face);
        HalfEdge he2 = new HalfEdge(v2, face);

        he0.prev = he2;
        he0.next = he1;
        he1.prev = he0;
        he1.next = he2;
        he2.prev = he1;
        he2.next = he0;

        face.he0 = he0;

        //compute the normal and offset
        face.computeNormalAndCentroid();
        return face;
    }

    /**
     * Compute Centroid
     */
    public void computeCentroid(Point3D centroid) {
        centroid.setZero();
        HalfEdge he = he0;
        do {
            centroid.add(he.head().pnt);
            he = he.next;
        } while (he != he0);
        centroid.scale(1 / (double) numVerts);
    }

    public void computeNormal(Vector3D normal) {
        HalfEdge he1 = he0.next;
        HalfEdge he2 = he1.next;

        Point3D p0 = he0.head().pnt;
        Point3D p2 = he1.head().pnt;

        double d2x = p2.x - p0.x;
        double d2y = p2.y - p0.y;
        double d2z = p2.z - p0.z;

        normal.setZero();

        numVerts = 2;

        while (he2 != he0) {
            double d1x = d2x;
            double d1y = d2y;
            double d1z = d2z;

            p2 = he2.head().pnt;
            d2x = p2.x - p0.x;
            d2y = p2.y - p0.y;
            d2z = p2.z - p0.z;

            normal.x += d1y * d2z - d1z * d2y;
            normal.y += d1z * d2x - d1x * d2z;
            normal.z += d1x * d2y - d1y * d2x;

            he1 = he2;
            he2 = he2.next;
            numVerts++;
        }
        area = normal.norm();
        normal.scale(1 / area);
    }

    public void computeNormal(Vector3D normal, double minArea) {
        computeNormal(normal);

        if (area < minArea) {
            //make the normal more robust by remove components parallel to the longest edge

            HalfEdge hedgeMax = null;
            double lenSqrMax = 0;
            HalfEdge hedge = he0;
            do {
                double lenSqr = hedge.lengthSquared();
                if (lenSqr > lenSqrMax) {
                    hedgeMax = hedge;
                    lenSqrMax = lenSqr;
                }
                hedge = hedge.next;
            } while (hedge != he0);

            Point3D p2 = hedgeMax.head().pnt;
            Point3D p1 = hedgeMax.tail().pnt;
            double lenMax = Math.sqrt(lenSqrMax);
            double ux = (p2.x - p1.x) / lenMax;
            double uy = (p2.y - p1.y) / lenMax;
            double uz = (p2.z - p1.z) / lenMax;
            double dot = normal.x * ux + normal.y * uy + normal.z * uz;
            normal.x -= dot * ux;
            normal.y -= dot * uy;
            normal.z -= dot * uz;

            normal.normalize();
        }
    }

    /**
     * Compute the distance from a pont p to the plane of this face
     *
     * @param p the poin
     * @return distance from the point to the plane
     */
    public double distanceToPlane(Point3D p) {
        return normal.x * p.x + normal.y * p.y + normal.z * p.z - planeOffset;
    }

    /**
     * Finds the half-edge within this face which have tail vt and head vh
     *
     * @param vt tail point
     * @param vh heat point
     * @return the half-edge, or null if none is found.
     */
    public HalfEdge findEdge(Vertex vt, Vertex vh) {
        HalfEdge he = he0;
        do {
            if (he.head() == vh && he.tail() == vt) {
                return he;
            }
            he = he.next;
        } while (he != he0);
        return null;
    }

    public Point3D getCentroid() {
        return centroid;
    }

    /**
     * Gets the i-th half-edge associated with the face
     *
     * @param i the half-edge index, in the range 0-2
     * @return the half-edge
     */
    public HalfEdge getEdge(int i) {
        HalfEdge he = he0;
        while (i > 0) {
            he = he.next;
            i--;
        }
        while (i < 0) {
            he = he.prev;
            i++;
        }
        return he;
    }

    public HalfEdge getFirstEdge() {
        return he0;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public void getVertexIndices(int[] idxs) {
        HalfEdge he = he0;
        int i = 0;
        do {
            idxs[i++] = he.head().index;
            he = he.next;
        } while (he != he0);
    }

    public String getVertexString() {
        String s = null;
        HalfEdge he = he0;
        do {
            if (s == null) {
                s = "" + he.head().index;
            } else {
                s += " " + he.head().index;
            }
            he = he.next;
        } while (he != he0);
        return s;
    }

    public int mergeAdjacentFace(HalfEdge hedgeAdj, Face[] discarded) {
        Face oppFace = hedgeAdj.oppositeFace();
        int numDiscarded = 0;

        discarded[numDiscarded++] = oppFace;
        oppFace.mask = DELETED;
        HalfEdge hedgeOpp = hedgeAdj.getOpposite();

        HalfEdge hedgeAdjPrev = hedgeAdj.prev;
        HalfEdge hedgeAdjNext = hedgeAdj.next;
        HalfEdge hedgeOppPrev = hedgeOpp.prev;
        HalfEdge hedgeOppNext = hedgeOpp.next;

        while (hedgeAdjPrev.oppositeFace() == oppFace) {
            hedgeAdjPrev = hedgeAdjPrev.prev;
            hedgeOppNext = hedgeOppNext.next;
        }

        while (hedgeAdjNext.oppositeFace() == oppFace) {
            hedgeOppPrev = hedgeOppPrev.prev;
            hedgeAdjNext = hedgeAdjNext.next;
        }

        HalfEdge hedge;

        for (hedge = hedgeOppNext; hedge != hedgeOppPrev.next; hedge = hedge.next) {
            hedge.face = this;
        }

        if (hedgeAdj == he0) {
            he0 = hedgeAdjNext;
        }

        //handle the half edges at the head
        Face discardedFace;

        discardedFace = connectHalfEdges(hedgeOppPrev, hedgeAdjNext);
        if (discardedFace != null) {
            discarded[numDiscarded++] = discardedFace;
        }

        //handle the half edges at the tail
        discardedFace = connectHalfEdges(hedgeAdjPrev, hedgeOppNext);
        if (discardedFace != null) {
            discarded[numDiscarded++] = discardedFace;
        }

        computeNormalAndCentroid();
        checkConsistency();
        return numDiscarded;
    }

    public int getNumVerts() {
        return numVerts;
    }

    public void triangulate(FaceList newFaces, double minArea) {
        HalfEdge hedge;

        if (getNumVerts() < 4) {
            return;
        }

        Vertex v0 = he0.head();

        hedge = he0.next;

        HalfEdge oppPrev = hedge.opposite;
        Face face0 = null;

        for (hedge = hedge.next; hedge != he0.prev; hedge = hedge.next) {
            Face face = createTriangle(v0, hedge.prev.head(), hedge.head(), minArea);
            face.he0.next.setOpposite(oppPrev);
            face.he0.prev.setPrev(hedge.opposite);
            oppPrev = face.he0;
            newFaces.add(face);
            if (face0 == null) {
                face0 = face;
            }
        }
        hedge = new HalfEdge(he0.prev.prev.head(), this);
        hedge.setOpposite(oppPrev);

        hedge.prev = he0;
        hedge.prev.next = hedge;

        hedge.next = he0.prev;
        hedge.next.prev = hedge;

        computeNormalAndCentroid(minArea);
        checkConsistency();

        for (Face face = face0; face != null; face = face.next) {
            face.checkConsistency();
        }
    }

    /**
     * return the squared area of the triangle defined by the half edge hedge- and the point at the head of hedge1
     *
     * @param hedge0
     * @param hedge1
     * @return
     */
    public double areaSquare(HalfEdge hedge0, HalfEdge hedge1) {
        Point3D p0 = hedge0.tail().pnt;
        Point3D p1 = hedge0.head().pnt;
        Point3D p2 = hedge1.head().pnt;

        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dz1 = p1.z - p0.z;

        double dx2 = p2.x - p0.x;
        double dy2 = p2.y - p0.y;
        double dz2 = p2.z - p0.z;

        double x = dy1 * dz2 - dz1 * dy2;
        double y = dz1 * dx2 - dx1 * dz2;
        double z = dx1 * dy2 - dy1 * dx2;
        return x * x + y * y + z * z;
    }

    private void computeNormalAndCentroid() {
        computeNormal(normal);
        computeCentroid(centroid);
        planeOffset = normal.dot(centroid);
        int numv = 0;
        HalfEdge he = he0;
        do {
            numv++;
            he = he.next;
        } while (he != he0);
        if (numv != numVerts) {
            throw new RuntimeException("face " + getVertexString() + " numVerts" +
                    numVerts + " shoule be " + numv);
        }
    }

    private void computeNormalAndCentroid(double minArea) {
        computeNormal(normal, minArea);
        computeCentroid(centroid);
        planeOffset = normal.dot(centroid);
    }

    private Face connectHalfEdges(HalfEdge hedgePrev, HalfEdge hedge) {
        Face discardFace = null;

        //have a redundant edge that can get rid off
        if (hedgePrev.oppositeFace() == hedge.oppositeFace()) {
            Face oppFace = hedge.oppositeFace();
            HalfEdge hedgeOpp;

            if (hedgePrev == he0) {
                he0 = hedge;
            }

            if (oppFace.getNumVerts() == 3) {
                //can get rid of the apposite face altogether
                hedgeOpp = hedge.getOpposite().prev.getOpposite();
                oppFace.mask = DELETED;
                discardFace = oppFace;
            } else {
                hedgeOpp = hedge.getOpposite().next;

                if (oppFace.he0 == hedgeOpp.prev) {
                    oppFace.he0 = hedgeOpp;
                }

                hedgeOpp.prev = hedgeOpp.prev.prev;
                hedgeOpp.prev.next = hedgeOpp;
            }
            hedge.prev = hedgePrev.prev;
            hedge.prev.next = hedge;

            hedge.opposite = hedgeOpp;
            hedgeOpp.opposite = hedge;

            //oppFace was modified, so need to recompute
            oppFace.computeNormalAndCentroid();
        } else {
            hedgePrev.next = hedge;
            hedge.prev = hedgePrev;
        }
        return discardFace;
    }

    void checkConsistency() {
        //do a santiy check on the face
        HalfEdge hedge = he0;
        double maxd = 0;
        int numv = 0;

        if (numVerts < 3) {
            throw new RuntimeException("degenerate face: " + getVertexString());
        }
        do {
            HalfEdge hedgeOpp = hedge.getOpposite();
            if (hedgeOpp == null) {
                throw new RuntimeException("face " + getVertexString() + ": " + "unreflected half edge " + hedge.getVertexString());
            } else if (hedgeOpp.getOpposite() != hedge) {
                throw new RuntimeException("face " + getVertexString() + ": " + "opposite half edge " + hedgeOpp.getVertexString() + " has opposite "
                        + hedgeOpp.getOpposite().getVertexString());
            }
            if (hedgeOpp.head() != hedge.tail() || hedge.head() != hedgeOpp.tail()) {
                throw new RuntimeException("face " + getVertexString() + ": " + "half edge " + hedge.getVertexString() + " reflected by " + hedgeOpp.getVertexString());
            }
            Face oppFace = hedgeOpp.face;
            if (oppFace == null) {
                throw new RuntimeException("face " + getVertexString() + ": " + "no face on half edge " + hedgeOpp.getVertexString());
            } else if (oppFace.mask == DELETED) {
                throw new RuntimeException("face " + getVertexString() + ": " + "opposite face " + oppFace.getVertexString() + " not on hull");
            }
            double d = Math.abs(distanceToPlane(hedge.head().pnt));
            if (d > maxd) {
                maxd = d;
            }
            numv++;
            hedge = hedge.next;
        } while (hedge != he0);

        if (numv != numVerts) {
            throw new RuntimeException("face " + getVertexString() + " numVerts=" + numVerts + " should be " + numv);
        }
    }
}
