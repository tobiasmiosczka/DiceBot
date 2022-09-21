package com.github.tobiasmiosczka.dicebot.util;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;

public class CollectionUtil {

    public static BinaryOperator<String> separatedBy(String separator) {
        return (s1, s2) -> s1 + separator + s2;
    }

    public static <K, V> Optional<K> getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet())
            if (Objects.equals(value, entry.getValue()))
                return Optional.of(entry.getKey());
        return Optional.empty();
    }
}
