package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.random.JavaRandomNumberGenerator;
import com.github.tobiasmiosczka.dicebot.random.RandomNumberGenerator;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;

@Command(
        command = "rgu",
        description = "Selects a random member of the guild."
)
public class RandomGuildUserCommand implements CommandFunction {

    private static final RandomNumberGenerator RANDOM_NUMBER_GENERATOR = new JavaRandomNumberGenerator();

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT)
            return event.reply("Command must be performed in a text channel.");
        List<Member> memberList = event.getGuild().getMembers();
        if (memberList.isEmpty())
            return event.reply("Guild is Empty.");
        Member randomMember = RANDOM_NUMBER_GENERATOR.randomOf(memberList);
        return event.reply("Random Member: " + randomMember.getAsMention());
    }
}
