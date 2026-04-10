package model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    /**
     * Lớp chứa trạng thái chung của trò chơi.
     * Có thể dùng để tách logic game khỏi phần hiển thị.
     */
    private int score;
    private boolean isPlaying;
    private boolean isPaused;
    private List<Block> fixedBlocks;

    public GameState() {
        this.fixedBlocks = new ArrayList<>();
        this.score = 0;
        this.isPlaying = false;
        this.isPaused = false;
    }

    public void reset() {
        fixedBlocks.clear();
        score = 0;
        isPlaying = false;
        isPaused = false;
    }

    // Các phương thức hỗ trợ logic
    /**
     * Cộng điểm cho kết quả hiện tại.
     */
    public void addScore(int points) { this.score += points; }
    
    public int getScore() { return score; }
    public List<Block> getFixedBlocks() { return fixedBlocks; }
    
    public boolean isPlaying() { return isPlaying; }
    public void setPlaying(boolean playing) { isPlaying = playing; }
    
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }
}