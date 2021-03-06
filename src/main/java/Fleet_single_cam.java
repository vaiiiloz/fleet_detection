
import Entity.Point3d;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Fleet_single_cam {
    private String id;
    private Mat image;
    private double resize_ratio;
    private int num_x;
    private int num_y;
    private int num_z;
    private boolean show;
    private Point3d[] points;
    //    private
    HashMap<String, intArray> side;

    public class intArray {
        private int[][] intArray;

        public intArray(int width, int height) {
            this.intArray = new int[width][height];
        }

        public intArray(int[][] intArray) {
            this.intArray = intArray;
        }

        public int[][] getIntArray() {
            return intArray.clone();
        }

        public int getWidth() {
            return intArray[0].length;
        }

        public int getHeight() {
            return intArray.length;
        }

        public void setIntArray(int[][] intArray) {
            this.intArray = intArray;
        }
    }

    public Fleet_single_cam(String id, String image, double resize_ratio, int num_x, int num_y, int num_z, boolean show) {
        this.id = id;
        this.image = Imgcodecs.imread(image);
        this.resize_ratio = resize_ratio;
        this.num_x = num_x;
        this.num_y = num_y;
        this.num_z = num_z;
        this.show = show;
        this.side = new HashMap<>();
        //each camera can view 3 side
        side.put("front", null);
        side.put("side", null);
        side.put("bottom", null);

        //resize image if resize_ratio !=0
        if (resize_ratio != 0) {
            Imgproc.resize(this.image, this.image, new Size(0, 0), resize_ratio, resize_ratio);
        }

        //save image if require
        if (show) {
            Imgcodecs.imwrite("cam_" + id + ".png", this.image);
        }
    }

    public void add_side(String side_name, float[][] side_loc) {
        assert (Arrays.asList(new String[]{"front", "side", "bottom"}).contains(side_name)) : "Wrong side_name";
        //get side_x, side_y
        int side_x = 0;
        int side_y = 0;
        switch (side_name) {
            case "front": {
                side_x = num_z;
                side_y = num_x;
                break;
            }
            case "side": {
                side_x = num_z;
                side_y = num_y;
                break;
            }
            case "bottom": {
                side_x = num_x;
                side_y = num_y;
                break;
            }
        }
        //get side
        if (side_loc == null) {
            side.put(side_name, new intArray(side_x, side_y));
        } else {
            Fleet_single_side sideProcess = new Fleet_single_side(image, side_name, side_loc, side_x, side_y, "Yellow", false);
            side.put(side_name, new intArray(sideProcess.process()));
        }
    }

    private int[][] fill_matrix(int[][] matrix) {
//         Change 0 to 1
//         [[1 0 0 1 1]      [[1 1 1 1 1]
//         [1 1 1 1 1]   ==  [1 1 1 1 1]
//         [1 1 1 0 1]   ==  [1 1 1 0 1]
//         [0 0 0 0 0]]      [0 0 0 0 0]]
        int h = matrix.length;
        int w = matrix[0].length;
        for (int i = h - 1; i > 0; i--) {
            if (w == Arrays.stream(matrix[i]).sum()) {
                for (int j = 0; j < i; j++) {
                    Arrays.fill(matrix[j], 1);
                }
            }
        }
        return matrix;
    }

    public void refine_matrix() {
        //get front, side, bottom
        int[][] front_ = this.side.get("front").getIntArray();
        int[][] side_ = this.side.get("side").getIntArray();
        int[][] bottom_ = this.side.get("bottom").getIntArray();

        int[][] side = padding(side_, num_z, num_y);
        int[][] front = padding(front_, num_z, num_x);
        int[][] bottom = padding(bottom_, num_x, num_y);

        this.side.get("front").setIntArray(fill_matrix(front));
        this.side.get("side").setIntArray(fill_matrix(side));
        this.side.get("bottom").setIntArray(fill_matrix(bottom));
    }

    /**
     * Create list point3d from 3 side
     */
    public void initPoint3d() {
        //init point list
        List<Point3d> point3dList = new ArrayList<>();

        //temp pixel
        int xx = 0;
        int yy = 0;
        int zz = 0;

        //get side front, side, bottom
        int[][] front = this.side.get("front").getIntArray();
        int[][] side = this.side.get("side").getIntArray();
        int[][] bottom = this.side.get("bottom").getIntArray();
        //add point 0 in side front
        zz = num_z + 1;
        yy = num_y + 1;
        for (int i = 0; i < front.length; i++) {
            zz -= 1;
            xx = 0;
            for (int j = 0; j < front[i].length; j++) {
                xx += 1;
                //add if point == 0
                if (front[i][j] == 0) {
                    point3dList.add(new Point3d(xx, yy, zz));
                }
            }
        }

        //add point 0 in side side
        zz = num_z + 1;
        xx = 0;
        for (int i = 0; i < side.length; i++) {
            zz -= 1;
            yy = 0;
            for (int j = 0; j < side[i].length; j++) {
                yy += 1;
                //add if point == 0
                if (side[i][j] == 0) {
                    point3dList.add(new Point3d(xx, yy, zz));
                }
            }
        }

        //add point 0 in side bottom
        zz = 0;
        yy = num_y + 1;
        for (int i = 0; i < bottom.length; i++) {
            yy -= 1;
            xx = 0;
            for (int j = 0; j < bottom[i].length; j++) {
                xx += 1;
                //add if point == 0
                if (bottom[i][j] == 0) {
                    point3dList.add(new Point3d(xx, yy, zz));
                }
            }
        }

        points = point3dList.toArray(new Point3d[point3dList.size()]);
    }

    public Point3d[] getPoints() {
        return points;
    }


    /**
     * Adding 0 into matrix inorder to make the matrix fit the require height and width
     * @param matrix
     * @param require_height
     * @param require_width
     * @return
     */
    private int[][] padding(int[][] matrix, int require_height, int require_width) {
        //get width, height
        int height = 0;
        int width = 0;
        if (matrix != null) {
            height = matrix.length;
            if (matrix[0] != null) {
                width = matrix[0].length;
            }
        }

        //get padding size
        if (height > require_height) {
            require_height = height;
        }

        if (width > require_width) {
            require_width = width;
        }
        int[][] return_matrix = new int[require_height][require_width];

        //copy matrix
        for (int i = 0; i < require_height; i++) {
            //padding
            Arrays.fill(return_matrix[i], 0);
            //copy matrix
            if (i < height) {

                System.arraycopy(matrix[i], 0, return_matrix[i], 0, width);
            }

        }

        return return_matrix;

    }
}
