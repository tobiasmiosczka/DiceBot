package com.github.tobiasmiosczka.dicebot.model;

import java.util.Random;

public class Dice {

    private static final Random R = new Random();

    private final int sides;

    public Dice(int sides) {
        this.sides = sides;
    }

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
