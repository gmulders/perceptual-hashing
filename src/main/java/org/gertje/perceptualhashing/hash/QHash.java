package org.gertje.perceptualhashing.hash;

public class QHash {

    private static final int HASH_LENGTH = 64;
    private static final int[] X;
    private static final int[] Y;

    static {
        X = new int[] {
                0, 0, 0, 0, 0, 0, 1, 1,
                1, 1, 0, 1, 3, 1, 4, 3,
                2, 2, 1, 4, 6, 5, 2, 5,
                4, 2, 4, 6, 3, 6, 2, 4,
                5, 7, 5, 3, 2, 3, 4, 7,
                3, 7, 2, 2, 2, 3, 5, 2,
                6, 5, 4, 1, 0, 6, 3, 5,
                0, 8, 1, 7, 8, 7, 8, 4
        };
        Y = new int[] {
                7, 8, 6, 9, 5, 10, 7, 8,
                6, 9, 4, 5, 0, 10, 0, 1,
                7, 6, 4, 1, 4, 4, 8, 3,
                2, 5, 3, 5, 2, 3, 0, 4,
                5, 4, 2, 3, 4, 4, 5, 5,
                5, 3, 3, 1, 9, 6, 1, 2,
                2, 6, 6, 3, 11, 6, 7, 0,
                3, 4, 11, 6, 3, 2, 5, 7 };
    }

    private final long hash;

    private QHash(long hash) {
        this.hash = hash;
    }

    static QHash calculate(double[][] m) {
        double[] values = new double[HASH_LENGTH];

        double sum = 0;
        for (int i = 0; i < HASH_LENGTH; i++) {
            values[i] = m[Y[i]][X[i]];
            sum += values[i];
        }
        double average = sum / 64.0;

        long hash = 0;
        for (int i = 0; i < HASH_LENGTH; i++) {
            if (values[i] > average) {
                hash |= (1L << (HASH_LENGTH - 1 - i));
            }
        }

        return new QHash(hash);
    }

    public static int distance(QHash hash1, QHash hash2) {
        return Long.bitCount(hash1.hash ^ hash2.hash);
    }

    public static QHash fromString(String string) {
        long hash = Long.parseUnsignedLong(string, 16);
        return new QHash(hash);
    }

    @Override
    public String toString() {
        return String.format("%016x", hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QHash pHash = (QHash) o;

        return hash == pHash.hash;
    }

    @Override
    public int hashCode() {
        return (int) (hash ^ (hash >>> 32));
    }
}
