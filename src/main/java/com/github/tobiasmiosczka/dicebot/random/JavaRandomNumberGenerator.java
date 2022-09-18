package com.github.tobiasmiosczka.dicebot.random;

import java.util.Random;

public class JavaRandomNumberGenerator extends RandomNumberGenerator {

    private final Random random;

    public JavaRandomNumberGenerator() {
        random = new Random();
    }

    public JavaRandomNumberGenerator(long seed) {
        random = new Random(seed);
    }

    @Override
    public int nextInteger(int rangeFrom, int rangeToExclusive) {
        return random.nextInt(rangeFrom, rangeToExclusive);
    }

    @Override
    public int nextInteger(int rangeToExclusive) {
        return random.nextInt(rangeToExclusive);
    }
}
