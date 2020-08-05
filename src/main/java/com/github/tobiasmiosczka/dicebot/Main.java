package com.github.tobiasmiosczka.dicebot;

import com.github.tobiasmiosczka.dicebot.discord.DiscordApiTokenUtil;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {

    private static final String BANNER = "" +
            "  _____  _          ____        _   \n" +
            " |  __ \\(_)        |  _ \\      | |  \n" +
            " | |  | |_  ___ ___| |_) | ___ | |_ \n" +
            " | |  | | |/ __/ _ \\  _ < / _ \\| __|\n" +
            " | |__| | | (_|  __/ |_) | (_) | |_ \n" +
            " |_____/|_|\\___\\___|____/ \\___/ \\__|\n";

    public static void main(String[] args) throws LoginException, IOException {
        System.out.println(BANNER);
        new DiceBot(DiscordApiTokenUtil.getDiscordApiToken());
    }
}
