package net.alex9849.cocktailpi.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage resizeImage(BufferedImage image, int maxWidth, double aspectRatio) {
        int outHeight = (int) (maxWidth / aspectRatio);
        int outWidth = maxWidth;
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        //Output image should have a width of maxWidth or smaller
        if(outHeight > image.getHeight()) {
            outHeight = image.getHeight();
            outWidth = (int) (outHeight * aspectRatio);
        }
        if(outWidth > image.getWidth()) {
            outWidth = image.getWidth();
            outHeight = (int) (outWidth / aspectRatio);
        }
        if(image.getWidth() > image.getHeight() * aspectRatio) {
            sourceWidth = (int) (image.getHeight() * aspectRatio);
        } else {
            sourceHeight = (int) (image.getWidth() / aspectRatio);
        }

        BufferedImage resized = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        int sourceImageX1 = (image.getWidth() - sourceWidth) / 2;
        int sourceImageY1 = (image.getHeight() - sourceHeight) / 2;
        int sourceImageX2 = sourceImageX1 + sourceWidth;
        int sourceImageY2 = sourceImageY1 + sourceHeight;
        graphics.drawImage(image, 0, 0, outWidth, outHeight, sourceImageX1,
                sourceImageY1, sourceImageX2, sourceImageY2, null);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.dispose();
        return resized;
    }

}
