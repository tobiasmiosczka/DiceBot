package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.util.CollectionUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.Argument;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(
        command = "v",
        description = "Begins a vote.",
        arguments = {
                @Argument(
                        name = "timeInSeconds",
                        description = "The duration of the vote in seconds."
                ),
                @Argument(
                        name = "options",
                        description = "All options separated by a space. Between 2 and 20 options allowed."
                )
        }
)
public class VoteCommand implements CommandFunction {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private static final Map<String, String> DEFINED_EMOJIS = Stream.of(
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
            ).collect(Collectors.toMap(SimpleEntry::getKey, v -> EmojiUtils.emojify(v.getValue())));

    private static final List<String> DEFAULT_EMOJIS = List.of(
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

    private static <T> Map<T, String> getOptions(Set<T> input) {
        Map<T, String> options = new HashMap<>();
        List<String> shuffled = new ArrayList<>(DEFAULT_EMOJIS);
        Collections.shuffle(shuffled);
        int iterator = 0;
        for (T option : input) {
            String emoji;
            if (DEFINED_EMOJIS.containsKey(option.toString())) {
                emoji = DEFINED_EMOJIS.get(option.toString());
            } else if (EmojiUtils.isEmoji(":" + option.toString() + ":") && !options.containsValue(EmojiUtils.emojify(":" + option.toString() + ":"))) {
                emoji = EmojiUtils.emojify(":" + option.toString() + ":");
            } else {
                emoji = shuffled.get(iterator++);
                while (options.containsValue(emoji)) {
                    emoji = shuffled.get(iterator++);
                }
            }
            options.put(option, emoji);
        }
        return options;
    }

    private static <T> MessageEmbed buildMessageEmbedVote(Map<T, String> options, int timeInSeconds) {
        String optionsString = options.entrySet().stream()
                .map(e -> e.getValue() + " : " + e.getKey())
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("");
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Vote")
                .addField("Options", optionsString, false)
                .addField("Time to vote", timeInSeconds + " seconds.", false)
                .setColor(0x00CC00);
        return embedBuilder.build();
    }

    private static <T> MessageEmbed buildMessageEmbedResult(int timeInSeconds, Map<T, Integer> votes) {
        String optionsString = votes.entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue() + ")" )
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("");
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Vote (closed)")
                .addField("Options", optionsString, false)
                .addField("Time to vote", timeInSeconds + " seconds.", false)
                .setColor(0x222222);
        String winnerString = getWinner(votes).stream()
                .map(Objects::toString)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
        embedBuilder.addField("Winner", winnerString, false);
        return embedBuilder.build();
    }

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        List<String> args = Arrays.asList(arg.split(" "));
        int timeInSeconds;
        try {
            timeInSeconds = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            messageChannel
                    .sendMessage("First parameter must be the time to vote in seconds.")
                    .queue();
            return false;
        }
        if (timeInSeconds < 10) {
            messageChannel
                    .sendMessage("Time to vote must be at least 10 seconds.")
                    .queue();
            return false;
        }
        if (timeInSeconds > 60 * 60 * 24) {
            messageChannel
                    .sendMessage("Time to vote must be less than 24 hours.")
                    .queue();
            return false;
        }
        Set<String> optionsSet = new HashSet<>(args.subList(1, args.size()));
        if (optionsSet.size() < 2) {
            messageChannel
                    .sendMessage("Define at least two options.")
                    .queue();
            return false;
        }
        if (optionsSet.size() > DEFAULT_EMOJIS.size()) {
            messageChannel
                    .sendMessage(DEFAULT_EMOJIS.size() + " options should be enough.")
                    .queue();
            return false;
        }
        Map<String, String> options = getOptions(optionsSet);
        Message message = messageChannel
                .sendMessage(buildMessageEmbedVote(options, timeInSeconds))
                .complete();
        for (Map.Entry<String, String> e : options.entrySet()) {
            message.addReaction(e.getValue()).queue();
        }
        scheduleVoteEnd(message, options, timeInSeconds);
        return true;
    }

    private static <T> Set<T> getWinner(Map<T, Integer> votes) {
        Set<T> winner = new HashSet<>();
        int max = 0;
        for (Map.Entry<T, Integer> e : votes.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                winner.clear();
                winner.add(e.getKey());
            } else if (e.getValue() == max) {
                winner.add(e.getKey());
            }
        }
        return winner;
    }

    private static <T> Map<T, Integer> getVotes(List<MessageReaction> messageReactions, Map<T, String> options) {
        Map<T, Integer> votes = new HashMap<>();
        for (MessageReaction r : messageReactions) {
            if (!options.containsValue(r.getReactionEmote().getEmoji()))
                continue;
            votes.put(CollectionUtil.getKeyByValue(options, r.getReactionEmote().getEmoji()), r.getCount() - 1);
        }
        return votes;
    }

    private static <T> void scheduleVoteEnd(Message message, Map<T, String> options, int timeInSecs) {
        SCHEDULER.schedule(() -> {
            Message updatedMessage = message.getChannel().retrieveMessageById(message.getIdLong()).complete();
            Map<T, Integer> votes = getVotes(updatedMessage.getReactions(), options);
            MessageEmbed messageEmbed = buildMessageEmbedResult(timeInSecs, votes);
            message.editMessage(messageEmbed).complete().clearReactions().queue();
        }, timeInSecs, TimeUnit.SECONDS);
    }
}
