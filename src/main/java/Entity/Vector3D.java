package Entity;

public class Vector3D {

    static private final double DOUBLE_PREC = 2.2204460492503131e-16;//Precision of a double

    public double x;//first element
    public double y;//second element
    public double z;//third element

    //Creates a 3-vector and initializes its elements to 0
    public Vector3D() {
    }

    /**
     * Creates a 3-vector by copy existing one
     *
     * @param v vector to be copied
     */
    public Vector3D(Vector3D v) {
        set(v);
    }

    /**
     * Creates a 3-vector with the supplied element values
     *
     * @param x first element
     * @param y second element
     * @param z third element
     */
    public Vector3D(double x, double y, double z) {
        set(x, y, z);
    }

    /**
     * Gets a single element of this vector. Element 0, 1, and 2 correspond to x,y and z
     *
     * @param i element index
     * @return element value throws ArrayIndexOutOfBoundsException if i is not in range 0 to 2
     */
    public double get(int i) {
        switch (i) {
            case 0: {
                return x;
            }
            case 1: {
                return y;
            }
            case 2: {
                return z;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(i);
            }
        }
    }

    /**
     * Sets a single element of this vector. Elements 0, 1, and 2 correspond to
     * x, y, and z
     *
     * @param i     element index
     * @param value element value
     * @return element value throws ArrayIndexOutOfBoundsException if i is not in the range 0 to 2
     */
    public void set(int i, double value) {
        switch (i) {
            case 0: {
                x = value;
                break;
            }
            case 1: {
                y = value;
                break;
            }
            case 2: {
                z = value;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(i);
            }
        }
    }

    /**
     * Sets the values of this vector to those of v1
     *
     * @param v1 vector whose values are copied
     */
    public void set(Vector3D v1) {
        this.x = v1.x;
        this.y = v1.y;
        this.z = v1.z;
    }

    /**
     * Sets the values of the vector to the prescribed values
     *
     * @param x value for first element
     * @param y value for second element
     * @param z value for third element
     */
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Sets the elements of this vector to zero
     */
    public void setZero() {
        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Adds vector v1 to v2 and places the result in this vector
     *
     * @param v1 left-hand vector
     * @param v2 right-hand vector
     */
    public void add(Vector3D v1, Vector3D v2) {
        x = v1.x + v2.x;
        y = v1.y + v2.y;
        z = v1.z + v2.z;
    }

    /**
     * Adds this vector to v and places the result in this vector
     *
     * @param v right-hand vector
     */
    public void add(Vector3D v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    /**
     * Subs vector v1 from v2 and places the result in this vector
     *
     * @param v1 left-hand vector
     * @param v2 right-hand vector
     */
    public void sub(Vector3D v1, Vector3D v2) {
        x = v1.x - v2.x;
        y = v1.y - v2.y;
        z = v1.z - v2.z;
    }

    /**
     * Subs this vector from v and places the result in this vector
     *
     * @param v right-hand vector
     */
    public void sub(Vector3D v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    /**
     * Scales the elements of this vector by s
     *
     * @param s scaling factor
     */
    public void scale(double s) {
        x = s * x;
        y = s * y;
        z = s * z;
    }

    /**
     * Scales the elements of vector v by s and place the results in this vector
     *
     * @param s scaling vector
     * @param v vector to be scaled
     */
    public void scale(double s, Vector3D v) {
        x = s * v.x;
        y = s * v.y;
        z = s * v.z;
    }

    /**
     * Returns the 2 norm of this vector. This is the square root of the sum of the squares of the elements
     *
     * @return vector 2 norm
     */
    public double norm() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Retruns the square of the 2 norm of this vector. this is the sum of the squares of the element
     *
     * @return square of the 2 norm
     */
    public double normSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Returns the Euclidean distance between this vector and vector v
     *
     * @param v vector destination
     * @return distance between this vector and v
     */
    public double distance(Vector3D v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Returns the squared of Euclidean distance between this vector and vector v
     *
     * @param v vector destination
     * @return squared distance between this vector and v
     */
    public double distanceSquared(Vector3D v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;

        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Returns the dot product of this vector and v1
     *
     * @param v right-hand vector
     * @return dot product
     */
    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Normalizes this vector in place
     */
    public void normalize() {
        double lenSqr = x * x + y * y + z * z;
        double err = lenSqr - 1;
        if (err > (2 * DOUBLE_PREC) || err < -(2 * DOUBLE_PREC)) {
            double len = Math.sqrt(lenSqr);
            x /= len;
            y /= len;
            z /= len;
        }
    }

    /**
     * Computes the cross product of v1 and v2 and places the result in this vector
     *
     * @param v1 left-hand vector
     * @param v2 right-hand vector
     */
    public void cross(Vector3D v1, Vector3D v2) {
        double tmpx = v1.y * v2.z - v1.z * v2.y;
        double tmpy = v1.z * v2.x - v1.x * v2.z;
        double tmpz = v1.x * v2.y - v1.y - v2.x;

        x = tmpx;
        y = tmpy;
        z = tmpz;
    }

    @Override
    public String toString() {
        return "Vector3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
