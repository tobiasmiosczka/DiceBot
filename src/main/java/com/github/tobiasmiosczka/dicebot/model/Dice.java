package com.github.tobiasmiosczka.dicebot.model;

import java.util.Random;

public record Dice(int sides) {

    private static final Random R = new Random();

    public Roll roll() {
        return new Roll(R.nextInt(sides) + 1, sides);
    }

    public Roll[] roll(int count) {
        Roll[] rolls = new Roll[count];
        for (int i = 0; i < count; ++i)
            rolls[i] = roll();
        return rolls;
    }
}
