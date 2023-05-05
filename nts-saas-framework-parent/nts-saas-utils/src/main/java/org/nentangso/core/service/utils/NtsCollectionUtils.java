package org.nentangso.core.service.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class NtsCollectionUtils {
    private NtsCollectionUtils() {
    }

    public static <T> Stream<List<T>> chunks(List<T> source, int length) {
        if (length <= 0)
            throw new IllegalArgumentException("length = " + length);
        int size = source.size();
        if (size <= 0)
            return Stream.empty();
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(
            n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }

    public static List<String> normalize(Collection<?> collection) {
        if (collection == null) {
            return Collections.emptyList();
        }
        return collection.stream()
            .filter(Objects::nonNull)
            .map(String::valueOf)
            .collect(Collectors.toList());
    }

    /**
     * Compares the specified object with this list for equality. Returns true if and only if the specified object is also a list, both lists have the same size, and all corresponding pairs of elements in the two lists are equal. (Two elements e1 and e2 are equal if (e1==null ? e2==null : e1.equals(e2)).) In other words, two lists are defined to be equal if they contain the same elements in the same order. This definition ensures that the equals method works properly across different implementations of the List interface.
     *
     * @param left  left list
     * @param right right list
     * @return boolean
     */
    public static boolean equals(List<?> left, List<?> right) {
        List<?> leftList = Optional.ofNullable(left).orElseGet(Collections::emptyList);
        List<?> rightList = Optional.ofNullable(right).orElseGet(Collections::emptyList);
        return leftList.equals(rightList);
    }

    /**
     * Compare two set, ignore order and duplicated
     *
     * @param left  left set
     * @param right right set
     * @return boolean
     */
    public static boolean equals(Set<?> left, Set<?> right) {
        Set<?> leftSet = Optional.ofNullable(left).orElseGet(Collections::emptySet);
        Set<?> rightSet = Optional.ofNullable(right).orElseGet(Collections::emptySet);
        return leftSet.equals(rightSet);
    }
}
