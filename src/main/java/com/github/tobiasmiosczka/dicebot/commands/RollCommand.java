package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.parsing.DiceNotationParser;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Command(command = "r", helpText = "Rolls some dices.")
public class RollCommand implements CommandFunction {

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (arg == null || arg.equals("")) {
            messageChannel
                    .sendMessage("Roll what?")
                    .queue();
            return false;
        }
        String rolls = DiceNotationParser.parseDiceNotation(arg);
        String formula = DiceNotationParser.parseRollNotation(rolls);
        try {
            String result = DiceNotationParser.calculate(formula, 10000000000L);
            messageChannel
                    .sendMessage(author.getAsMention() + ": `" + arg + "`\n" + rolls + " = __" + result + "__")
                    .queue();
            return true;
        } catch (TimeoutException e) {
            messageChannel
                    .sendMessage(author.getAsMention() + ": '" + arg + "'\n" + "Sorry, this is too complicated for me.")
                    .queue();
            return false;
        } catch (InterruptedException | ExecutionException e) {
            messageChannel
                    .sendMessage(author.getAsMention() + ": `" + arg + "`\n" + "Sorry, something went wrong.:thinking:")
                    .queue();
            e.printStackTrace();
            return false;
        }
    }
}
