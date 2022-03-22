
import Entity.Point3d;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import utils.QuickHull3D;

import java.util.Arrays;

public class main {
    public static void main(String[] args) {

        Loader.load(opencv_java.class);

        int x = 5;
        int y = 5;
        int z = 4;
        //Khoi tạo cam 1
        Fleet_single_cam cam1 = new Fleet_single_cam("1", "1da36bd1c4aa0bf452bb24.jpg", 0.5, x, y, z, false);

        //create front side, add 4 đỉnh của side front vào
        cam1.add_side("front", new float[][]{
                new float[]{681, 54},
                new float[]{1250, 115},
                new float[]{984, 528},
                new float[]{679, 366}
        });

        //create mặt side, add 4 đỉnh của mặt side vào
        cam1.add_side("side", new float[][]{
                new float[]{8, 152},
                new float[]{681, 54},
                new float[]{679, 366},
                new float[]{288, 545}
        });

        //create bottom side, add 4 đỉnh của side bottom vào
        cam1.add_side("bottom", new float[][]{
                new float[]{679, 366},
                new float[]{984, 528},
                new float[]{732, 955},
                new float[]{288, 545}
        });

        //tính matrix thể tích vật
        cam1.refine_matrix();
        //lấy những point chứa vật
        cam1.initPoint3d();

        //Khoi tạo cam2
        Fleet_single_cam cam2 = new Fleet_single_cam("a", "5b8ab6b219c9d6978fd827.jpg", 0.5, 5, 5, 4, false);

        //create front side, add 4 đỉnh của side front vào
        cam2.add_side("front", new float[][]{
                new float[]{687, 8},
                new float[]{1264, 54},
                new float[]{1027, 432},
                new float[]{672, 305}
        });

        //create mặt side, add 4 đỉnh của mặt side vào
        cam2.add_side("side", new float[][]{
                new float[]{12, 132},
                new float[]{687, 8},
                new float[]{672, 305},
                new float[]{275, 497}
        });

        //create bottom side, add 4 đỉnh của side bottom vào
        cam2.add_side("bottom", new float[][]{
                new float[]{672, 305},
                new float[]{1027, 432},
                new float[]{817, 923},
                new float[]{275, 497}
        });

        //tính matrix thể tích vật
        cam2.refine_matrix();
        //lấy những point của vật thể
        cam2.initPoint3d();


        // tổng hợp lại những point chứa vật trong 2 cam
        Point3d[] poly = Arrays.copyOf(cam1.getPoints(), cam1.getPoints().length+cam2.getPoints().length);
        System.arraycopy(cam2.getPoints(), 0, poly, cam1.getPoints().length, cam2.getPoints().length);

        //dùng quick hull algorithm để tính thể tích
        QuickHull3D hull = new QuickHull3D();
        hull.build(poly);

        //lấy thể tích của vật
        double volume = hull.calVolume();


        //Kết quả trả ra: tỉ lệ thể tích
        double result = volume/((x+1)*(y+1)*(z+1));


    }
}
