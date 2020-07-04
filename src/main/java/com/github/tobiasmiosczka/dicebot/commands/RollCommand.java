package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Argument;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.parsing.DiceNotationParser;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Command(
        command = "r",
        description = "Rolls some dices.",
        arguments = {
                @Argument(
                        name = "rollDefinition",
                        description = "Definition of the Roll."
                )
        }
)
public class RollCommand implements CommandFunction {

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (arg == null || arg.isEmpty()) {
            messageChannel
                    .sendMessage("Roll what?")
                    .queue();
            return false;
        }
        String rolls = DiceNotationParser.parseDiceNotation(arg);
        String formula = DiceNotationParser.parseRollNotation(rolls);
        try {
            String result = DiceNotationParser.calculate(formula, 10, TimeUnit.SECONDS);
            messageChannel
                    .sendMessage(
                            author.getAsMention() + ": " + JdaUtil.quoted(arg)
                                    + "\n" + rolls + " = " + JdaUtil.underlined(result)
                    )
                    .queue();
            return true;
        } catch (TimeoutException e) {
            messageChannel
                    .sendMessage(
                            author.getAsMention() + ": " + JdaUtil.quoted(arg)
                                    + "\nSorry, this is too complicated for me."
                    )
                    .queue();
            return false;
        } catch (InterruptedException | ExecutionException e) {
            messageChannel
                    .sendMessage(
                            author.getAsMention() + ": " + JdaUtil.quoted(arg)
                                    + "\nSorry, something went wrong.:thinking:"
                    )
                    .queue();
            e.printStackTrace();
            return false;
        }
    }
}
