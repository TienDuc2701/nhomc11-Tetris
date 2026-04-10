package utils;

import java.awt.Color;

/**
 * Lưu các hằng số chung được sử dụng trong toàn bộ game.
 * Giúp dễ thay đổi cấu hình và giữ mã sạch hơn.
 */
public class Constants {
    // Kích thước mỗi ô trong bảng Tetris, tính bằng pixel.
    public static final int TILE_SIZE = 25;
    public static final int COLS = 10;
    public static final int ROWS = 20;

    // Kích thước màn hình
    public static final int SCREEN_WIDTH = COLS * TILE_SIZE;
    public static final int SCREEN_HEIGHT = ROWS * TILE_SIZE;

    // Tốc độ game (ms)
    public static final int INITIAL_SPEED = 250;

    // Màu sắc chủ đạo
    public static final Color BACKGROUND_COLOR = new Color(20, 20, 20);
    public static final Color GRID_COLOR = new Color(45, 45, 45);
}