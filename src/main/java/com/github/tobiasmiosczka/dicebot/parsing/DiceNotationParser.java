package com.github.tobiasmiosczka.dicebot.parsing;

import com.github.tobiasmiosczka.dicebot.model.Dice;

import java.util.Arrays;
import java.util.regex.Pattern;

public class DiceNotationParser {

    private static final String[] DICE_SYMBOLS = {"d", "w"};
    private static final String REGEX_DICE = "\\d+[" + Arrays.stream(DICE_SYMBOLS).reduce(String::concat) + "]\\d+";

    private static final Pattern dicePattern = Pattern.compile(REGEX_DICE);

    public static String parseDiceNotation(String input) {
        return dicePattern
                .matcher(input)
                .replaceAll((a) -> parseDice(a.group()));
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
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < quantity; ++i) {
            result.append(dice.roll());
        }
        return result.toString();
    }
}
