input: ảnh của mỗi cam, và 4 điểm/side. Mỗi  cam sẽ có 3 side. 4 điểm của mỗi side là 4 góc của side đó.  Side  là các mặt của khối thể tích cần tính.
output: mỗi mặt trả về một  binary matrix, dùng  để tính diện tích vùng có giá trị 0.
//Khoi tạo 1 cam, nhận ảnh tĩnh vào
        Fleet_single_cam cam = new Fleet_single_cam("a","5b8ab6b219c9d6978fd827.jpg", 0.5, 5, 5, 4,false);
        
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

        //tạo matrix để tính thể tích
        cam.refine_matrix();