package com.github.tobiasmiosczka.dicebot.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Random;

public class JdaUtil {

    private static final Random R = new Random();

    public static VoiceChannel getVoiceChannelWithMember(User user) {
        for (Guild guild : user.getMutualGuilds()) {
            for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                if (voiceChannel.getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == user.getIdLong()))
                    return voiceChannel;
            }
        }
        return null;
    }

    public static Member getRandomMember(Guild guild) {
        int size = guild.getMembers().size();
        if (size == 0)
            return null;
        return guild.getMembers().get(R.nextInt(size));
    }

    public static Member getRandomMember(VoiceChannel voiceChannel) {
        int size = voiceChannel.getMembers().size();
        if (size == 0)
            return null;
        return voiceChannel.getMembers().get(R.nextInt(size));
    }
}
