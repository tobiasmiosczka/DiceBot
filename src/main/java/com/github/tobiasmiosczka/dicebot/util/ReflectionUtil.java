package com.github.tobiasmiosczka.dicebot.util;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class ReflectionUtil {

    public static <T> Optional<? extends T> instantiate(Class<? extends T> tClass) {
        try {
            return Optional.of(tClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T extends Annotation> Optional<T> getAnnotation(Class<?> c, Class<T> annotationClass) {
        return Optional.ofNullable(c.getAnnotation(annotationClass));
    }
}
