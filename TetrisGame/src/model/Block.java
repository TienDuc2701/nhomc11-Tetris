package model;

import java.awt.Color;

public class Block {
    /**
     * Ô vuông cố định đã rơi xuống bảng.
     * Mỗi Block lưu vị trí x, y và màu sắc.
     */
    private int x, y;
    private Color color;

    public Block(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    // Getter/Setter cho tọa độ và màu sắc.
    public int getX() { return x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public Color getColor() { return color; }
}