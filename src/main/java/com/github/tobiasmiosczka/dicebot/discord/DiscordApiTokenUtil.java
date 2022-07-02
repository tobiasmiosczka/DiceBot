package com.github.tobiasmiosczka.dicebot.discord;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class DiscordApiTokenUtil {

    public static String getDiscordApiToken(String[] args) throws IOException {

        if (args.length != 0)
            return args[0];

        URL url = ClassLoader.getSystemClassLoader().getResource("discord.properties");
        if (url == null)
            return "";
        InputStream input = url.openStream();
        Properties prop = new Properties();
        prop.load(input);
        String token = prop.getProperty("api.token");
        input.close();
        return token;
    }

}