package net.alex9849.cocktailpi.utils;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageUtilsTest {

    @Test
    void resizesAndCropsToSquareAspect() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);

        BufferedImage resized = ImageUtils.resizeImage(image, 100, 1.0);

        assertEquals(100, resized.getWidth());
        assertEquals(100, resized.getHeight());
    }

    @Test
    void respectsOriginalBoundsWhenMaxWidthExceedsSource() {
        BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB);

        BufferedImage resized = ImageUtils.resizeImage(image, 150, 1.5);

        assertEquals(100, resized.getWidth());
        assertEquals(66, resized.getHeight());
    }
}
