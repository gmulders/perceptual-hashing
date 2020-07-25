package org.gertje.perceptualhashing.hash;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Scaler {

    private Scaler() {
    }

    /**
     * Scales an image down to the given width and height and translates the image into the YCbCr colorspace.
     *
     * @param image The image to resize
     * @param scaledWidth The new width
     * @param scaledHeight The new height
     * @return The three YCbCr components
     */
    public static double[][][] scale(BufferedImage image, int scaledWidth, int scaledHeight) {
        double [][][] m = new double[3][scaledHeight][scaledWidth];

        int width = image.getWidth();
        int height = image.getHeight();
        double xScale = width / (double) scaledWidth;
        double yScale = height / (double) scaledHeight;

        double yEnd = 0;
        for (int i = 0; i < scaledHeight; i++) {

            double yStart = yEnd;
            yEnd = (i + 1) * yScale;
            if (yEnd >= height) {
                yEnd = height - 0.000001;
            }
            double xEnd = 0;
            for (int j = 0; j < scaledWidth; j++) {

                double xStart = xEnd;
                xEnd = (j + 1) * xScale;
                if (xEnd >= width) {
                    xEnd = width - 0.000001;
                }

                double sumL = 0;
                double sumCb = 0;
                double sumCr = 0;

                for (int y = (int)yStart; y <= yEnd; y++) {
                    double yPortion = 1.0;
                    if (y == (int)yStart) {
                        yPortion -= yStart - y;
                    }
                    if (y == (int)yEnd) {
                        yPortion -= y + 1 - yEnd;
                    }

                    for (int x = (int)xStart; x <= xEnd; x++) {
                        double xPortion = 1.0;
                        if (x == (int)xStart) {
                            xPortion -= xStart - x;
                        }
                        if (x == (int)xEnd) {
                            xPortion -= x + 1 - xEnd;
                        }

                        Color color = new Color(image.getRGB(x, y));
                        int r = color.getRed();
                        int g = color.getGreen();
                        int b = color.getBlue();

                        double l = 0.299 * r + 0.587 * g + 0.114 * b;
                        double cb = 128 - 0.169 * r - 0.331 * g + 0.500 * b;
                        double cr = 128 + 0.500 * r - 0.419 * g - 0.081 * b;

                        double portionArea = xPortion * yPortion;
                        sumL += l * portionArea;
                        sumCb += cb * portionArea;
                        sumCr += cr * portionArea;
                    }
                }
                double invArea = 1.0 / ((xEnd - xStart) * (yEnd - yStart));
                m[0][i][j] = sumL * invArea - 128;
                m[1][i][j] = sumCb * invArea - 128;
                m[2][i][j] = sumCr * invArea - 128;
            }
        }

        return m;
    }
}
