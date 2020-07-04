package com.github.tobiasmiosczka.dicebot.parsing;

import com.github.tobiasmiosczka.dicebot.model.Dice;
import com.github.tobiasmiosczka.dicebot.model.Roll;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiceNotationParserTest {

    @Test
    void testParseDiceNotation() {
        assertEquals("[1][1][1][1] + 3",    DiceNotationParser.parseDiceNotation("4d1 + 3"));
        assertEquals("[1] - 2",             DiceNotationParser.parseDiceNotation("1d1 - 2"));
        assertEquals("",                    DiceNotationParser.parseDiceNotation(""));
    }

    @Test
    void testParseDice() {
        assertEquals("[1]",             DiceNotationParser.parseDice(1, new Dice(1)));
        assertEquals("[1][1][1]",       DiceNotationParser.parseDice(3, new Dice(1)));
        assertEquals("[1][1][1][1][1]", DiceNotationParser.parseDice(5, new Dice(1)));
    }

    @Test
    void testParseRollNotation() {
        assertEquals("(1)",         DiceNotationParser.parseRollNotation("[1]"));
        assertEquals("(4+6+634)+4", DiceNotationParser.parseRollNotation("[4][6][634]+4"));
        assertEquals("",            DiceNotationParser.parseRollNotation(""));
        assertEquals("(4+6+634)+4", DiceNotationParser.parseRollNotation("[4][6][634]+4"));
    }

    @Test
    void testCalculate() throws InterruptedException, ExecutionException, TimeoutException {
        assertEquals("2",       DiceNotationParser.calculate("1+1",         10, TimeUnit.SECONDS));
        assertEquals("null",    DiceNotationParser.calculate("",            10, TimeUnit.SECONDS));
        assertEquals("4",       DiceNotationParser.calculate("2*2",         10, TimeUnit.SECONDS));
        assertEquals("18",      DiceNotationParser.calculate("(4+5)*2",     10, TimeUnit.SECONDS));
        assertEquals("test",    DiceNotationParser.calculate("\"test\"",    10, TimeUnit.SECONDS));
        assertEquals("true",    DiceNotationParser.calculate("1==1",        10, TimeUnit.SECONDS));
    }

    @Test
    void testCalculateTimeoutException() {
        assertThrows(
                TimeoutException.class,
                () -> DiceNotationParser.calculate("while(true){}", 10, TimeUnit.SECONDS)
        );
    }

    @Test
    void testCalculateScriptException() {
        ExecutionException exception = assertThrows(
                ExecutionException.class,
                () -> DiceNotationParser.calculate("error", 10, TimeUnit.SECONDS)
        );
        assertEquals(ScriptException.class, exception.getCause().getClass());
    }

    @Test
    void testRollToString() {
        assertEquals("[5]", DiceNotationParser.rollToString(new Roll(5, 6)));
        assertEquals("[4]", DiceNotationParser.rollToString(new Roll(4, 6)));
        assertEquals("[3]", DiceNotationParser.rollToString(new Roll(3, 6)));
    }

    @Test
    void testRollsToString() {
        assertEquals("[1][2][3]", DiceNotationParser.rollsToString(
                new Roll(1, 6),
                new Roll(2, 6),
                new Roll(3, 6)));
    }
}