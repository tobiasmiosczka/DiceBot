package com.github.tobiasmiosczka.dicebot.util;

import java.util.*;
import java.util.function.BinaryOperator;

public class CollectionUtil {

    private static final Random RANDOM = new Random();

    public static BinaryOperator<String> separatedBy(String separator) {
        return (s1, s2) -> s1 + separator + s2;
    }

    public static <K, V> Optional<K> getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet())
            if (Objects.equals(value, entry.getValue()))
                return Optional.of(entry.getKey());
        return Optional.empty();
    }

    public static <T> T randomOf(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static <T> T randomOf(T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    public static <T> List<T> shuffled(Collection<T> list) {
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled, RANDOM);
        return shuffled;
    }
}
