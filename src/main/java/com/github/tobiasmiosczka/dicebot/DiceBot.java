package com.github.tobiasmiosczka.dicebot;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandEngine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiceBot {

    private static final Logger LOGGER = Logger.getGlobal();
    private static final String COMMANDS_PACKAGE = "com.github.tobiasmiosczka.dicebot.commands";
    private static final  String IS_PLAYING_STRING = "Pen & Paper";

    public DiceBot(String apiKey) throws LoginException, InterruptedException {
        JDA jda = buildJda(apiKey);
        CommandEngine commandEngine = new CommandEngine(jda, COMMANDS_PACKAGE);
        jda.addEventListener(commandEngine);
        LOGGER.log(Level.INFO, "Bot running on:\n" +
                jda.getGuilds().stream()
                        .map(g -> "  -" + g.getName())
                        .reduce("", (s1, s2) -> s1 + "\n" + s2));
    }

    private JDA buildJda(String apiKey) throws LoginException, InterruptedException {
        return JDABuilder.createDefault(apiKey)
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.ZLIB)
                .setActivity(Activity.playing(IS_PLAYING_STRING))
                .build()
                .awaitReady();
    }

}
