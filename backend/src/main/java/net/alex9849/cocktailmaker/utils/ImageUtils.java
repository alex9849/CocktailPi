package net.alex9849.cocktailmaker.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage resizeImage(BufferedImage image, int width, int hight) {
        BufferedImage resized = new BufferedImage(width, hight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.drawImage(image, 0, 0, width, hight, null);
        graphics.dispose();
        return resized;
    }

}
