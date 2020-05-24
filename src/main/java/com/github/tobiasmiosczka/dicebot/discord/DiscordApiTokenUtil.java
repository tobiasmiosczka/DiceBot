package com.github.tobiasmiosczka.dicebot.discord;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class DiscordApiTokenUtil {

    public static String getDiscordApiToken() throws IOException {
        URL url = ClassLoader.getSystemClassLoader().getResource("discord.properties");
        if (url == null)
            return "";
        InputStream input = url.openStream();
        Properties prop = new Properties();
        prop.load(input);
        return prop.getProperty("api.token");
    }

}