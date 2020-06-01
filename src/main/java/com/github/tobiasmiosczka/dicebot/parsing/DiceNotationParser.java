package com.github.tobiasmiosczka.dicebot.parsing;

import com.github.tobiasmiosczka.dicebot.model.Dice;
import com.github.tobiasmiosczka.dicebot.model.Roll;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DiceNotationParser {

    private static final String[] DICE_SYMBOLS = {"d", "w"};
    private static final String REGEX_DICE = "\\d+[" + Arrays.stream(DICE_SYMBOLS).reduce(String::concat) + "]\\d+";
    private static final Pattern dicePattern = Pattern.compile(REGEX_DICE);

    public static String parseDiceNotation(String input) {
        return dicePattern
                .matcher(input)
                .replaceAll((a) -> parseDice(a.group()));
    }

    private static String calculate(String input) throws ScriptException {
        return "" + (new ScriptEngineManager().getEngineByName("JavaScript").eval(input));
    }

    public static String calculate(String input, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return Executors.newCachedThreadPool()
                .submit(() -> calculate(input))
                .get(timeout, TimeUnit.NANOSECONDS);
    }

    public static String parseRollNotation(String input) {
        return input.replaceAll("]\\[", "+")
                .replaceAll("\\[", "(")
                .replaceAll("]", ")");
    }

    private static String parseDice(String input) {
        for (String diceSymbol : DICE_SYMBOLS) {
            if (input.contains(diceSymbol)) {
                String[] strings = input.split(diceSymbol);
                return parseDice(Integer.parseInt(strings[0]), new Dice(Integer.parseInt(strings[1])));
            }
        }
        return input;
    }

    public static String parseDice(int quantity, Dice dice) {
        return rollsToString(dice.roll(quantity));
    }

    public static String rollsToString(Roll...rolls) {
        return Arrays.stream(rolls)
                .map(DiceNotationParser::rollToString)
                .collect(Collectors.joining());
    }

    public static String rollToString(Roll roll) {
        return "[" + roll.getRoll() + "]";
    }
}
