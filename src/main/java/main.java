import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;

public class main {
    public static void main(String[] args) {

        Loader.load(opencv_java.class);

        //Khoi tạo 1 cam, nhận ảnh tĩnh vào
        Fleet_single_cam cam = new Fleet_single_cam("a", "5b8ab6b219c9d6978fd827.jpg", 0.5, 5, 5, 4, false);

        //create front side, add 4 đỉnh của side front vào
        cam.add_side("front", new float[][]{
                new float[]{687, 8},
                new float[]{1264, 54},
                new float[]{1027, 432},
                new float[]{672, 305}
        });

        //create mặt side, add 4 đỉnh của mặt side vào
        cam.add_side("side", new float[][]{
                new float[]{12, 132},
                new float[]{687, 8},
                new float[]{672, 305},
                new float[]{275, 497}
        });

        //create bottom side, add 4 đỉnh của side bottom vào
        cam.add_side("bottom", new float[][]{
                new float[]{672, 305},
                new float[]{1027, 432},
                new float[]{817, 923},
                new float[]{275, 497}
        });

        cam.refine_matrix();

        //print matrix
        int[][] matrix = cam.side.get("front").getIntArray();
        for (int i = 0; i < matrix.length; i++) {

            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();

        int[][] matrix2 = cam.side.get("side").getIntArray();
        for (int i = 0; i < matrix2.length; i++) {

            for (int j = 0; j < matrix2[0].length; j++) {
                System.out.print(matrix2[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();

        int[][] matrix3 = cam.side.get("bottom").getIntArray();
        for (int i = 0; i < matrix2.length; i++) {

            for (int j = 0; j < matrix3[0].length; j++) {
                System.out.print(matrix3[i][j] + " ");
            }
            System.out.println();
        }

    }
}
