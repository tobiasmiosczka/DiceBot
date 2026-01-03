package com.github.tobiasmiosczka.dicebot;

import com.github.tobiasmiosczka.dicebot.discord.DiscordApiTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiceBotApplication implements CommandLineRunner {

    @Value("${discord.api.api-key}") String apiKey;

    private static final String BANNER = """
              _____  _          ____        _  \s
             |  __ \\(_)        |  _ \\      | | \s
             | |  | |_  ___ ___| |_) | ___ | |_\s
             | |  | | |/ __/ _ \\  _ < / _ \\| __|
             | |__| | | (_|  __/ |_) | (_) | |_\s
             |_____/|_|\\___\\___|____/ \\___/ \\__|
            """;

    public static void main(String[] args) {
        SpringApplication.run(DiceBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(BANNER);
        new DiceBot(apiKey);
    }
}
