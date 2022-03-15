import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.Arrays;
import java.util.Comparator;

public class Fleet_single_side {
    private Mat image;
    private String side_name;
    private float[][] side_loc; //4-point location on image
    private int num_x; //number of fleet on height
    private int num_y; //number of fleet on width
    private String color; // color of fleet
    private boolean show;
    private Scalar lower_color; // lower color of fleet
    private Scalar upper_color; // upper color of fleet

    public Fleet_single_side(Mat image, String side_name, float[][] side_loc, int num_x, int num_y, String color, boolean show) {
        this.image = image;
        this.side_name = side_name;
        this.side_loc = side_loc;
        this.num_x = num_x;
        this.num_y = num_y;
        this.color = color;
        this.show = show;
        //initialize  lower, u
        if (color == "Yellow") {
            this.lower_color = new Scalar(20, 100, 100);
            this.upper_color = new Scalar(30, 255, 255);
        }
    }

    public Mat four_point_transform() {
        //get the order coordinates
        float[][] rect = order_points(this.side_loc);
        float[] tl = rect[0];
        float[] tr = rect[1];
        float[] br = rect[2];
        float[] bl = rect[3];

//        compute the width of the new image, which will be the
//        maximum distance between bottom-right and bottom-left
//        x-coordiates or the top-right and top-left x-coordinates
        double widthA = Math.sqrt(Math.pow((br[0] - bl[0]), 2) + Math.pow((br[1] - bl[1]), 2));
        double widthB = Math.sqrt(Math.pow((tr[0] - tl[0]), 2) + Math.pow((tr[1] - tl[1]), 2));
        int maxWidth = Math.max((int) widthA, (int) widthB);

//        compute the height of the new image, which will be the
//        maximum distance between the top-right and bottom-right
//        y-coordinates or the top-left and bottom-left y-coordinates
        double heightA = Math.sqrt(Math.pow((tr[0] - br[0]), 2) + Math.pow((tr[1] - br[1]), 2));
        double heightB = Math.sqrt(Math.pow((tl[0] - bl[0]), 2) + Math.pow((tl[1] - bl[1]), 2));
        int maxHeight = Math.max((int) heightA, (int) heightB);

//        now that we have the dimensions of the new image, construct
//        the set of destination points to obtain a "birds eye view",
//        (i.e. top-down view) of the image, again specifying points
//        in the top-left, top-right, bottom-right, and bottom-left
//        order
        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, 0),
                new Point(maxWidth - 1, 0),
                new Point(maxWidth - 1, maxHeight - 1),
                new Point(0, maxHeight - 1)
        );

        //gen src from rect
        MatOfPoint2f src = new MatOfPoint2f(
                new Point(rect[0][0], rect[0][1]),
                new Point(rect[1][0], rect[1][1]),
                new Point(rect[2][0], rect[2][1]),
                new Point(rect[3][0], rect[3][1])
        );

        //compute the perspective transform matrix and then apply it
        Mat M = Imgproc.getPerspectiveTransform(src, dst);
        Mat warped = new Mat();
        Imgproc.warpPerspective(this.image, warped, M, new Size(maxWidth, maxHeight));
        //return the warped image
        return warped;
    }

    public Mat[] filter_color(Mat image) {
        //convert image from bgr to hsv
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        //gen mask from hsv, lower color, upper color
        Mat mask = new Mat();
        Core.inRange(hsv, this.lower_color, this.upper_color, mask);

        //gen bitwise_and from image using mask
        Mat result = new Mat();
        Core.bitwise_and(image, image, result, mask);

        //return result, mask
        return new Mat[]{result, mask};
    }

    public int[][] image2mat(Mat image) {
        int[] shape = new int[]{num_x, num_y};

        //initialize mat
        int[][] mat = new int[num_x][num_y];

        //get h, w
        int w = image.width();
        int h = image.height();

        //cal stride_w, stride_h
        int stride_w = w / shape[1];
        int stride_h = h / shape[0];

        //create mat
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                //cal mat[i][j]
                double sum = 0;
                for (int x = i * stride_h; x < (i + 1) * stride_h; x++) {
                    for (int y = j * stride_w; y < (j + 1) * stride_w; y++) {
                        sum += Arrays.stream(image.get(x, y)).sum();
                    }
                }
                mat[i][j] = (int) Math.floor(sum / 255);

                //draw circle if require show
                if (this.show) {
                    Imgproc.circle(image, new Point(j * stride_w, i * stride_h), 1, new Scalar(255), 1);
                }
            }
        }
        //save image is require
        if (this.show) {
            Imgcodecs.imwrite(this.side_name + ".png", image);
        }
        //cal mean
        double mean = Arrays.stream(mat).flatMapToInt(Arrays::stream).sum() / (num_x * num_y);
        //cal std
        double std = cal_std(mat, mean);

        //map condition
        for (int i = 0; i < num_x; i++) {
            for (int j = 0; j < num_y; j++) {
                if ((mat[i][j] >= (mean - 0.5 * std)) && (mat[i][j] <= (mean + 3.0 * std)) && (mat[i][j]>stride_h*stride_w/16)) {
                    mat[i][j] = 1;
                } else {
                    mat[i][j] = 0;
                }
            }
        }
        return mat;
    }

    public int[][] process() {
        Mat wrap_side = four_point_transform();
        Mat[] result_and_mask = filter_color(wrap_side);
        return image2mat(result_and_mask[1]);
    }

    private double cal_std(int[][] mat, double avg) {
        double mean = 0;
        double size = 0;
        //cal mean = sum((value-avg)^2)
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                mean += Math.pow(mat[i][j] - avg, 2);
                size++;
            }
        }
        return Math.sqrt(mean / size);
    }

    private float[][] order_points(float[][] pts) {
        //initialize empty rect
        float[][] rect = new float[4][];

        float[][] xSorted = pts.clone();
        float[][] ySorted = pts.clone();

        //sort the points base on their x_coordinates
        Arrays.sort(xSorted, new Comparator<float[]>() {
            @Override
            public int compare(float[] point1, float[] point2) {
                return Float.compare(point1[0], point2[0]);
            }
        });

        //sort the points base on their y_coordinates
        Arrays.sort(ySorted, new Comparator<float[]>() {
            @Override
            public int compare(float[] point1, float[] point2) {
                return Float.compare(point1[1], point2[1]);
            }
        });

        //grab the left-most and right-most points from the sorted x-roodinate points
        float[][] leftMost = new float[][]{xSorted[0], xSorted[1]};
        //grab the left-most from the sorted y-roodinate points
        float[] topMost = ySorted[0];

        //get the clockwise sort
        float[][] clockwise_pts = sort_clockwise(pts);

        int idx = 0;
        //find index of topMost
        for (int i=0;i<clockwise_pts.length;i++){
            if (Arrays.equals(clockwise_pts[i], topMost)){
                idx=i;
                break;
            }
        }

        //sort topMost is the first element
        float[][] order_pts = new float[4][2];
        if (idx!=0){
            System.arraycopy(clockwise_pts, idx, order_pts, 0, clockwise_pts.length-idx);
            System.arraycopy(clockwise_pts, 0, order_pts, clockwise_pts.length-idx, idx);
        }else{
            order_pts = clockwise_pts;
        }



        // topMost is top-left
        if (Arrays.stream(leftMost).anyMatch(point -> {
            return Arrays.equals(point, topMost);
        })){
            return order_pts;
        }else{//topMost is top-right
            float[] temp = order_pts[order_pts.length-1];
            System.arraycopy(order_pts,0 , order_pts, 1, order_pts.length-1);
            order_pts[0] = temp;
            return order_pts;
        }

    }

    private float[][] sort_clockwise(float[][] pts){
        float[][] points = pts.clone();
        float sum_x = 0;
        float sum_y = 0;
        //cal sum x, sum y
        for (int i=0;i<points.length;i++){
            sum_x+=points[i][0];
            sum_y+=points[i][1];
        }
        final float centre_x = sum_x / points.length;
        final float centre_y = sum_y / points.length;

        //sort by angle;
        Arrays.sort(points, new Comparator<float[]>() {
            @Override
            public int compare(float[] floats1, float[] floats2) {
                return Double.compare(Math.atan2(floats1[1]-centre_y, floats1[0]-centre_x)
                        , Math.atan2(floats2[1]-centre_y, floats2[0]-centre_x) );
            }
        });

        return points;
    }
}
