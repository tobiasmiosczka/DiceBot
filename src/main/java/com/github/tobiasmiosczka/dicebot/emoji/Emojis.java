package com.github.tobiasmiosczka.dicebot.emoji;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Emojis {
    public static final Map<String, Emoji> FOOD_EMOJIS = Stream.of(
            "\uD83C\uDF47",
            "\uD83C\uDF48",
            "\uD83C\uDF49",
            "\uD83C\uDF4A",
            "\uD83C\uDF4B",
            "\uD83C\uDF4C",
            "\uD83C\uDF4D",
            "\uD83C\uDF4E",
            "\uD83C\uDF50",
            "\uD83C\uDF51",
            "\uD83C\uDF52",
            "\uD83C\uDF53",
            "\uD83E\uDD5D",
            "\uD83C\uDF45",
            "\uD83C\uDF46"
    ).collect(Collectors.toMap(Function.identity(), Emoji::new));
}
