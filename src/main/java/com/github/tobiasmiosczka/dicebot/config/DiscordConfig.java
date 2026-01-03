package com.github.tobiasmiosczka.dicebot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {

    private static final  String IS_PLAYING_STRING = "Pen & Paper";

    @Bean
    public JDA jda(DiscordApiProperties properties) throws InterruptedException {
        return JDABuilder.createDefault(properties.getApiKey())
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.ZLIB)
                .setActivity(Activity.playing(IS_PLAYING_STRING))
                .build()
                .awaitReady();
    }
}
