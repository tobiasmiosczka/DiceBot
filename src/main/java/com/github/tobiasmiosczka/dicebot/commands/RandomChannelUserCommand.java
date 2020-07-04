package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.util.CollectionUtil;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.List;

@Command(
        command = "rcu",
        description = "Selects a random user of the voice channel."
)
public class RandomChannelUserCommand implements CommandFunction {

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (messageChannel.getType() != ChannelType.TEXT) {
            messageChannel
                    .sendMessage("This command must be performed in a text channel. :L")
                    .queue();
            return false;
        }
        Guild guild = ((TextChannel)messageChannel).getGuild();
        VoiceChannel voiceChannel = JdaUtil.getVoiceChannelWithMember(guild, author);
        if (voiceChannel == null || voiceChannel.getGuild().getIdLong() != guild.getIdLong()) {
            messageChannel
                    .sendMessage("You must be in a voice channel to perform this command. :L")
                    .queue();
            return false;
        }
        List<Member> member = voiceChannel.getMembers();
        if (member.isEmpty()) {
            messageChannel
                    .sendMessage("VoiceChannel is Empty.")
                    .queue();
            return false;
        }
        Member randomMember = CollectionUtil.getRandom(member);
        messageChannel
                .sendMessage("Random User: " + randomMember.getAsMention())
                .queue();
        return true;
    }
}
