package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

@Command(command = "ru")
public class RandomUserCommand implements CommandFunction {

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if(messageChannel.getType() != ChannelType.TEXT) {
            messageChannel
                    .sendMessage("This command can only be performed on a text channel. :L")
                    .queue();
            return false;
        }

        VoiceChannel voiceChannel = JdaUtil.getVoiceChannelWithMember(author);
        if (voiceChannel == null || voiceChannel.getGuild().getIdLong() != ((TextChannel)messageChannel).getGuild().getIdLong()) {
            messageChannel
                    .sendMessage("You must be in a voice channel to perform this command. :L")
                    .queue();
            return false;
        }

        Member randomMember = JdaUtil.getRandomMember(voiceChannel);
        if (randomMember == null) {
            messageChannel
                    .sendMessage("VoiceChannel is Empty.")
                    .queue();
            return false;
        }
        messageChannel
                .sendMessage("Random User: " + randomMember.getAsMention())
                .queue();
        return true;
    }
}
