package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.parsing.DiceNotationParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Command(
        command = "r",
        description = "Rolls some dices.",
        arguments = {
                @Option(
                        name = "roll",
                        type = OptionType.STRING,
                        description = "Definition of the Roll.")})
public class RollCommand implements CommandFunction {

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        if (event.getOption("roll") == null)
            return event.reply("Roll what?");
        String arg = event.getOption("roll").getAsString();
        String rolls = DiceNotationParser.parseDiceNotation(arg);
        String formula = DiceNotationParser.parseRollNotation(rolls);
        try {
            String result = DiceNotationParser.calculate(formula, 10, TimeUnit.SECONDS);
            return event.reply(event.getUser().getAsMention() + ": " + JdaUtil.quoted(arg) + "\n" + rolls + " = " + JdaUtil.underlined(result));
        } catch (TimeoutException e) {
            return event.reply(event.getUser().getAsMention() + ": " + JdaUtil.quoted(arg) + "\nSorry, this is too complicated for me.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return event.reply(event.getUser().getAsMention() + ": " + JdaUtil.quoted(arg) + "\nSorry, something went wrong.:thinking:");
        }
    }
}
