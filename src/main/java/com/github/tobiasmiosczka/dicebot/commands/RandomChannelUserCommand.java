package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.JdaUtil;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.util.CollectionUtil;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;

@Command(
        command = "rcu",
        description = "Selects a random user of the voice channel."
)
public class RandomChannelUserCommand implements CommandFunction {

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT) {
            return event.reply("This command must be performed in a text channel. :L");
        }
        Guild guild = event.getGuild();
        VoiceChannel voiceChannel = JdaUtil.getVoiceChannelWithMember(guild, event.getUser());
        if (voiceChannel == null || voiceChannel.getGuild().getIdLong() != guild.getIdLong()) {
            return event.reply("You must be in a voice channel to perform this command. :L");
        }
        List<Member> member = voiceChannel.getMembers();
        if (member.isEmpty()) {
            return event.reply("VoiceChannel is Empty.");
        }
        Member randomMember = CollectionUtil.getRandom(member);
        return event.reply("Random User: " + randomMember.getAsMention());
    }
}
