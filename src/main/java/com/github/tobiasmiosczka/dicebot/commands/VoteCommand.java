package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.model.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.*;

import static com.github.tobiasmiosczka.dicebot.util.VoteUtil.*;

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
}