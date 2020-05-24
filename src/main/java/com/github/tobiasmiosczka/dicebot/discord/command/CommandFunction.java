package com.github.tobiasmiosczka.dicebot.discord.command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

@FunctionalInterface
public interface CommandFunction {
    boolean performCommand(String arg, User author, MessageChannel messageChannel);
}
