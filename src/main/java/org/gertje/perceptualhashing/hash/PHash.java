package org.gertje.perceptualhashing.hash;

public class PHash {

    private final long hash;

    private PHash(long hash) {
        this.hash = hash;
    }

    static PHash calculate(double[][] m) {
        double sum = -m[0][0];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sum += m[i][j];
            }
        }
        double average = sum / 63.0;

        long hash = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (m[i][j] > average) {
                    hash |= 1L << (i * 8 + j);
                }
            }
        }

        return new PHash(hash);
    }

    public static int distance(PHash hash1, PHash hash2) {
        return Long.bitCount(hash1.hash ^ hash2.hash);
    }

    public static PHash fromString(String string) {
        long hash = Long.parseUnsignedLong(string, 16);
        return new PHash(hash);
    }

    @Override
    public String toString() {
        return String.format("%016x", hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PHash pHash = (PHash) o;

        return hash == pHash.hash;
    }

    @Override
    public int hashCode() {
        return (int) (hash ^ (hash >>> 32));
    }
}
