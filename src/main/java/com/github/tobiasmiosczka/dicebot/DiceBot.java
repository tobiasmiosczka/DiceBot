package com.github.tobiasmiosczka.dicebot;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandEngine;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.separatedBy;

@Component
public class DiceBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiceBot.class);

    public DiceBot(CommandEngine commandEngine, JDA jda) {
        jda.addEventListener(commandEngine);
        printInfo(jda);
    }

    private void printInfo(JDA jda) {
        LOGGER.info("Bot running on:\n{}", jda.getGuilds().stream()
                .map(g -> "  -" + g.getName())
                .reduce(separatedBy("\n")).orElse(""));
    }

}
