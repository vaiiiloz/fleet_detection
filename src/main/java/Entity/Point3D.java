package Entity;

public class Point3D extends Vector3D{

    /**
     * Create a Point3D amd initializes it to zero
     */
    public Point3D() {
    }

    /**
     * Creates a Point3D by copy a vector
     * @param v
     */
    public Point3D(Vector3D v) {
        super(v);
    }

    /**
     * Create a Point3D with the supplied element values
     * @param x
     * first element
     * @param y
     * second element
     * @param z
     * third element
     */
    public Point3D(double x, double y, double z) {
        super(x, y, z);
    }
}
