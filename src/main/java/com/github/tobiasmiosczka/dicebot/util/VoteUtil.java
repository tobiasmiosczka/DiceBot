package com.github.tobiasmiosczka.dicebot.util;

import com.github.tobiasmiosczka.dicebot.emoji.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.github.tobiasmiosczka.dicebot.emoji.Emojis.FOOD_EMOJIS;
import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.*;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromFormatted;

public class VoteUtil {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    public static final int MAX_OPTIONS = FOOD_EMOJIS.size();

    public static <T> ScheduledFuture<Set<T>> performVote(MessageChannel channel, List<T> options, Function<T, String> toString, int timeInSeconds) {
        Map<T, Emoji> optionsMap = buildOptionMap(options);
        Message message = sendVoteMessage(channel, optionsMap, toString, timeInSeconds);
        return scheduleVoteEnd(message, optionsMap, toString, timeInSeconds);
    }

    private static <T> Message sendVoteMessage(MessageChannel channel, Map<T, Emoji> optionsMap, Function<T, String> toString, int timeInSeconds) {
        Message message = channel.sendMessageEmbeds(buildVoteMessage(optionsMap, toString, timeInSeconds)).complete();
        optionsMap.forEach((key, value) -> message.addReaction(fromFormatted(value.emoji())).queue());
        return message;
    }

    private static <T> ScheduledFuture<Set<T>> scheduleVoteEnd(Message message, Map<T, Emoji> options, Function<T, String> toString, int timeInSecs) {
        return SCHEDULER.schedule(() -> {
            Message updatedMessage = message.getChannel().retrieveMessageById(message.getIdLong()).complete();
            Map<T, Integer> votes = getVotes(updatedMessage, options);
            Set<T> winners = getWinners(votes);
            MessageEmbed messageEmbed = buildResultMessage(votes, winners, toString, timeInSecs);
            message.editMessageEmbeds(messageEmbed).complete().clearReactions().queue();
            return winners;
        }, timeInSecs, TimeUnit.SECONDS);
    }

    private static Optional<Emoji> emojiFromString(String emoji) {
        return Optional.ofNullable(FOOD_EMOJIS.get(emoji));
    }

    private static <T> Map<T, Emoji> buildOptionMap(List<T> input) {
        Map<T, Emoji> options = new HashMap<>();
        List<Emoji> shuffled = shuffled(FOOD_EMOJIS.values());
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
                .reduce(separatedBy("\n")).orElse("");
        return new EmbedBuilder()
                .setTitle("Vote")
                .addField("Options", optionsString, false)
                .addField("Time to vote", timeInSeconds + " seconds.", false)
                .setColor(0x00CC00)
                .build();
    }

    private static <T> MessageEmbed buildResultMessage(Map<T, Integer> votes, Set<T> winners, Function<T, String> toString, int timeInSeconds) {
        String optionsString = votes.entrySet().stream()
                .map(e -> toString.apply(e.getKey()) + " (" + e.getValue() + ")" )
                .reduce(separatedBy("\n")).orElse("");
        String winnerString = winners.stream()
                .map(toString)
                .reduce(separatedBy(", ")).orElse("");
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
