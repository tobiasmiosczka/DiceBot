package com.github.tobiasmiosczka.dicebot.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static emoji4j.EmojiUtils.emojify;

public class Emojis {

    public static final Map<String, String> DEFINED_EMOJIS = Stream.of(
            new SimpleEntry<>("1", ":one:"),
            new SimpleEntry<>("2", ":two:"),
            new SimpleEntry<>("3", ":three:"),
            new SimpleEntry<>("4", ":four:"),
            new SimpleEntry<>("5", ":five:"),
            new SimpleEntry<>("6", ":six:"),
            new SimpleEntry<>("7", ":seven:"),
            new SimpleEntry<>("8", ":eight:"),
            new SimpleEntry<>("9", ":nine:"),
            new SimpleEntry<>("10", ":ten:"),
            new SimpleEntry<>("ja", ":white_check_mark:"),
            new SimpleEntry<>("nein", ":negative_squared_cross_mark:"),
            new SimpleEntry<>("yes", ":white_check_mark:"),
            new SimpleEntry<>("no", ":negative_squared_cross_mark:")
    ).collect(Collectors.toMap(SimpleEntry::getKey, v -> emojify(v.getValue())));

    public static final List<String> DEFAULT_EMOJIS = List.of(
            emojify(":strawberry:"),
            emojify(":pineapple:"),
            emojify(":apple:"),
            emojify(":banana:"),
            emojify(":grapes:"),
            emojify(":watermelon:"),
            emojify(":cherries:"),
            emojify(":tomato:"),
            emojify(":corn:"),
            emojify(":eggplant:"),
            emojify(":peach:"),
            emojify(":mushroom:"),
            emojify(":sushi:"),
            emojify(":rice:"),
            emojify(":tea:"),
            emojify(":pear:"),
            emojify(":chestnut:"),
            emojify(":stew:"),
            emojify(":hamburger:"),
            emojify(":bread:")
    );
}
