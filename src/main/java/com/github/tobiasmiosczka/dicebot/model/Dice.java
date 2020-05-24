package com.github.tobiasmiosczka.dicebot.model;

import java.util.Random;

public class Dice {

    private static final Random RANDOM = new Random();

    private final int sides;

    public Dice(int sides) {
        this.sides = sides;
    }

    public Roll roll() {
        return new Roll(getRandomInRange(sides));
    }

    public static int getRandomInRange(int range) {
        return RANDOM.nextInt(range) + 1;
    }

    public Roll[] roll(int count) {
        Roll[] rolls = new Roll[count];
        for (int i = 0; i < count; ++i)
            rolls[i] = roll();
        return rolls;
    }

}
