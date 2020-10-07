package net.alex9849.cocktailmaker.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        double newAspectRatio = width / (double) height;
        int sourceRatioWidth = image.getWidth();
        int sourceRatioHeight = image.getHeight();
        if(image.getHeight() * newAspectRatio > image.getWidth()) {
            sourceRatioHeight = (int) (image.getWidth() / newAspectRatio);
        } else {
            sourceRatioWidth = (int) (image.getHeight() * newAspectRatio);
        }
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        int sourceImageX1 = (image.getWidth() - sourceRatioWidth) / 2;
        int sourceImageY1 = (image.getHeight() - sourceRatioHeight) / 2;
        int sourceImageX2 = sourceImageX1 + sourceRatioWidth;
        int sourceImageY2 = sourceImageY1 + sourceRatioHeight;
        graphics.drawImage(image, 0, 0, width, height, sourceImageX1,
                sourceImageY1, sourceImageX2, sourceImageY2, null);
        graphics.dispose();
        return resized;
    }

}
