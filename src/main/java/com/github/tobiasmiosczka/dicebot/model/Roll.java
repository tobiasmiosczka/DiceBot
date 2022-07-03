package com.github.tobiasmiosczka.dicebot.model;

import java.util.Optional;

public record Roll(int roll, int sides) {

    public static int sum(Roll... rolls) {
        int sum = 0;
        for (Roll roll : rolls) {
            sum += roll.roll();
        }
        return sum;
    }

    public static Optional<Double> average(Roll... rolls) {
        if (rolls.length == 0)
            return Optional.empty();
        return Optional.of(1D * sum(rolls) / rolls.length);
    }

    public static Optional<Integer> min(Roll... rolls) {
        if (rolls.length == 0)
            return Optional.empty();
        int min = rolls[0].roll;
        for (Roll roll : rolls) {
            if (roll.roll() < min)
                min = roll.roll();
        }
        return Optional.of(min);
    }

    public static Optional<Integer> max(Roll... rolls) {
        if (rolls.length == 0)
            return Optional.empty();
        int max = rolls[0].roll;
        for (Roll roll : rolls) {
            if (roll.roll() > max)
                max = roll.roll();
        }
        return Optional.of(max);
    }
}
