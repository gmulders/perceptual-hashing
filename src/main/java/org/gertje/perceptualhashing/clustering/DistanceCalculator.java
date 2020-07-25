package org.gertje.perceptualhashing.clustering;

public interface DistanceCalculator<T> {

    int distance(T t1, T t2);
}
