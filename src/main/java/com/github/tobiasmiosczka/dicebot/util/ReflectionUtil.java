package com.github.tobiasmiosczka.dicebot.util;

import java.util.Optional;

public class ReflectionUtil {

    public static <T> Optional<? extends T> instantiate(Class<? extends T> tClass) {
        try {
            return Optional.of(tClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
