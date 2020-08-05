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

    public DiceBot(String apiKey) throws LoginException {
        JDA jda = JDABuilder.createDefault(apiKey)
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.ZLIB)
                .setActivity(Activity.playing("Pen & Paper"))
                .build();
        jda.addEventListener(new CommandEngine(jda, "!", "com.github.tobiasmiosczka.dicebot.commands"));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "Bot running on:\n" + jda.getGuilds().stream().map(g -> "  -" + g.getName()).reduce((s1, s2) -> s1 + "\n" + s2).orElse(""));
    }

}
