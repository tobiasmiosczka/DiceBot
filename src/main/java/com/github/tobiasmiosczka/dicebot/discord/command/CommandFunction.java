package com.github.tobiasmiosczka.dicebot.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

@FunctionalInterface
public interface CommandFunction {
    ReplyCallbackAction performCommand(SlashCommandInteractionEvent event);
}
