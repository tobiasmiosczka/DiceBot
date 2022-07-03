package com.github.tobiasmiosczka.dicebot;

import com.github.tobiasmiosczka.dicebot.discord.DiscordApiTokenUtil;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {

    private static final String BANNER = """
              _____  _          ____        _  \s
             |  __ \\(_)        |  _ \\      | | \s
             | |  | |_  ___ ___| |_) | ___ | |_\s
             | |  | | |/ __/ _ \\  _ < / _ \\| __|
             | |__| | | (_|  __/ |_) | (_) | |_\s
             |_____/|_|\\___\\___|____/ \\___/ \\__|
            """;

    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        System.out.println(BANNER);
        new DiceBot(DiscordApiTokenUtil.getDiscordApiToken(args));
    }
}
