package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.model.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

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
        Map<String, Emoji> options = buildOptions(optionsArray);
        Message message = sendVoteMessage(event.getMessageChannel(), options, String::toString, timeInSeconds);
        scheduleVoteEnd(message, options, String::toString, timeInSeconds);
        return event.reply("Ok");
    }

    private static <T> Message sendVoteMessage(MessageChannel channel, Map<T, Emoji> options, Function<T, String> toString, int timeInSeconds) {
        Message message = channel.sendMessageEmbeds(buildVoteMessage(options, toString, timeInSeconds)).complete();
        for (Map.Entry<T, Emoji> e : options.entrySet())
            message.addReaction(fromFormatted(e.getValue().emoji())).queue();
        return message;
    }

    private static <T> void scheduleVoteEnd(Message message, Map<T, Emoji> options, Function<T, String> toString, int timeInSecs) {
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
                .map(e -> e.getKey() + " (" + e.getValue() + ")" )
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