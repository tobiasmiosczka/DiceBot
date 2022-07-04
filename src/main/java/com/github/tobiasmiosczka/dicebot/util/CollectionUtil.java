package com.github.tobiasmiosczka.dicebot.util;

import java.util.*;

public class CollectionUtil {

    private static final Random R = new Random();

    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet())
            if (Objects.equals(value, entry.getValue()))
                return entry.getKey();
        return null;
    }

    public static <T> T getRandom(List<T> list) {
        return list.get(R.nextInt(list.size()));
    }

    public static <T> T getRandom(T[] array) {
        return array[R.nextInt(array.length)];
    }

    public static <T> List<T> shuffled(List<T> list) {
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled, R);
        return shuffled;
    }
}
