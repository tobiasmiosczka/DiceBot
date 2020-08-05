package com.github.tobiasmiosczka.dicebot.parsing;

import com.github.tobiasmiosczka.dicebot.model.Dice;
import com.github.tobiasmiosczka.dicebot.model.Roll;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class DiceNotationParser {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static final String[] DICE_SYMBOLS = {"d", "w", "D", "W"};
    private static final String REGEX_DICE = "\\d+[" + Arrays.stream(DICE_SYMBOLS).reduce(String::concat).orElse("") + "]\\d+";
    private static final Pattern DICE_PATTERN = Pattern.compile(REGEX_DICE);

    public static String parseDiceNotation(String input) {
        return DICE_PATTERN
                .matcher(input)
                .replaceAll((a) -> parseDice(a.group()));
    }

    private static String calculate(String input) throws ScriptException {
        return "" + (new ScriptEngineManager().getEngineByName("JavaScript").eval(input));
    }

    public static String calculate(String input, long timeoutNanoSeconds, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return EXECUTOR_SERVICE
                .submit(() -> calculate(input))
                .get(timeoutNanoSeconds, timeUnit);
    }

    public static String parseRollNotation(String input) {
        StringBuilder result = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (c == ']') {
                if (i == input.length() - 1 || input.charAt(i + 1) != '[') {
                    result.append(')');
                } else {
                    result.append('+');
                    ++i;
                }
            } else if (c == '[') {
                result.append('(');
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String parseDice(String input) {
        for (String diceSymbol : DICE_SYMBOLS) {
            if (input.contains(diceSymbol)) {
                String[] s = input.split(diceSymbol);
                return parseDice(Integer.parseInt(s[0]), new Dice(Integer.parseInt(s[1])));
            }
        }
        return input;
    }

    public static String parseDice(int quantity, Dice dice) {
        return rollsToString(dice.roll(quantity));
    }

    public static String rollsToString(Roll...rolls) {
        StringBuilder sb = new StringBuilder();
        for (Roll roll : rolls) {
            sb.append(rollToString(roll));
        }
        return sb.toString();
    }

    public static String rollToString(Roll roll) {
        return "[" + roll.getRoll() + "]";
    }
}