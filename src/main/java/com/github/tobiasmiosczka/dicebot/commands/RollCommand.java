package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.parsing.DiceNotationParser;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Command(command = "r")
public class RollCommand implements CommandFunction {

    private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (arg == null || arg.equals("")) {
            messageChannel.sendMessage("Roll what?").queue();
            return false;
        }
        try {
            String rolls = DiceNotationParser.parseDiceNotation(arg);
            String formula = DiceNotationParser.parseRollNotation(rolls);
            String result = "" + SCRIPT_ENGINE.eval(formula);

            messageChannel
                    .sendMessage("\n" + author.getAsMention() + ": `" + arg + "`\n" + rolls + " = __" + result + "__")
                    .queue();
            return true;
        } catch (Exception e) {
            messageChannel
                    .sendMessage("\n" + author.getAsMention() + ": `" + arg + "`\n" + "Sorry, something went wrong.:thinking:")
                    .queue();
            e.printStackTrace();
            return false;
        }
    }
}
