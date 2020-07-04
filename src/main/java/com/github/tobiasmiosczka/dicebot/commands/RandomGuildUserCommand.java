package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.util.CollectionUtil;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

@Command(
        command = "rgu",
        description = "Selects a random member of the guild."
)
public class RandomGuildUserCommand implements CommandFunction {

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (messageChannel.getType() != ChannelType.TEXT) {
            messageChannel
                    .sendMessage("Command must be performed in a text channel.")
                    .queue();
            return false;
        }
        List<Member> memberList = ((TextChannel)messageChannel).getGuild().getMembers();
        if (memberList.isEmpty()) {
            messageChannel
                    .sendMessage("Guild is Empty.")
                    .queue();
            return false;
        }
        Member randomMember = CollectionUtil.getRandom(memberList);
        messageChannel
                .sendMessage("Random Member: " + randomMember.getAsMention())
                .queue();
        return true;
    }
}
