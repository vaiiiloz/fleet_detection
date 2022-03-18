package Entity;

public class Vertex {

    /**
     * Spatial point associated with this vertex
     */
    public Point3D pnt;

    /**
     * Back index into an array
     */
    public int index;

    /**
     * List forward link
     */
    public Vertex prev;

    /**
     * List backward link
     */
    public Vertex next;

    /**
     * Current face that this vertex is outside of
     */
    public Face face;

    /**
     * Constructs a vertex and sets its coordinates to 0
     */
    public Vertex() {
    }

    /**
     * Constructs a vertex with the specified coordinates and index
     */
    public Vertex(double x, double y, double z, int idx) {
        pnt = new Point3D(x, y, z);
        index = idx;
    }
}