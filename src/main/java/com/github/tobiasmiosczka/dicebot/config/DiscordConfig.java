package com.github.tobiasmiosczka.dicebot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.separatedBy;

@Configuration
public class DiscordConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordConfig.class);

    private static final  String IS_PLAYING_STRING = "Pen & Paper";

    @Bean
    public JDA jda(DiscordApiProperties properties) throws InterruptedException {
        JDA jda = JDABuilder.createDefault(properties.getApiKey())
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.ZLIB)
                .setActivity(Activity.playing(IS_PLAYING_STRING))
                .build()
                .awaitReady();
        printInfo(jda);
        return jda;
    }

    private void printInfo(JDA jda) {
        LOGGER.info("Bot running on:\n{}", jda.getGuilds().stream()
                .map(g -> "  -" + g.getName())
                .reduce(separatedBy("\n")).orElse(""));
    }
}
