package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.*;

import static com.github.tobiasmiosczka.dicebot.util.CollectionUtil.shuffled;

@Command(
        command = "rco",
        description = "Returns the users of a channel in random order."
)
public class RandomChannelUserOrderCommand implements CommandFunction {

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT)
            return event.reply("This command must be performed in a text channel. :L");
        Guild guild = event.getGuild();
        Optional<VoiceChannel> voiceChannel = JdaUtil.getVoiceChannelWithMember(guild, event.getUser());
        if (voiceChannel.isEmpty() || voiceChannel.get().getGuild().getIdLong() != guild.getIdLong())
            return event.reply("You must be in a voice channel to perform this command. :L");
        List<Member> members = shuffled(voiceChannel.get().getMembers());
        StringBuilder sb = new StringBuilder();
        sb
                .append("Random order of ")
                .append(voiceChannel.get().getName())
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
