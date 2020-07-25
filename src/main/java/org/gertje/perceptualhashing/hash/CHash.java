package org.gertje.perceptualhashing.hash;

public class CHash {

    private static final int ZIG_ZAG_LENGTH = 11;
    private static final int HASH_LENGTH = 16;
    private static final int[] ZIG_ZAG_X = ZigZag.zigZagComponent(ZIG_ZAG_LENGTH, HASH_LENGTH, true);
    private static final int[] ZIG_ZAG_Y = ZigZag.zigZagComponent(ZIG_ZAG_LENGTH, HASH_LENGTH, false);

    private final int hash;

    private CHash(int hash) {
        this.hash = hash;
    }

    static CHash calculate(double[][] cb, double[][] cr) {
        int hash = hash(cb);
        hash = hash << 16;
        hash |= hash(cr);
        return new CHash(hash);
    }

    private static int hash(double[][] m) {
        double[] values = new double[HASH_LENGTH];

        double sum = -m[0][0];
        for (int i = 0; i < HASH_LENGTH; i++) {
            values[i] = m[ZIG_ZAG_Y[i]][ZIG_ZAG_X[i]];
            sum += values[i];
        }
        double average = sum / ((double)HASH_LENGTH - 1);

        int hash = 0;
        for (int i = 0; i < HASH_LENGTH; i++) {
            if (values[i] > average) {
                hash |= (1L << (HASH_LENGTH - 1 - i));
            }
        }
        return hash;
    }

    public static int distance(CHash hash1, CHash hash2) {
        return Integer.bitCount(hash1.hash ^ hash2.hash);
    }

    public static CHash fromString(String string) {
        int hash = Integer.parseUnsignedInt(string, 16);
        return new CHash(hash);
    }

    @Override
    public String toString() {
        return String.format("%08x", hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CHash zHash = (CHash) o;

        return hash == zHash.hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
