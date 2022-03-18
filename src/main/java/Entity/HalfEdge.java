package Entity;

public class HalfEdge {

    /**
     * The vertex associated with the heat of this half-edge
     */
    protected Vertex vertex;

    /**
     * Triangular face associated with this half-edge
     */
    protected Face face;

    /**
     * Next haft-edge in the triangle
     */
    protected HalfEdge next;

    /**
     * Previous half-edge in the reiangle
     */
    protected HalfEdge prev;

    /**
     * Half-edge associated with the opposite triangle adjacent to this edge
     */
    protected HalfEdge opposite;

    public HalfEdge() {
    }

    /**
     * Constructs a HalfEdge with head vertex and left-hand triangular face
     *
     * @param vertex head vertex
     * @param face   left-hand triangular face
     */
    public HalfEdge(Vertex vertex, Face face) {
        this.vertex = vertex;
        this.face = face;
    }

    /**
     * Gets the triangular face located to the left of this half-edge
     *
     * @return left-hand triangular face
     */
    public Face getFace() {
        return face;
    }

    /**
     * Gets the value of the next edge adjacent (counter-clockwise) to this one within the triangle
     *
     * @return next adjacent edge
     */
    public HalfEdge getNext() {
        return next;
    }

    /**
     * Sets the value of the next edge adjacent (counter-clockwise) to this one within the triangle
     *
     * @param next next adjacent edge
     */
    public void setNext(HalfEdge next) {
        this.next = next;
    }

    /**
     * Gets the value of the previous edge adjacent (clockwise) to this one within the triangle
     *
     * @return previous adjacent edge
     */
    public HalfEdge getPrev() {
        return prev;
    }

    /**
     * Sets the value of the previous edge adjacent (clockwise) to this one within the triangle
     *
     * @param prev previous adjacent edge
     */
    public void setPrev(HalfEdge prev) {
        this.prev = prev;
    }

    /**
     * Returns the half-edge opposite to this half-edge
     *
     * @return opposite half-edge
     */
    public HalfEdge getOpposite() {
        return opposite;
    }

    /**
     * Sets the half-edge opposite to this half-edge
     *
     * @param opposite opposite half-edge
     */
    public void setOpposite(HalfEdge opposite) {
        this.opposite = opposite;
    }

    /**
     * Returns the head vertex associated with this half-edge
     *
     * @return head vertex
     */
    public Vertex head() {
        return vertex;
    }

    /**
     * Returns the tail vertex associate with this half-edge
     *
     * @return
     */
    public Vertex tail() {
        return prev != null ? prev.vertex : null;
    }

    /**
     * Returns the opposite triangular face associate with this half-edge
     *
     * @return opposite triangular face
     */
    public Face oppositeFace() {
        return opposite != null ? opposite.face : null;
    }

    /**
     * Produces a string identifying this half-edge by the point index values of its tail and head vertices
     *
     * @return identifying string
     */
    public String getVertexString() {
        if (tail() != null) {
            return "" + tail().index + "-" + head().index;
        } else {
            return "?-" + head().index;
        }
    }

    /**
     * Returns the length of this half-edge
     *
     * @return half-edge length
     */
    public double length() {
        if (tail() != null) {
            return head().pnt.distance(tail().pnt);
        } else {
            return -1;
        }
    }

    /**
     * Returns the length squared of this half-edge
     *
     * @return half-edge squared length
     */
    public double lengthSquared() {
        if (tail() != null) {
            return head().pnt.distanceSquared(tail().pnt);
        } else {
            return -1;
        }
    }

}
