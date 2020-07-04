package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Command(
        command = "rgo",
        description = "Returns the users of a channel in random order."
)
public class RandomGuildOrderCommand implements CommandFunction {

    private static final Random R = new Random();

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (messageChannel.getType() != ChannelType.TEXT) {
            messageChannel
                    .sendMessage("This command can only be performed on a text channel. :L")
                    .queue();
            return false;
        }
        Guild guild = ((TextChannel)messageChannel).getGuild();
        List<Member> members = new ArrayList<>((guild.getMembers()));
        Collections.shuffle(members, R);
        StringBuilder sb = new StringBuilder();
        sb
                .append("Random order of ")
                .append(guild.getName())
                .append(":");
        for (int i = 0; i < members.size(); ++i) {
            sb
                    .append("\n")
                    .append(i + 1)
                    .append(": ")
                    .append(members.get(i).getAsMention());
        }
        messageChannel
                .sendMessage(sb.toString())
                .queue();
        return true;
    }
}
