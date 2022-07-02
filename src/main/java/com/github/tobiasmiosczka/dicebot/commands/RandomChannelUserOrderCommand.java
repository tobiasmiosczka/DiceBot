package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

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
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT)
            return event.reply("This command must be performed in a text channel. :L");
        Guild guild = event.getGuild();
        VoiceChannel voiceChannel = JdaUtil.getVoiceChannelWithMember(guild, event.getUser());
        if (voiceChannel == null || voiceChannel.getGuild().getIdLong() != guild.getIdLong())
            return event.reply("You must be in a voice channel to perform this command. :L");
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
        return event.reply(sb.toString());
    }
}
