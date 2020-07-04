package com.github.tobiasmiosczka.dicebot.model;

import java.util.Optional;

public class Roll {

    private final int roll;
    private final int sides;

    public Roll(int roll, int sides) {
        this.roll = roll;
        this.sides = sides;
    }

    public int getRoll() {
        return roll;
    }

    public int getSides() {
        return sides;
    }

    public static int sum(Roll...rolls) {
        int sum = 0;
        for (Roll roll : rolls) {
            sum += roll.getRoll();
        }
        return sum;
    }

    public static Optional<Double> average(Roll...rolls) {
        if (rolls.length == 0)
            return Optional.empty();
        return Optional.of(1D * sum(rolls) / rolls.length);
    }

    public static Optional<Integer> min(Roll...rolls) {
        if (rolls.length == 0)
            return Optional.empty();
        int min = rolls[0].roll;
        for (Roll roll : rolls) {
            if (roll.getRoll() < min)
                min = roll.getRoll();
        }
        return Optional.of(min);
    }

    public static Optional<Integer> max(Roll...rolls) {
        if (rolls.length == 0)
            return Optional.empty();
        int max = rolls[0].roll;
        for (Roll roll : rolls) {
            if (roll.getRoll() > max)
                max = roll.getRoll();
        }
        return Optional.of(max);
    }
}
