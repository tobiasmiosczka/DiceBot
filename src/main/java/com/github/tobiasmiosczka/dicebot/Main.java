package com.github.tobiasmiosczka.dicebot;

import com.github.tobiasmiosczka.dicebot.discord.DiscordApiTokenUtil;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws LoginException, IOException {
        new DiceBot(DiscordApiTokenUtil.getDiscordApiToken());
    }
}
