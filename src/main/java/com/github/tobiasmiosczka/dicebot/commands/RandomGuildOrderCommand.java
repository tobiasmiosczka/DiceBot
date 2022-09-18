package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.random.JavaRandomNumberGenerator;
import com.github.tobiasmiosczka.dicebot.random.RandomNumberGenerator;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;

@Command(
        command = "rgo",
        description = "Returns the users of a channel in random order."
)
public class RandomGuildOrderCommand implements CommandFunction {

    private static final RandomNumberGenerator RANDOM_NUMBER_GENERATOR = new JavaRandomNumberGenerator();

    private static String getHeader(Guild guild) {
        return "Random order of " + guild.getName() + ":\n";
    }

    private static String toLine(int i, Member member) {
        return i + ": " + member.getAsMention();
    }

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT)
            return event.reply("This command can only be performed on a text channel. :L");
        List<Member> members = RANDOM_NUMBER_GENERATOR.shuffled((event.getGuild().getMembers()));
        StringBuilder sb = new StringBuilder(getHeader(event.getGuild()));
        for (int i = 0; i < members.size(); ++i)
            sb.append(toLine(i + 1, members.get(i))).append("\n");
        return event.reply(sb.toString());
    }
}
