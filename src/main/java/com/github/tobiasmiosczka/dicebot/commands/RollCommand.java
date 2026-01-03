package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.parsing.DiceNotationParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.tobiasmiosczka.dicebot.discord.JdaUtil.quoted;
import static com.github.tobiasmiosczka.dicebot.discord.JdaUtil.underlined;
import static com.github.tobiasmiosczka.dicebot.parsing.DiceNotationParser.*;

@Command(
        command = "r",
        description = "Rolls some dices.",
        options = {
                @Option(name = "roll", type = OptionType.STRING, description = "Definition of the Roll.")
        })
public class RollCommand implements CommandFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollCommand.class);

    private final DiceNotationParser parser;

    public RollCommand(DiceNotationParser parser) {
        this.parser = parser;
    }

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        if (event.getOption("roll") == null) {
            return event.reply("Roll what?");
        }
        String arg = event.getOption("roll", "", OptionMapping::getAsString);
        String rolls = parser.parseDiceNotation(arg);
        String formula = parseRollNotation(rolls);
        String user = event.getUser().getAsMention();
        try {
            String result = calculate(formula, 10, TimeUnit.SECONDS);
            return event.reply(user + ": " + quoted(arg) + "\n" + rolls + " = " + underlined(result));
        } catch (TimeoutException e) {
            return event.reply(user + ": " + quoted(arg) + "\nSorry, this is too complicated for me.");
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn(e.getMessage(), e);
            return event.reply(user + ": " + quoted(arg) + "\nSorry, something went wrong.:thinking:");
        }
    }
}
