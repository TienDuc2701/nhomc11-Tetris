package utils;

import javax.sound.sampled.*;
import java.net.URL;

public class ResourceLoader {
    /**
     * Tải và phát tệp âm thanh từ thư mục res.
     * Hàm này dùng AudioSystem để mở luồng âm thanh và phát clip.
     */
    public static void playSound(String fileName) {
        try {
            URL url = ResourceLoader.class.getResource("/res/" + fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}