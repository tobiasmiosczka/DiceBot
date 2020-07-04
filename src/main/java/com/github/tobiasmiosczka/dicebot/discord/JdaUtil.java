package com.github.tobiasmiosczka.dicebot.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class JdaUtil {

    public static VoiceChannel getVoiceChannelWithMember(Guild guild, User user) {
        for (VoiceChannel voiceChannel : guild.getVoiceChannels())
            for (Member member : voiceChannel.getMembers())
                if (member.getUser().getIdLong() == user.getIdLong())
                    return voiceChannel;
        return null;
    }

    public static String quoted(String text) {
        return "`" + text + "`";
    }

    public static String code(String text) {
        return "```" + text + "```";
    }

    public static String underlined(String text) {
        return "__" + text + "__";
    }

    public static String crossedOut(String text) {
        return "~~" + text + "~~";
    }
}
