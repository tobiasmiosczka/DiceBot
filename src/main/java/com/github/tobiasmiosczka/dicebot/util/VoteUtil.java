package com.github.tobiasmiosczka.dicebot.util;

import com.github.tobiasmiosczka.dicebot.model.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.getKeyByValue;
import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.shuffled;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromFormatted;

public class VoteUtil {

    public static final Map<String, Emoji> DEFAULT_EMOJIS = Stream.of(
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
    ).collect(Collectors.toMap(e -> e, Emoji::new));

    private static final BinaryOperator<String> LINES_SEPARATED = (s1, s2) -> s1 + "\n" + s2;
    private static final BinaryOperator<String> COMMA_SEPARATED = (s1, s2) -> s1 + ", " + s2;

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    public static <T> Message sendVoteMessage(MessageChannel channel, Map<T, Emoji> options, Function<T, String> toString, int timeInSeconds) {
        Message message = channel.sendMessageEmbeds(buildVoteMessage(options, toString, timeInSeconds)).complete();
        for (Map.Entry<T, Emoji> e : options.entrySet())
            message.addReaction(fromFormatted(e.getValue().emoji())).queue();
        return message;
    }

    public static <T> void scheduleVoteEnd(Message message, Map<T, Emoji> options, Function<T, String> toString, int timeInSecs) {
        SCHEDULER.schedule(() -> {
            Message updatedMessage = message.getChannel().retrieveMessageById(message.getIdLong()).complete();
            MessageEmbed messageEmbed = buildResultMessage(getVotes(updatedMessage, options), toString, timeInSecs);
            message.editMessageEmbeds(messageEmbed).complete().clearReactions().queue();
        }, timeInSecs, TimeUnit.SECONDS);
    }

    private static Optional<Emoji> emojiFromString(String emoji) {
        return Optional.ofNullable(DEFAULT_EMOJIS.get(emoji));
    }

    public static <T> Map<T, Emoji> buildOptions(T[] input) {
        Map<T, Emoji> options = new HashMap<>();
        List<Emoji> shuffled = shuffled(DEFAULT_EMOJIS.values());
        int iterator = 0;
        for (T option : input) {
            Emoji emoji = shuffled.get(iterator++);
            while (options.containsValue(emoji))
                emoji = shuffled.get(iterator++);
            options.put(option, emoji);
        }
        return options;
    }

    private static <T> MessageEmbed buildVoteMessage(Map<T, Emoji> options, Function<T, String> toString, int timeInSeconds) {
        String optionsString = options.entrySet().stream()
                .map(e -> e.getValue().emoji() + " : " + toString.apply(e.getKey()))
                .reduce("", LINES_SEPARATED);
        return new EmbedBuilder()
                .setTitle("Vote")
                .addField("Options", optionsString, false)
                .addField("Time to vote", timeInSeconds + " seconds.", false)
                .setColor(0x00CC00)
                .build();
    }

    private static <T> MessageEmbed buildResultMessage(Map<T, Integer> votes, Function<T, String> toString, int timeInSeconds) {
        String optionsString = votes.entrySet().stream()
                .map(e -> toString.apply(e.getKey()) + " (" + e.getValue() + ")" )
                .reduce("", LINES_SEPARATED);
        String winnerString = getWinners(votes).stream()
                .map(toString)
                .reduce("", COMMA_SEPARATED);
        return new EmbedBuilder()
                .setTitle("Vote (closed)")
                .addField("Options", optionsString, false)
                .addField("Time to vote", timeInSeconds + " seconds.", false)
                .addField("Winner", winnerString, false)
                .build();
    }

    private static <T> Set<T> getWinners(Map<T, Integer> votes) {
        Set<T> winner = new HashSet<>();
        int max = 0;
        for (Map.Entry<T, Integer> e : votes.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                winner.clear();
                winner.add(e.getKey());
            } else if (e.getValue() == max)
                winner.add(e.getKey());
        }
        return winner;
    }

    private static <T> Map<T, Integer> getVotes(Message message, Map<T, Emoji> options) {
        Map<T, Integer> votes = new HashMap<>();
        for (MessageReaction r : message.getReactions())
            emojiFromString(r.getEmoji().getName())
                    .flatMap(emoji -> getKeyByValue(options, emoji))
                    .ifPresent(e -> votes.put(e, r.getCount() - 1));
        return votes;
    }
}
