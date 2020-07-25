/*
 * Adjusted version taken from https://www.nayuki.io/res/fast-discrete-cosine-transform-algorithms/FastDctLee.java
 * Adjustments made:
 * - Pre-compute cosines
 * - Added private constructor
 * - Removed unneeded inverse algorithm
 *
 * Fast discrete cosine transform algorithms (Java)
 *
 * Copyright (c) 2017 Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/fast-discrete-cosine-transform-algorithms
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */

package org.gertje.perceptualhashing.hash;

public final class DCTLee {

    // To speed up the computation we can pre-compute the cosines, since they only depend on scale and index.
    private static final int MAX_SCALE = 5; // length 32
    private static final double[][] SCALE_COSINE = new double[MAX_SCALE][];
    static {
        for (int scale = 1; scale <= MAX_SCALE; scale++) {
            int len = 1 << scale;
            int halfLen = 1 << (scale - 1);
            SCALE_COSINE[scale - 1] = new double[halfLen];

            for (int i = 0; i < halfLen; i++) {
                SCALE_COSINE[scale - 1][i] = 1.0 / (Math.cos((i + 0.5) * Math.PI / len) * 2.0);
            }
        }
    }

    private DCTLee() {
        // Not to be instantiated
    }

    /**
     * Computes the unscaled DCT type II on the specified array in place.
     * The array length must be a power of 2.
     * <p>For the formula, see <a href="https://en.wikipedia.org/wiki/Discrete_cosine_transform#DCT-II">
     * Wikipedia: Discrete cosine transform - DCT-II</a>.</p>
     * @param vector the vector of numbers to transform
     * @throws NullPointerException if the array is {@code null}
     */
    public static void transform(double[] vector) {
        int n = vector.length;
        if (Integer.bitCount(n) != 1) {
            throw new IllegalArgumentException("Length must be power of 2");
        }
        int scale = 31 - Integer.numberOfLeadingZeros(n);
        if (scale > MAX_SCALE) {
            throw new IllegalArgumentException("Length must be power of 2 lower than " + (1 << 5) + ".");
        }
        transform(vector, 0, n, new double[n], scale);
    }

    private static void transform(double[] vector, int off, int len, double[] temp, int scale) {
        // Algorithm by Byeong Gi Lee, 1984. For details, see:
        // See: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.118.3056&rep=rep1&type=pdf#page=34
        if (len == 1)
            return;
        int halfLen = len / 2;
        scale--;
        for (int i = 0; i < halfLen; i++) {
            double x = vector[off + i];
            double y = vector[off + len - 1 - i];
            temp[off + i] = x + y;
            temp[off + i + halfLen] = (x - y) * SCALE_COSINE[scale][i];
        }
        transform(temp, off, halfLen, vector, scale);
        transform(temp, off + halfLen, halfLen, vector, scale);
        for (int i = 0; i < halfLen - 1; i++) {
            vector[off + i * 2] = temp[off + i];
            vector[off + i * 2 + 1] = temp[off + i + halfLen] + temp[off + i + halfLen + 1];
        }
        vector[off + len - 2] = temp[off + halfLen - 1];
        vector[off + len - 1] = temp[off + len - 1];
    }
}
