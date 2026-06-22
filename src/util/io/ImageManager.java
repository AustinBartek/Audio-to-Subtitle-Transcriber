package util.io;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageManager {
    public static final BufferedImage MEDIA_IMAGE, TRASH_IMAGE, UP_IMAGE, DOWN_IMAGE;

    static {
        MEDIA_IMAGE = loadImage("/res/mediaFull.png");
        TRASH_IMAGE = loadImage("/res/trash.png");
        UP_IMAGE = loadImage("/res/wordUp.png");
        DOWN_IMAGE = loadImage("/res/wordDown.png");
    }

    public static BufferedImage loadImage(String path) {
        try {
            URL url = ImageManager.class.getResource(path);
            if (url != null) {
                return ImageIO.read(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
