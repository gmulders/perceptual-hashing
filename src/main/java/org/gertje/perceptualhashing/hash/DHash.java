package org.gertje.perceptualhashing.hash;

import java.awt.image.BufferedImage;

public class DHash {

    private static final int SCALED_WIDTH = 9;
    private static final int SCALED_HEIGHT = 9;

    private final long part1;
    private final long part2;

    private DHash(long part1, long part2) {
        this.part1 = part1;
        this.part2 = part2;
    }

    public static DHash calculate(BufferedImage image) {
        double [][] m = Scaler.scale(image, SCALED_WIDTH, SCALED_HEIGHT)[0];

        long val1 = 0;
        long val2 = 0;
        for (int i = 0; i < SCALED_HEIGHT - 1; i++) {
            for (int j = 0; j < SCALED_WIDTH - 1; j++) {
                if (m[i][j] > m[i][j + 1]) {
                    val1 |= 1L << (i * 8 + j);
                }
                if (m[i][j] > m[i + 1][j]) {
                    val2 |= 1L << (i * 8 + j);
                }
            }
        }

        return new DHash(val1, val2);
    }

    public static int distance(DHash hash1, DHash hash2) {
        return Long.bitCount(hash1.part1 ^ hash2.part1) + Long.bitCount(hash1.part2 ^ hash2.part2);
    }

    public static DHash fromString(String string) {
        String s1 = string.substring(0, 16);
        String s2 = string.substring(16, 32);

        long part1 = Long.parseUnsignedLong(s1, 16);
        long part2 = Long.parseUnsignedLong(s2, 16);

        return new DHash(part1, part2);
    }

    @Override
    public String toString() {
        return String.format("%016x%016x", part1, part2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DHash hash = (DHash) o;

        if (part1 != hash.part1) return false;
        return part2 == hash.part2;
    }

    @Override
    public int hashCode() {
        int result = (int) (part1 ^ (part1 >>> 32));
        result = 31 * result + (int) (part2 ^ (part2 >>> 32));
        return result;
    }
}