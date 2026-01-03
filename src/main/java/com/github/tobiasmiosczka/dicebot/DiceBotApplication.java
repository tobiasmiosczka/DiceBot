package com.github.tobiasmiosczka.dicebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiceBotApplication {

    private static final String BANNER = """
              _____  _          ____        _  \s
             |  __ \\(_)        |  _ \\      | | \s
             | |  | |_  ___ ___| |_) | ___ | |_\s
             | |  | | |/ __/ _ \\  _ < / _ \\| __|
             | |__| | | (_|  __/ |_) | (_) | |_\s
             |_____/|_|\\___\\___|____/ \\___/ \\__|
            """;

    public static void main(String[] args) {
        System.out.println(BANNER);
        SpringApplication.run(DiceBotApplication.class, args);
    }
}
