package com.github.tobiasmiosczka.dicebot.util;

import com.github.tobiasmiosczka.dicebot.model.Roll;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class RollUtil {

    public static int sum(Roll... rolls) {
        return Arrays.stream(rolls).mapToInt(Roll::roll).sum();
    }

    public static OptionalDouble avg(Roll... rolls) {
        return Arrays.stream(rolls).mapToInt(Roll::roll).average();
    }

    public static OptionalInt min(Roll... rolls) {
        return Arrays.stream(rolls).mapToInt(Roll::roll).min();
    }

    public static OptionalInt max(Roll... rolls) {
        return Arrays.stream(rolls).mapToInt(Roll::roll).max();
    }
}
