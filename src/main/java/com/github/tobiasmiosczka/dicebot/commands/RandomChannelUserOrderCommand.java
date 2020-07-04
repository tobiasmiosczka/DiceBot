package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Command(
        command = "rco",
        description = "Returns the users of a channel in random order."
)
public class RandomChannelUserOrderCommand implements CommandFunction {

    private static final Random R = new Random();

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
        if (voiceChannel == null || voiceChannel.getGuild().getIdLong() != ((TextChannel)messageChannel).getGuild().getIdLong()) {
            messageChannel
                    .sendMessage("You must be in a voice channel to perform this command. :L")
                    .queue();
            return false;
        }
        List<Member> members = new ArrayList<>(voiceChannel.getMembers());
        Collections.shuffle(members, R);
        StringBuilder sb = new StringBuilder();
        sb
                .append("Random order of ")
                .append(voiceChannel.getName())
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
