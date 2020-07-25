package org.gertje.perceptualhashing.hash;

public class ZigZag {

    private ZigZag() {
    }

    public static int[][] zigZag(int n) {
        int[][] m = new int[n][n];

        for (int y = 0; y < n; y++) {
            for (int x = y & 1; x < n - y; x+=2) {
                m[y][x] = triangle(x + y + 1) - x;
            }
            for (int x = (y+1) & 1; x < n - y; x+=2) {
                m[y][x] = triangle(x + y + 1) - y;
            }
        }
        return m;
    }

    public static int[] zigZagComponent(int n, int l, boolean isX) {
        int[][] zigZagMatrix = ZigZag.zigZag(n);
        int[] values = new int[l];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int value = zigZagMatrix[j][i] - 1;
                if (value < 0 || value > l - 1) {
                    continue;
                }
                values[value] = isX ? i : j;
            }
        }
        return values;
    }

    private static int triangle(int n) {
        return (n * (n + 1)) / 2;
    }
}
