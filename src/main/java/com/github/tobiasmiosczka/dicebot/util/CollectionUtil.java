package com.github.tobiasmiosczka.dicebot.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class CollectionUtil {

    private static final Random R = new Random();

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet())
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
}
