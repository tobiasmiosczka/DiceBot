package com.github.tobiasmiosczka.dicebot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeyHelper {

    public static String getApiKey() throws IOException {
        InputStream input = ClassLoader.getSystemClassLoader().getResource("discord.properties").openStream();

        Properties prop = new Properties();
        prop.load(input);

        return (prop.getProperty("api.key"));
    }

}