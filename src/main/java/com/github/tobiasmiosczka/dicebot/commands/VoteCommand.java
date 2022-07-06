package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.*;

import static com.github.tobiasmiosczka.dicebot.util.VoteUtil.*;

@Command(
        command = "v",
        description = "Starts a voting.",
        options = {
                @Option(
                        name = "time",
                        type = OptionType.INTEGER,
                        description = "The duration of the voting in seconds."
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
        List<String> options = List.of(event.getOption("options").getAsString().split(" "));
        if (options.size() < 2)
            return event.reply("Define at least two options.");
        if (options.size() > MAX_OPTIONS)
            return event.reply(MAX_OPTIONS + " options should be enough.");
        performVote(event.getMessageChannel(), options, String::toString, timeInSeconds);
        return event.reply("Ok");
    }
}