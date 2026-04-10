package view;

import model.Block;
import model.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GamePanel là thành phần chính của trò chơi Tetris.
 *
 * Nó quản lý trạng thái bàn chơi, khối đang rơi, khối cố định,
 * xử lý va chạm, vẽ giao diện và nhận điều khiển bàn phím.
 */
public class GamePanel extends JPanel {

    // Kích thước mỗi ô vuông trong bàn chơi, theo pixel.
    private static final int TILE_W = 25;
    private static final int TILE_H = 25;

    // Số cột và số hàng của bảng Tetris.
    private static final int COLS = 10;
    private static final int ROWS = 20;

    // Kích thước phần bảng chơi và phần thông tin bên phải.
    private static final int BOARD_WIDTH = COLS * TILE_W;
    private static final int INFO_WIDTH = 220;
    private static final int PANEL_WIDTH = BOARD_WIDTH + INFO_WIDTH;
    private static final int PANEL_HEIGHT = ROWS * TILE_H;

    // Tốc độ rơi ban đầu của khối (ms).
    private static final int BASE_SPEED = 400;

    // Kiểu và tọa độ của khối hiện tại đang rơi.
    private Shape currentShapeType;
    private int[][] currentShapeCoords;
    private Color currentColor;
    private int currentX;
    private int currentY;

    // Khối tiếp theo sẽ xuất hiện và điểm số, số dòng đã xóa.
    private Shape nextShapeType;
    private int score = 0;
    private int lines = 0;

    // Danh sách các khối đã được đóng băng nằm ở đáy bảng.
    private final List<Block> fixedBlocks = new ArrayList<>();
    private final Random rand = new Random();

    // Bộ hẹn giờ cho vòng lặp game.
    private Timer gameTimer;

    // Trạng thái trò chơi hiện tại: đang chơi hay đang tạm dừng.
    private boolean isPlaying = false;
    private boolean isPaused = false;

    /**
     * Khởi tạo GamePanel và cấu hình giao diện chính.
     * Khởi tạo timer để tự động đẩy khối xuống khi trò chơi đang chạy.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(22, 22, 22));
        setFocusable(true);

        nextShapeType = randomShape();
        initControls();

        gameTimer = new Timer(BASE_SPEED, e -> {
            if (isPlaying && !isPaused) {
                doMoveDown();
                repaint();
            }
        });
    }

    /**
     * Trả về trạng thái đang chơi của trò chơi.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Trả về trạng thái đang tạm dừng của trò chơi.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Bắt đầu trò chơi mới, khởi tạo lại trạng thái và tạo khối đầu tiên.
     */
    public void startGame() {
        resetGame();
        isPlaying = true;
        isPaused = false;
        nextShapeType = randomShape();
        spawnNewBlock();
        gameTimer.start();
    }

    /**
     * Thay đổi trạng thái tạm dừng của trò chơi.
     */

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    /**
     * Di chuyển khối hiện tại sang trái nếu không bị va chạm.
     */
    public void moveLeft() {
        if (!checkCollisionAt(currentX - TILE_W, currentY, currentShapeCoords)) {
            currentX -= TILE_W;
        }
    }

    /**
     * Di chuyển khối hiện tại sang phải nếu không bị va chạm.
     */
    public void moveRight() {
        if (!checkCollisionAt(currentX + TILE_W, currentY, currentShapeCoords)) {
            currentX += TILE_W;
        }
    }

    /**
     * Đẩy khối hiện tại xuống dưới một bước.
     */
    public void moveDown() {
        doMoveDown();
    }

    /**
     * Xoay khối hiện tại nếu không va chạm.
     */
    public void rotateShape() {
        doRotateShape();
    }

