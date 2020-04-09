package com.github.tobiasmiosczka.dicebot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceNotationParser {

    private static final String DICE_SYMBOL = "d";
    private static final String REGEX_DICE = "\\d+" + DICE_SYMBOL + "\\d+";

    final Pattern dicePattern = Pattern.compile(REGEX_DICE);

    public String parseDiceNotation(String input) {
        String result = input;

        //replace dices with rolls
        Matcher matcher = dicePattern.matcher(result);
        result = matcher.replaceAll((a) -> parseDice(a.group()));
        return result;
    }

    public String parseRollNotation(String input) {
        String result = input;

        result = result.replaceAll("]\\[", "+");
        result = result.replaceAll("\\[", "(");
        result = result.replaceAll("]", ")");

        return result;
    }

    private String parseDice(String input) {
        String[] strings = input.split(DICE_SYMBOL);
        return parseDice(Integer.parseInt(strings[0]), new Dice(Integer.parseInt(strings[1])));
    }

    public String parseDice(int quantity, Dice dice) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < quantity; ++i) {
            result.append(dice.roll());
        }
        return result.toString();
    }
}
