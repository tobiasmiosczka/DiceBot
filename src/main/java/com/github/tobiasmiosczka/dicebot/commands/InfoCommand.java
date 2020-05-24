package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

@Command(command = "info")
public class InfoCommand implements CommandFunction {
    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        messageChannel
                .sendMessage("I am open source!\nView: https://github.com/tobiasmiosczka/DiceBot")
                .queue();
        return true;
    }
}