    /**
     * Thiết lập điều khiển bàn phím cho GamePanel.
     * Bao gồm: bắt đầu, tạm dừng, tiếp tục, di chuyển và xoay khối.
     */
    private void initControls() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_ENTER && !isPlaying) {
                    startGame();
                } else if (key == KeyEvent.VK_X && isPlaying) {
                    isPaused = true;
                } else if (key == KeyEvent.VK_R && isPlaying) {
                    isPaused = false;
                }

                if (isPlaying && !isPaused) {
                    switch (key) {
                        case KeyEvent.VK_LEFT -> moveLeft();
                        case KeyEvent.VK_RIGHT -> moveRight();
                        case KeyEvent.VK_DOWN -> doMoveDown();
                        case KeyEvent.VK_UP, KeyEvent.VK_SPACE -> doRotateShape();
                    }
                }
                repaint();
            }
        });
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    /**
     * Xoay khối hiện tại theo chiều kim đồng hồ.
     * Khối vuông không cần xoay vì hình dáng không đổi.
     */
    private void doRotateShape() {
        if (currentShapeType == Shape.SQUARE) return;
        int[][] rotated = Shape.rotate(currentShapeCoords);
        if (!checkCollisionAt(currentX, currentY, rotated)) {
            currentShapeCoords = rotated;
        }
    }

    /**
     * Sinh khối mới khi khối trước đó đã đóng băng hoặc khi bắt đầu trò chơi.
     * Khối tiếp theo được lấy từ `nextShapeType` và chọn ngẫu nhiên khối mới tiếp theo.
     */
    private void spawnNewBlock() {
        if (nextShapeType == null) {
            nextShapeType = randomShape();
        }
        currentShapeType = nextShapeType;
        currentShapeCoords = copyCoords(currentShapeType.coords);
        currentColor = currentShapeType.color;
        nextShapeType = randomShape();

        currentX = ((COLS - getShapeWidth(currentShapeCoords)) / 2) * TILE_W;
        currentY = 0;

        // Nếu khối mới chạm vào khối cố định ngay tại vị trí xuất phát thì kết thúc trò chơi.
        if (checkCollisionAt(currentX, currentY, currentShapeCoords)) {
            gameTimer.stop();
            isPlaying = false;
            JOptionPane.showMessageDialog(this, "GAME OVER! Điểm của bạn: " + score);
            resetGame();
        }
    }

    /**
     * Đặt lại trạng thái trò chơi về ban đầu khi chơi lại.
     */
    private void resetGame() {
        fixedBlocks.clear();
        score = 0;
        lines = 0;
        isPlaying = false;
        isPaused = false;
        repaint();
    }

    /**
     * Di chuyển khối xuống một bước nếu không va chạm.
     * Nếu va chạm, cố định khối tại vị trí hiện tại và sinh khối mới.
     */
    private void doMoveDown() {
        if (!checkCollisionAt(currentX, currentY + TILE_H, currentShapeCoords)) {
            currentY += TILE_H;
        } else {
            freezeBlock();
        }
    }

    /**
     * Kiểm tra va chạm của khối với biên, đáy hoặc các khối cố định.
     * Trả về true nếu có va chạm.
     */
    private boolean checkCollisionAt(int nx, int ny, int[][] shape) {
        for (int[] p : shape) {
            int tx = nx + p[0] * TILE_W;
            int ty = ny + p[1] * TILE_H;
            if (tx < 0 || tx >= BOARD_WIDTH || ty >= PANEL_HEIGHT) return true;
            for (Block b : fixedBlocks) {
                if (tx == b.getX() && ty == b.getY()) return true;
            }
        }
        return false;
    }

    /**
     * Chuyển các ô của khối hiện tại thành khối cố định trên bảng.
     */
    private void freezeBlock() {
        for (int[] p : currentShapeCoords) {
            fixedBlocks.add(new Block(currentX + p[0] * TILE_W, currentY + p[1] * TILE_H, currentColor));
        }
        clearFullRows();
        spawnNewBlock();
    }

    /**
     * Xóa dòng đầy toàn bộ và đếm điểm.
     * Các ô phía trên dòng bị xóa sẽ dịch xuống.
     */
    private void clearFullRows() {
        for (int row = 0; row < ROWS; row++) {
            int targetY = row * TILE_H;
            int count = 0;
            for (Block b : fixedBlocks) {
                if (b.getY() == targetY) count++;
            }
            if (count >= COLS) {
                fixedBlocks.removeIf(b -> b.getY() == targetY);
                for (Block b : fixedBlocks) {
                    if (b.getY() < targetY) {
                        b.setY(b.getY() + TILE_H);
                    }
                }
                lines++;
                score += 100;
                row--;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ toàn bộ bảng và phần thông tin bên phải.
        drawBoard(g);
        drawInfoPanel(g);

        // Hiển thị thông báo khi chưa chơi hoặc đang tạm dừng.
        if (!isPlaying) {
            drawOverlay(g, "PRESS ENTER TO START");
        } else if (isPaused) {
            drawOverlay(g, "PAUSED (PRESS R TO RESUME)");
        }
    }

    /**
     * Vẽ phần bảng chơi chính, gồm nền, lưới và các khối đã đóng băng.
     */
    private void drawBoard(Graphics g) {
        g.setColor(new Color(20, 20, 20));
        g.fillRect(0, 0, BOARD_WIDTH, PANEL_HEIGHT);

        g.setColor(new Color(60, 60, 60));
        for (int i = 0; i <= COLS; i++) {
            g.drawLine(i * TILE_W, 0, i * TILE_W, PANEL_HEIGHT);
        }
        for (int i = 0; i <= ROWS; i++) {
            g.drawLine(0, i * TILE_H, BOARD_WIDTH, i * TILE_H);
        }

        for (Block b : fixedBlocks) {
            g.setColor(b.getColor());
            g.fillRoundRect(b.getX() + 1, b.getY() + 1, TILE_W - 2, TILE_H - 2, 5, 5);
        }

        if (isPlaying) {
            g.setColor(currentColor);
            for (int[] p : currentShapeCoords) {
                int dx = currentX + p[0] * TILE_W;
                int dy = currentY + p[1] * TILE_H;
                g.fillRoundRect(dx + 1, dy + 1, TILE_W - 2, TILE_H - 2, 5, 5);
            }
        }
    }

    /**
     * Vẽ phần thông tin bên phải màn hình, gồm điểm số, số dòng và preview khối tiếp theo.
     */
    private void drawInfoPanel(Graphics g) {
        int x = BOARD_WIDTH;
        g.setColor(new Color(35, 35, 35));
        g.fillRect(x, 0, INFO_WIDTH, PANEL_HEIGHT);

        g.setColor(Color.CYAN);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("TETRIS", x + 20, 40);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("SCORE", x + 20, 110);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        g.drawString(String.valueOf(score), x + 20, 145);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("LINES", x + 20, 190);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        g.drawString(String.valueOf(lines), x + 20, 225);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("NEXT", x + 20, 275);

        int previewX = x + 30;
        int previewY = 370;
        int previewSize = 120;
        g.setColor(Color.WHITE);
        g.drawRect(previewX, previewY, previewSize, previewSize);
        g.setColor(new Color(15, 15, 15));
        g.fillRect(previewX + 1, previewY + 1, previewSize - 1, previewSize - 1);
        drawNextShape(g, previewX + 10, previewY + 10, previewSize - 20);

        g.setColor(new Color(100, 100, 100));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Controls: LEFT/RIGHT = Move", x + 10, PANEL_HEIGHT - 70);
        g.drawString("UP/SPACE = Rotate", x + 10, PANEL_HEIGHT - 50);
        g.drawString("DOWN = Speed Up", x + 10, PANEL_HEIGHT - 30);
        g.drawString("ENTER = New Game | X = Pause | R = Resume", x + 10, PANEL_HEIGHT - 10);
    }

    /**
     * Vẽ ô preview khối tiếp theo trong phần thông tin.
     */
    private void drawNextShape(Graphics g, int x, int y, int size) {
        if (nextShapeType == null) return;
        int[][] coords = nextShapeType.coords;
        int blockSize = 24;
        int offsetX = x + size / 2 - (getShapeWidth(coords) * blockSize) / 2;
        int offsetY = y + size / 2 - (getShapeHeight(coords) * blockSize) / 2;
        g.setColor(nextShapeType.color);
        for (int[] p : coords) {
            int dx = offsetX + p[0] * blockSize;
            int dy = offsetY + p[1] * blockSize;
            g.fillRoundRect(dx, dy, blockSize - 4, blockSize - 4, 5, 5);
        }
    }

    /**
     * Chọn ngẫu nhiên một hình dạng khối mới từ danh sách các Shape.
     */
    private Shape randomShape() {
        Shape[] values = Shape.values();
        return values[rand.nextInt(values.length)];
    }

    /**
     * Tính chiều rộng của một khối theo tọa độ.
     */
    private int getShapeWidth(int[][] shape) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        for (int[] p : shape) {
            minX = Math.min(minX, p[0]);
            maxX = Math.max(maxX, p[0]);
        }
        return maxX - minX + 1;
    }

    /**
     * Tính chiều cao của một khối theo tọa độ.
     */
    private int getShapeHeight(int[][] shape) {
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int[] p : shape) {
            minY = Math.min(minY, p[1]);
            maxY = Math.max(maxY, p[1]);
        }
        return maxY - minY + 1;
    }

    /**
     * Sao chép mảng tọa độ khối để tránh sửa trực tiếp dữ liệu gốc.
     */
    private int[][] copyCoords(int[][] coords) {
        int[][] copy = new int[coords.length][2];
        for (int i = 0; i < coords.length; i++) {
            copy[i][0] = coords[i][0];
            copy[i][1] = coords[i][1];
        }
        return copy;
    }

    /**
     * Vẽ lớp phủ màn hình khi trò chơi chờ bắt đầu hoặc đang tạm dừng.
     */
    private void drawOverlay(Graphics g, String text) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        g.drawString(text, x, getHeight() / 2);
    }
}
