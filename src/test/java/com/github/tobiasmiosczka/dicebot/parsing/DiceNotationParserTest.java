package com.github.tobiasmiosczka.dicebot.parsing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiceNotationParserTest {

    @Test
    void test() {
        assertEquals("[1][1][1][1] + 3", DiceNotationParser.parseDiceNotation("4d1 + 3"));
        assertEquals("[1] - 2", DiceNotationParser.parseDiceNotation("1d1 - 2"));
        assertEquals("", DiceNotationParser.parseDiceNotation(""));
    }

}