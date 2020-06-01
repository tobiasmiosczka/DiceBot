package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

@Command(command = "rgu", helpText = "Select a random member of the guild.")
public class RandomGuildUserCommand implements CommandFunction {

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (messageChannel.getType() != ChannelType.TEXT) {
            messageChannel.sendMessage("Command must be performed in a text channel.").queue();
            return false;
        }
        Member randomMember = JdaUtil.getRandomMember(((TextChannel)messageChannel).getGuild());
        if (randomMember == null) {
            messageChannel
                    .sendMessage("Guild is Empty.")
                    .queue();
            return false;
        }
        messageChannel.sendMessage("Random Member: " + randomMember.getAsMention()).queue();
        return true;
    }
}
