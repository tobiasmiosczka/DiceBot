package com.github.tobiasmiosczka.dicebot;

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
}
