import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;

import java.util.Arrays;

public class main {
    public static void main(String[] args) {

        Loader.load(opencv_java.class);

        //Khoi tạo 1 cam, nhận ảnh tĩnh vào
        Fleet_single_cam cam1 = new Fleet_single_cam("a", "1da36bd1c4aa0bf452bb24.jpg", 0.5, 5, 5, 4, false);

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

        cam1.refine_matrix();
        cam1.initPoint3d();

        //Khoi tạo 1 cam, nhận ảnh tĩnh vào
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

        cam2.refine_matrix();
        cam2.initPoint3d();




//        Point3d[] poly = Arrays.copyOf(cam1.getPoints(), cam1.getPoints().length+cam2.getPoints().length);
//        System.arraycopy(cam2.getPoints(), 0, poly, cam1.getPoints().length, cam2.getPoints().length);
//
//        QuickHull3D hull = new QuickHull3D();
//        hull.build(poly);
//        System.out.println("Vertices:");
//        Point3d[] vertices = hull.getVertices();
//        for (int i = 0; i < vertices.length; i++) {
//            Point3d pnt = vertices[i];
//            System.out.println(pnt.x + " " + pnt.y + " " + pnt.z);
//        }
//
//        System.out.println("Faces:");
//        int[][] faceIndices = hull.getFaces();
//        for (int i = 0; i < vertices.length; i++) {
//            for (int k = 0; k < faceIndices[i].length; k++) {
//                System.out.print(faceIndices[i][k] + " ");
//            }
//            System.out.println("");
//        }


    }
}
