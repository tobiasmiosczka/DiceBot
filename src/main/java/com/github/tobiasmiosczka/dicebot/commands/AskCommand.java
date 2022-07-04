package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.util.CollectionUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.randomOf;

@Command(
        command = "ask",
        description = "Ask me something!",
        arguments = {
                @Option(
                        name = "question",
                        type = OptionType.STRING,
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
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        String question = event.getOptionsByName("question").get(0).getAsString();
        String answer = CollectionUtil.randomOf(ANSWERS);
        String bot = event.getJDA().getSelfUser().getAsMention();
        String user = event.getUser().getAsMention();
        return event.reply(user + ": " + question + "\n" + bot + ": " + answer);
    }
}
