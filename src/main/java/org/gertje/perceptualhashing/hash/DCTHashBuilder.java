package org.gertje.perceptualhashing.hash;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DCTHashBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DCTHashBuilder.class);

    public static final int SCALED_WIDTH = 32;
    public static final int SCALED_HEIGHT = 32;

    private final double [][] l;
    private final double [][] cb;
    private final double [][] cr;

    private DCTHashBuilder(double[][] l, double[][] cb, double[][] cr) {
        this.l = l;
        this.cb = cb;
        this.cr = cr;
    }

    public static DCTHashBuilder builder(BufferedImage image) {
        double[][][] m = Scaler.scale(image, SCALED_WIDTH, SCALED_HEIGHT);

        for (int c = 0; c < 3; c++) {
            for (int i = 0; i < SCALED_HEIGHT; i++) {
                DCTLee.transform(m[c][i]);
            }

            m[c] = transposeMatrix(m[c]);

            for (int i = 0; i < SCALED_WIDTH; i++) {
                DCTLee.transform(m[c][i]);
            }

            m[c] = transposeMatrix(m[c]);
        }

        return new DCTHashBuilder(m[0], m[1], m[2]);
    }

    static double[][] transposeMatrix(double [][] m){
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }

    public DCTHashBuilder log() {
        if (LOGGER.isDebugEnabled()) {
            log(l);
            log(cb);
            log(cr);
        }
        return this;
    }

    private void log(double[][] m) {
        for (int i = 0; i < SCALED_WIDTH; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < SCALED_HEIGHT; j++) {
                builder.append(String.format(" %5.0f", m[j][i]));
            }
            LOGGER.debug(builder.toString());
        }
    }

    public PHash pHash() {
        return PHash.calculate(l);
    }

    public QHash qHash() {
        return QHash.calculate(l);
    }

    public ZHash zHash() {
        return ZHash.calculate(l);
    }

    public CHash cHash() {
        return CHash.calculate(cb, cr);
    }
}
