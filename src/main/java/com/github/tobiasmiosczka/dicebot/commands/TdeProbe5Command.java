package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.model.Dice;
import com.github.tobiasmiosczka.dicebot.model.Roll;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

@Command(command = "p")
public class TdeProbe5Command implements CommandFunction {

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        Roll[] rolls = new Dice(20).roll(3);

        boolean critical = (
                (rolls[0].getRoll() == 1 && rolls[1].getRoll() == 1) ||
                (rolls[1].getRoll() == 1 && rolls[2].getRoll() == 1) ||
                (rolls[0].getRoll() == 1 && rolls[2].getRoll() == 1));

        boolean miss = (
                (rolls[0].getRoll() == 20 && rolls[1].getRoll() == 20) ||
                (rolls[1].getRoll() == 20 && rolls[2].getRoll() == 20) ||
                (rolls[0].getRoll() == 20 && rolls[2].getRoll() == 20));

        messageChannel
                .sendMessage(
                        author.getAsMention()
                                + ": " + Roll.rollsToString(rolls)
                                + (critical ? " Critical hit!:partying_face: " : "")
                                + (miss ? " Critical miss!:see_no_evil: " : "")).queue();
        return true;
    }
}
