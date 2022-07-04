package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.getKeyByValue;
import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.shuffled;

@Command(
        command = "v",
        description = "Begins a vote.",
        arguments = {
                @Option(
                        name = "time",
                        type = OptionType.INTEGER,
                        description = "The duration of the vote in seconds."
                ),
                @Option(
                        name = "options",
                        type = OptionType.STRING,
                        description = "All options separated by a space. Between 2 and 20 options allowed."
                )
        }
)
public class VoteCommand implements CommandFunction {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private record EmojiConstant(String emoji) {}


    private static Map<String, EmojiConstant> DEFAULT_EMOJIS = Stream.of(
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
    ).collect(Collectors.toMap(e -> e, EmojiConstant::new));

    private static Optional<EmojiConstant> emojiFromString(String emoji) {
        EmojiConstant result = DEFAULT_EMOJIS.get(emoji);
        if (result == null)
            return Optional.empty();
        return Optional.of(result);
    }

    private static <T> Map<T, EmojiConstant> buildOptions(T[] input) {
        Map<T, EmojiConstant> options = new HashMap<>();
        List<EmojiConstant> shuffled = shuffled(DEFAULT_EMOJIS.values());
        int iterator = 0;
        for (T option : input) {
            EmojiConstant emoji = shuffled.get(iterator++);
            while (options.containsValue(emoji))
                emoji = shuffled.get(iterator++);
            options.put(option, emoji);
        }
        return options;
    }

    private static <T> MessageEmbed buildMessageEmbedVote(Map<T, EmojiConstant> options, Function<T, String> toString, int timeInSeconds) {
        String optionsString = options.entrySet().stream()
                .map(e -> e.getValue().emoji() + " : " + toString.apply(e.getKey()))
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
                .addField("Time to vote", timeInSeconds + " seconds.", false);
        String winnerString = getWinner(votes).stream()
                .map(Objects::toString)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
        embedBuilder.addField("Winner", winnerString, false);
        return embedBuilder.build();
    }

    private static <T> Set<T> getWinner(Map<T, Integer> votes) {
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

    private static <T> Map<T, Integer> getVotes(List<MessageReaction> messageReactions, Map<T, EmojiConstant> options) {
        Map<T, Integer> votes = new HashMap<>();
        for (MessageReaction r : messageReactions)
            emojiFromString(r.getEmoji().getName())
                    .flatMap(emoji -> getKeyByValue(options, emoji))
                    .ifPresent(e -> votes.put(e, r.getCount() - 1));
        return votes;
    }

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        int timeInSeconds = event.getOption("time").getAsInt();
        if (timeInSeconds < 10)
            return event.reply("Time to vote must be at least 10 seconds.");
        if (timeInSeconds > 60 * 60 * 24)
            return event.reply("Time to vote must be less than 24 hours.");
        String[] optionsArray = event.getOption("options").getAsString().split(" ");
        if (optionsArray.length < 2)
            return event.reply("Define at least two options.");
        if (optionsArray.length > DEFAULT_EMOJIS.size())
            return event.reply(DEFAULT_EMOJIS.size() + " options should be enough.");
        Map<String, EmojiConstant> options = buildOptions(optionsArray);
        Message message = sendVoteMessage(event.getMessageChannel(), options, String::toString, timeInSeconds);
        scheduleVoteEnd(message, options, timeInSeconds);
        return event.reply("Ok");
    }

    private static <T> Message sendVoteMessage(MessageChannel messageChannel, Map<T, EmojiConstant> options, Function<T, String> toString, int timeInSeconds) {
        Message message = messageChannel.sendMessageEmbeds(buildMessageEmbedVote(options, toString, timeInSeconds)).complete();
        for (Map.Entry<T, EmojiConstant> e : options.entrySet())
            message.addReaction(net.dv8tion.jda.api.entities.emoji.Emoji.fromFormatted(e.getValue().emoji())).queue();
        return message;
    }

    private static <T> void scheduleVoteEnd(Message message, Map<T, EmojiConstant> options, int timeInSecs) {
        SCHEDULER.schedule(() -> {
            Message updatedMessage = message.getChannel().retrieveMessageById(message.getIdLong()).complete();
            Map<T, Integer> votes = getVotes(updatedMessage.getReactions(), options);
            MessageEmbed messageEmbed = buildMessageEmbedResult(timeInSecs, votes);
            message.editMessageEmbeds(messageEmbed).complete().clearReactions().queue();
        }, timeInSecs, TimeUnit.SECONDS);
    }
}