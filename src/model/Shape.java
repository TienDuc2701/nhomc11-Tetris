package model;

import java.awt.Color;

public enum Shape {
    /**
     * Các hình dạng khối Tetris được định nghĩa bằng tọa độ ô vuông.
     * Mỗi shape có tọa độ tương đối và màu sắc riêng.
     */
    TRIPLE_HORIZONTAL(new int[][]{{0,0}, {1,0}, {2,0}}, Color.CYAN),
    I_SHAPE(new int[][]{{0,0}, {1,0}, {2,0}, {3,0}}, Color.BLUE),
    L_SHAPE(new int[][]{{0,0}, {0,1}, {1,1}, {2,1}}, Color.ORANGE),
    SQUARE(new int[][]{{0,0}, {1,0}, {0,1}, {1,1}}, Color.YELLOW),
    T_SHAPE(new int[][]{{1,0}, {0,1}, {1,1}, {2,1}}, Color.MAGENTA);
    
    
    public int[][] coords;
    public Color color;

    Shape(int[][] coords, Color color) {
        this.coords = coords;
        this.color = color;
    }

    /**
     * Trả về tọa độ mới của khối sau khi xoay 90 độ.
     * Hàm xoay điểm (x, y) thành (-y, x).
     */
    public static int[][] rotate(int[][] currentCoords) {
        int[][] rotated = new int[currentCoords.length][2];
        for (int i = 0; i < currentCoords.length; i++) {
            rotated[i][0] = -currentCoords[i][1];
            rotated[i][1] = currentCoords[i][0];
        }
        return rotated;
    }
}
