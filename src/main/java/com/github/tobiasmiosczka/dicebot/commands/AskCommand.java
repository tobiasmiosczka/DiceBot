package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Argument;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.util.CollectionUtil;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

@Command(
        command = "ask",
        description = "Ask me something!",
        arguments = {
                @Argument(
                        name = "question",
                        description = "The question to ask."
                )
        })
public class AskCommand implements CommandFunction {

    private static final String[] ANSWERS = {
            "Maybe someday.",
            "Nothing.",
            "I don't think so.",
            "No.",
            "Yes.",
            "Try asking again.",
            "It is certain.",
            "It is decidedly so.",
            "Without a doubt.",
            "Yes – definitely.",
            "You may rely on it.",
            "As I see it, yes.",
            "Most likely.",
            "Outlook good.",
            "Signs point to yes.",
            "Reply hazy, try again.",
            "Ask again later.",
            "Better not tell you now.",
            "Cannot predict now.",
            "Concentrate and ask again.",
            "Don't count on it.",
            "My reply is no.",
            "My sources say no.",
            "Outlook not so good.",
            "Very doubtful."
    };

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        String question = arg.isEmpty() ? " " : arg;
        String answer = CollectionUtil.getRandom(ANSWERS);
        User bot = messageChannel.getJDA().getSelfUser();
        messageChannel
                .sendMessage(author.getAsMention() + ": " + question + "\n" + bot.getAsMention() + ": " + answer)
                .queue();
        return true;
    }
}
