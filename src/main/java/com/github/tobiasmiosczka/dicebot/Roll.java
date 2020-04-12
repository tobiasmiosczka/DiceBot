package com.github.tobiasmiosczka.dicebot;

import java.util.Arrays;

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

    public static int sum(Roll...rolls) {
        return Arrays.stream(rolls)
                .map(Roll::getRoll)
                .reduce(Integer::sum)
                .get();
    }
}
