package org.gertje.perceptualhashing.hash;

public class ZHash {

    private static final int ZIG_ZAG_LENGTH = 11;
    private static final int HASH_LENGTH = 64;
    private static final int[] ZIG_ZAG_X = ZigZag.zigZagComponent(ZIG_ZAG_LENGTH, HASH_LENGTH, true);
    private static final int[] ZIG_ZAG_Y = ZigZag.zigZagComponent(ZIG_ZAG_LENGTH, HASH_LENGTH, false);

    private final long hash;

    public ZHash(long hash) {
        this.hash = hash;
    }

    static ZHash calculate(double[][] m) {
        double[] values = new double[HASH_LENGTH];

        double sum = -m[0][0];
        for (int i = 0; i < HASH_LENGTH; i++) {
            values[i] = m[ZIG_ZAG_Y[i]][ZIG_ZAG_X[i]];
            sum += values[i];
        }
        double average = sum / 63.0;

        long hash = 0;
        for (int i = 0; i < HASH_LENGTH; i++) {
            if (values[i] > average) {
                hash |= (1L << (HASH_LENGTH - 1 - i));
            }
        }

        return new ZHash(hash);
    }

    public static int distance(ZHash hash1, ZHash hash2) {
        return Long.bitCount(hash1.hash ^ hash2.hash);
    }

    public static ZHash fromString(String string) {
        long hash = Long.parseUnsignedLong(string, 16);
        return new ZHash(hash);
    }

    @Override
    public String toString() {
        return String.format("%016x", hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZHash zHash = (ZHash) o;

        return hash == zHash.hash;
    }

    @Override
    public int hashCode() {
        return (int) (hash ^ (hash >>> 32));
    }
}
