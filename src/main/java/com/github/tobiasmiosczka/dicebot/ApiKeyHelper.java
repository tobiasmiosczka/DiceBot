package com.github.tobiasmiosczka.dicebot;

import java.io.InputStream;
import java.util.Properties;

public class ApiKeyHelper {

    private static String apiKey;
    private static long defaultTextChannelId;

    static {
        try {
            InputStream input = ClassLoader.getSystemClassLoader().getResource("discord.properties").openStream();
            Properties prop = new Properties();
            prop.load(input);
            apiKey = prop.getProperty("api.key");
            defaultTextChannelId = Long.parseLong(prop.getProperty("defaultTextChannelId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static long getDefaultTextChannelId() {
        return defaultTextChannelId;
    }

}