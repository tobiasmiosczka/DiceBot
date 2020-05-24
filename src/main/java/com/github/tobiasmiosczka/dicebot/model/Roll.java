package com.github.tobiasmiosczka.dicebot.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Roll {

    private final int roll;

    public Roll(int roll) {
        this.roll = roll;
    }

    public int getRoll() {
        return this.roll;
    }

    @Override
    public String toString() {
        return "[" + this.roll + "]";
    }

    public static String rollsToString(Roll...rolls) {
        return Arrays.stream(rolls)
                .map(Roll::toString)
                .collect(Collectors.joining());
    }
}
