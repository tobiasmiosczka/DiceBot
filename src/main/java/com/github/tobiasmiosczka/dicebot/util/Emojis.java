package com.github.tobiasmiosczka.dicebot.util;

import emoji4j.EmojiUtils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Emojis {

    public static final Map<String, String> DEFINED_EMOJIS = Stream.of(
            new AbstractMap.SimpleEntry<>("1", ":one:"),
            new AbstractMap.SimpleEntry<>("2", ":two:"),
            new AbstractMap.SimpleEntry<>("3", ":three:"),
            new AbstractMap.SimpleEntry<>("4", ":four:"),
            new AbstractMap.SimpleEntry<>("5", ":five:"),
            new AbstractMap.SimpleEntry<>("6", ":six:"),
            new AbstractMap.SimpleEntry<>("7", ":seven:"),
            new AbstractMap.SimpleEntry<>("8", ":eight:"),
            new AbstractMap.SimpleEntry<>("9", ":nine:"),
            new AbstractMap.SimpleEntry<>("10", ":ten:"),
            new AbstractMap.SimpleEntry<>("ja", ":white_check_mark:"),
            new AbstractMap.SimpleEntry<>("nein", ":negative_squared_cross_mark:"),
            new AbstractMap.SimpleEntry<>("yes", ":white_check_mark:"),
            new AbstractMap.SimpleEntry<>("no", ":negative_squared_cross_mark:")
    ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, v -> EmojiUtils.emojify(v.getValue())));

    public static final List<String> DEFAULT_EMOJIS = List.of(
            EmojiUtils.emojify(":strawberry:"),
            EmojiUtils.emojify(":pineapple:"),
            EmojiUtils.emojify(":apple:"),
            EmojiUtils.emojify(":banana:"),
            EmojiUtils.emojify(":grapes:"),
            EmojiUtils.emojify(":watermelon:"),
            EmojiUtils.emojify(":cherries:"),
            EmojiUtils.emojify(":tomato:"),
            EmojiUtils.emojify(":corn:"),
            EmojiUtils.emojify(":eggplant:"),
            EmojiUtils.emojify(":peach:"),
            EmojiUtils.emojify(":mushroom:"),
            EmojiUtils.emojify(":sushi:"),
            EmojiUtils.emojify(":rice:"),
            EmojiUtils.emojify(":tea:"),
            EmojiUtils.emojify(":pear:"),
            EmojiUtils.emojify(":chestnut:"),
            EmojiUtils.emojify(":stew:"),
            EmojiUtils.emojify(":hamburger:"),
            EmojiUtils.emojify(":bread:")
    );
}
