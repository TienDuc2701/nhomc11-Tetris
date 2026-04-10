package utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class GameHelper {
    /**
     * Lớp hỗ trợ các hàm chung cho game.
     * Hiện tại dùng để căn giữa chữ trong các phần vẽ GUI.
     */
    
    /**
     * Vẽ chuỗi chữ vào giữa một vùng có kích thước width x height.
     */
    public static void drawCenteredString(Graphics g, String text, int width, int height, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (width - metrics.stringWidth(text)) / 2;
        int y = ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }
}