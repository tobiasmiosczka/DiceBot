package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.util.CollectionUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import emoji4j.EmojiUtils;
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
import java.util.stream.Collectors;

import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.shuffled;
import static com.github.tobiasmiosczka.dicebot.util.Emojis.DEFAULT_EMOJIS;
import static com.github.tobiasmiosczka.dicebot.util.Emojis.DEFINED_EMOJIS;

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

    public VoteCommand() {}

    private static <T> Map<T, String> getOptions(Set<T> input) {
        Map<T, String> options = new HashMap<>();
        List<String> shuffled = shuffled(DEFAULT_EMOJIS);
        int iterator = 0;
        for (T option : input) {
            String emoji;
            if (DEFINED_EMOJIS.containsKey(option.toString())) {
                emoji = DEFINED_EMOJIS.get(option.toString());
            } else if (EmojiUtils.isEmoji(":" + option + ":")
                    && !options.containsValue(EmojiUtils.emojify(":" + option + ":"))) {
                emoji = EmojiUtils.emojify(":" + option + ":");
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
                .reduce("", (s1, s2) -> s1 + "\n" + s2);
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
                .reduce("", (s1, s2) -> s1 + "\n" + s2);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Vote (closed)")
                .addField("Options", optionsString, false)
                .addField("Time to vote", timeInSeconds + " seconds.", false);
        String winnerString = getWinner(votes).stream()
                .map(Objects::toString)
                .reduce("", (s1, s2) -> s1 + ", " + s2);
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
            } else if (e.getValue() == max) {
                winner.add(e.getKey());
            }
        }
        return winner;
    }

    private static <T> Map<T, Integer> getVotes(List<MessageReaction> messageReactions, Map<T, String> options) {
        Map<T, Integer> votes = new HashMap<>();
        for (MessageReaction r : messageReactions) {
            if (!options.containsValue(r.getEmoji()))
                continue;
            votes.put(CollectionUtil.getKeyByValue(options, r.getEmoji().toString()), r.getCount() - 1);
        }
        return votes;
    }

    private static <T> void scheduleVoteEnd(Message message, Map<T, String> options, int timeInSecs) {
        SCHEDULER.schedule(() -> {
            Message updatedMessage = message.getChannel().retrieveMessageById(message.getIdLong()).complete();
            Map<T, Integer> votes = getVotes(updatedMessage.getReactions(), options);
            MessageEmbed messageEmbed = buildMessageEmbedResult(timeInSecs, votes);
            message.editMessageEmbeds(messageEmbed).complete().clearReactions().queue();
        }, timeInSecs, TimeUnit.SECONDS);
    }

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        int time;
        try {
            time = event.getOption("time").getAsInt();
        } catch (NullPointerException e) {
            return event.reply("First parameter must be the time to vote in seconds.");
        }
        if (time < 10)
            return event.reply("Time to vote must be at least 10 seconds.");
        if (time > 60 * 60 * 24)
            return event.reply("Time to vote must be less than 24 hours.");
        Set<String> optionsSet = toOptionsSet(event.getOption("options").getAsString());
        if (optionsSet.size() < 2)
            return event.reply("Define at least two options.");
        if (optionsSet.size() > DEFAULT_EMOJIS.size())
            return event.reply(DEFAULT_EMOJIS.size() + " options should be enough.");
        Map<String, String> options = getOptions(optionsSet);
        Message message = sendMessage(event.getMessageChannel(), options, time);
        scheduleVoteEnd(message, options, time);
        return event.reply("ok");
    }

    private static Set<String> toOptionsSet(String input) {
        return Arrays.stream(input.split(" "))
                .collect(Collectors.toSet());
    }

    private Message sendMessage(MessageChannel messageChannel, Map<String, String> options, int time) {
        Message message = messageChannel.sendMessageEmbeds(buildMessageEmbedVote(options, time)).complete();
        for (Map.Entry<String, String> e : options.entrySet())
            message.addReaction(Emoji.fromUnicode(e.getValue())).queue();
        return message;
    }
}
