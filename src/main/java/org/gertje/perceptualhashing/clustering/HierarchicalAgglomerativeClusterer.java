package org.gertje.perceptualhashing.clustering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Integer.min;

public class HierarchicalAgglomerativeClusterer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalAgglomerativeClusterer.class);

    public HierarchicalAgglomerativeClusterer(DistanceCalculator<T> distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    private final DistanceCalculator<T> distanceCalculator;

    public List<Set<T>> cluster(List<T> objects, double breakDistance) {
        List<Set<T>> clusters = new ArrayList<>();
        for (T object : objects) {
            clusters.add(Set.of(object));
        }

        int size = objects.size();
        LOGGER.info("Clustering {} items", size);
        boolean[] removed = new boolean[size];
        int[][] distanceMatrix = calculateDistanceMatrix(objects);

        for (int gg = 1; gg < size; gg++) {

            int parallelism = ForkJoinPool.commonPool().getParallelism();
//            int parallelism = 1;
            int subCount = size / parallelism + 1;
            Optional<Pair> smallest = IntStream.range(0, parallelism)
                    .parallel()
                    .mapToObj(index -> {
                        int currentMinDistance = Integer.MAX_VALUE;
                        int first = -1;
                        int second = -1;

                        int start = subCount * index;
                        int end = min(subCount * (index + 1), size);
                        for (int j = start; j < end; j++) {
                            if (removed[j]) {
                                continue;
                            }
                            for (int k = 0; k < size; k++) {
                                if (removed[k] || j == k || distanceMatrix[j][k] >= currentMinDistance) {
                                    continue;
                                }
                                first = j;
                                second = k;
                                currentMinDistance = distanceMatrix[j][k];
                                if (currentMinDistance == 0) { // It doesn't get any smaller
                                    break;
                                }
                            }
                            if (currentMinDistance == 0) { // It doesn't get any smaller
                                break;
                            }
                        }
                        return new Pair(first, second);
                    })
                    .reduce((left, right) ->
                        distanceMatrix[left.first][left.second] < distanceMatrix[right.first][right.second] ? left : right
                    );

            if (smallest.isEmpty()) {
                throw new RuntimeException("Could not determine smallest");
            }

            int first = smallest.get().first;
            int second = smallest.get().second;
            int currentMinDistance = distanceMatrix[first][second];

            if (currentMinDistance >= breakDistance) {
                break;
            }

            for (int j = 0; j < size; j++) {
                if (!removed[j] && j != first && j != second) {
                    int newDistance = Math.max(distanceMatrix[first][j], distanceMatrix[second][j]);
                    distanceMatrix[first][j] = newDistance;
                    distanceMatrix[j][first] = newDistance;
                }
            }

            removed[second] = true;

            clusters.set(first, join(clusters.get(first), clusters.get(second)));
        }

        for (int i = size - 1; i >= 0; i--) {
            if (removed[i]) {
                clusters.remove(i);
            }
        }

        return clusters;
    }

    private Set<T> join(Set<T> left, Set<T> right) {
        Set<T> set = new HashSet<>();
        set.addAll(left);
        set.addAll(right);
        return set;
    }

    private int[][] calculateDistanceMatrix(List<T> objects) {
        int[][] distanceMatrix = new int[objects.size()][objects.size()];
        int[] counts = new int[128];
        for (int i = 0; i < objects.size(); i++) {
            for (int j = 0; j < objects.size(); j++) {
                distanceMatrix[i][j] = distanceCalculator.distance(objects.get(i), objects.get(j));
                counts[distanceMatrix[i][j]]++;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < 128; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(i).append(": ").append(counts[i]);
            }
            sb.append("]");
            LOGGER.debug("Counts: {}", sb);
        }

        return distanceMatrix;
    }

    private static class Pair {
        private final int first;
        private final int second;

        public Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        public int getFirst() {
            return first;
        }

        public int getSecond() {
            return second;
        }
    }

}
