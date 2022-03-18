package utils;

import Entity.*;

import java.util.Vector;

public class QuickHull3d {
    /**
     * Specifies that the distance tolerance should be computed automatically from the input point data
     */
    public static final double AUTOMATIC_TOLERANCE = -1;

    protected int findIndex = -1;

    //estimated size of the point set
    protected double charLength;

    protected Vertex[] pointBuffer = new Vertex[0];

    protected int[] vertexPointIndices = new int[0];

    private Face[] discardedFaces = new Face[3];

    private Vertex[] maxVtxs = new Vertex[3];

    private Vertex[] minVtxs = new Vertex[3];

    protected Vector face = new Vector(16);

    protected Vector horizon = new Vector(16);

    private FaceList newFaces = new FaceList();

    private VertexList unclaimed = new VertexList();

    private VertexList claimed = new VertexList();

    protected int numVertices;

    protected int numFaces;

    protected int numPoints;

    protected double explicitTolerance = AUTOMATIC_TOLERANCE;

    protected double tolerance;

    private static final double DOUBLE_PREC = 2.2204460492503131e-16;

    /**
     * Create a convex hull object and initializes it to the convex hull of a set of points
     * @param points
     * input points
     * @throws IllegalArgumentException
     * the number of input points is less than four, or the point appear to be coincident, colinear, or coplanar
     */
    public QuickHull3d(Point3D[] points) throws IllegalArgumentException{
        build(points, points.length);
    }

    /**
     * Constructs the convex hull of a set of points
     * @param points
     * input points
     * @param nump
     * number of input poins
     * @throws IllegalArgumentException
     * the number of input points is less than four or greater than the length if points, or the points appear to be coincident, colinear, or coplanar
     */
    public void build(Point3D[] points, int nump) throws IllegalArgumentException{
        if (nump < 4){
            throw new IllegalArgumentException("Less than four input points specified");
        }
        if (points.length < nump){
            throw new IllegalArgumentException("Point array too small for specified number of points");
        }
        initBuffers(nump);
        setPoints(points, nump);
        buildHull();
    }

    protected void initBuffers(int nump){
        if (pointBuffer.length < nump){
            Vertex[] newBuffer = new Vertex[nump];
            vertexPointIndices = new int[nump];
            for (int i = 0; i < pointBuffer.length; i++){
                newBuffer[i] = pointBuffer[i];
            }
            for (int i = pointBuffer.length; i < nump; i++){
                newBuffer[i] = new Vertex();
            }
            pointBuffer = newBuffer;
        }
        face.clear();
        claimed.clear();
        numFaces = 0;
        numPoints = nump;
    }

    protected void setPoints(Point3D[] pnts, int nump) {
        for (int i = 0; i < nump; i++){
            Vertex vtx = pointBuffer[i];
            vtx.pnt.set(pnts[i]);
            vtx.index = i;
        }
    }

    protected void buildHull(){
        int cnt = 0;
        Vertex eyeVtx;

        computeMaxAndMin();
        createInitialSimplex();
        while ((eyeVtx = nextPointToAdd()) != null){
            addPointToHull(eyeVtx);
            cnt++;
        }
        reindexFacesAndVertices();
    }

    protected void computeMaxAndMin(){
        //initialize max, min
        Vector3D max = new Vector3D();
        Vector3D min = new Vector3D();

        for (int i=0;i<3;i++){
            maxVtxs[i] = minVtxs[i] = pointBuffer[0];
        }
        max.set(pointBuffer[0].pnt);
        min.set(pointBuffer[0].pnt);

        //check all point to find max, min
        for (int i=1;i<numPoints;i++){
            Point3D pnt = pointBuffer[i].pnt;
            //find maxVtxs[0] and minVtxs[0] , vectors with max x and min x
            if (pnt.x >max.x){
                max.x = pnt.x;
                maxVtxs[0] = pointBuffer[i];
            }else if (pnt.x < min.x){
                min.x = pnt.x;
                minVtxs[0] = pointBuffer[i];
            }

            //find maxVtxs[1] and minVtxs[1] , vectors with max y and min y
            if (pnt.y >max.y){
                max.y = pnt.y;
                maxVtxs[1] = pointBuffer[i];
            }else if (pnt.y < min.y){
                min.y = pnt.y;
                minVtxs[1] = pointBuffer[i];
            }

            //find maxVtxs[2] and minVtxs[2] , vectors with max z and min z
            if (pnt.z >max.z){
                max.z = pnt.z;
                maxVtxs[2] = pointBuffer[i];
            }else if (pnt.z < min.z){
                min.z = pnt.z;
                minVtxs[2] = pointBuffer[i];
            }

            charLength = Math.max(max.x - min.x, max.y - min.y);
            charLength = Math.max(max.z - min.z, charLength);
            if (explicitTolerance == AUTOMATIC_TOLERANCE){
                tolerance = 3* DOUBLE_PREC * (Math.max(Math.abs(max.x), Math.abs(min.x)) + Math.max(Math.abs(max.y), Math.abs(min.y)) + Math.max(Math.abs(max.z), Math.abs(min.z)));
            }else{
                tolerance = explicitTolerance;
            }
        }
    }

    protected void createInitialSimplex() throws IllegalArgumentException{
        double max = 0;
        int imax = 0;

        for (int i = 0; i< 3; i++){
            double diff = maxVtxs[i].pnt.get(i)-minVtxs[i].pnt.get(i);
            if (diff > max){
                max = diff;
                imax = i;
            }
        }

        if (max <= tolerance){
            throw new IllegalArgumentException("Input points appear to be conicident");
        }

        Vertex[] vtx = new Vertex[4];
        //set first two vertices to be those with the greatest one dimensional separation

        vtx[0] = maxVtxs[imax];
        vtx[1] = minVtxs[imax];

        // set third vertex to be the vertex farthest from the line between vtx0 and vtx1
        Vector3D u01 = new Vector3D();
        Vector3D diff02 = new Vector3D();
        Vector3D nrml = new Vector3D();

    }

}
