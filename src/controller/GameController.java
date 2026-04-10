package controller;

import view.GamePanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * GameController đóng vai trò điều hướng hành động từ người dùng 
 * và cập nhật trạng thái của GamePanel.
 */
public class GameController extends KeyAdapter {
    /**
     * GameController điều khiển hành vi của trò chơi dựa trên phím nhấn.
     */
    private final GamePanel view;

    public GameController(GamePanel view) {
        this.view = view;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // 1. Xử lý trạng thái Game (Bắt đầu, Tạm dừng, Tiếp tục)
        handleGameState(key);

        // 2. Xử lý di chuyển (Chỉ thực hiện khi đang trong trận và không tạm dừng)
        if (view.isPlaying() && !view.isPaused()) {
            handleMovement(key);
        }

        // Cập nhật lại giao diện sau mỗi lần nhấn phím
        view.repaint();
    }

    /**
     * Xử lý phím thay đổi trạng thái trò chơi.
     * ENTER: bắt đầu mới, X: tạm dừng, R: tiếp tục.
     */
    private void handleGameState(int key) {
        switch (key) {
            case KeyEvent.VK_ENTER -> {
                if (!view.isPlaying()) {
                    view.startGame();
                }
            }
            case KeyEvent.VK_X -> {
                if (view.isPlaying()) view.setPaused(true);
            }
            case KeyEvent.VK_R -> {
                if (view.isPlaying()) view.setPaused(false);
            }
        }
    }

    /**
     * Xử lý các phím di chuyển khối khi trò chơi đang chơi.
     * Trái/Phải: di chuyển ngang, Xuống: rơi nhanh, Lên/Space: xoay khối.
     */
    private void handleMovement(int key) {
        switch (key) {
            case KeyEvent.VK_LEFT -> view.moveLeft();
            case KeyEvent.VK_RIGHT -> view.moveRight();
            case KeyEvent.VK_DOWN -> view.moveDown();
            case KeyEvent.VK_UP, KeyEvent.VK_SPACE -> view.rotateShape();
        }
    }
}