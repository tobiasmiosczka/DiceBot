package com.github.tobiasmiosczka.dicebot;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandEngine;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;

import javax.security.auth.login.LoginException;

public class DiceBot {

    public DiceBot(String apiKey) throws LoginException {
        CommandEngine commandEngine = new CommandEngine("com.github.tobiasmiosczka.dicebot.commands");
        new JDABuilder(apiKey)
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.NONE)
                .setActivity(Activity.playing("Pen & Paper"))
                .addEventListeners(commandEngine)
                .build();
    }
}
