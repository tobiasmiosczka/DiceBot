package com.github.tobiasmiosczka.dicebot.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.Optional;

public class JdaUtil {

    public static Optional<VoiceChannel> getVoiceChannelWithMember(Guild guild, User user) {
        for (VoiceChannel voiceChannel : guild.getVoiceChannels())
            for (Member member : voiceChannel.getMembers())
                if (member.getUser().getIdLong() == user.getIdLong())
                    return Optional.of(voiceChannel);
        return Optional.empty();
    }

    public static String quoted(String text) {
        return "`%s`".formatted(text);
    }

    public static String code(String text) {
        return "```%s```".formatted(text);
    }

    public static String underlined(String text) {
        return "__%s__".formatted(text);
    }

    public static String crossedOut(String text) {
        return "~~%s~~".formatted(text);
    }
}
