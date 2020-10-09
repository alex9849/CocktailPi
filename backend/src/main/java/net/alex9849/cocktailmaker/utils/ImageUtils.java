package net.alex9849.cocktailmaker.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage resizeImage(BufferedImage image, int maxWidth, double aspectRatio) {
        int maxHeight = (int) (maxWidth / aspectRatio);
        int outWidth = Math.min(maxWidth, image.getWidth());
        int outHeight = Math.min(maxHeight, image.getHeight());
        int sourceRatioWidth = image.getWidth();
        int sourceRatioHeight = image.getHeight();
        if(outHeight * aspectRatio > outWidth) {
            outHeight = (int) (outWidth / aspectRatio);
            sourceRatioHeight = (int) (image.getWidth() / aspectRatio);
        } else {
            outWidth = (int) (outHeight * aspectRatio);
            sourceRatioWidth = (int) (image.getHeight() * aspectRatio);
        }
        BufferedImage resized = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        int sourceImageX1 = (image.getWidth() - sourceRatioWidth) / 2;
        int sourceImageY1 = (image.getHeight() - sourceRatioHeight) / 2;
        int sourceImageX2 = sourceImageX1 + sourceRatioWidth;
        int sourceImageY2 = sourceImageY1 + sourceRatioHeight;
        graphics.drawImage(image, 0, 0, outWidth, outHeight, sourceImageX1,
                sourceImageY1, sourceImageX2, sourceImageY2, null);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.dispose();
        return resized;
    }

}
