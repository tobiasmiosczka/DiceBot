package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;
import java.util.Optional;

import static com.github.tobiasmiosczka.dicebot.discord.JdaUtil.getVoiceChannelWithMember;
import static com.github.tobiasmiosczka.dicebot.util.VoteUtil.*;

@Command(
        command = "vcu",
        description = "Starts a voting about users of a voice channel.",
        options = {
                @Option(
                        name = "time",
                        type = OptionType.INTEGER,
                        description = "The duration of the voting in seconds."
                )
        }
)
public class VoteChannelUserCommand implements CommandFunction {

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        int timeInSeconds = event.getOption("time").getAsInt();
        if (timeInSeconds < 10)
            return event.reply("Time to vote must be at least 10 seconds.");
        if (timeInSeconds > 60 * 60 * 24)
            return event.reply("Time to vote must be less than 24 hours.");
        if (event.getChannel().getType() != ChannelType.TEXT)
            return event.reply("This command must be performed in a text channel. :L");
        Guild guild = event.getGuild();
        Optional<VoiceChannel> voiceChannel = getVoiceChannelWithMember(guild, event.getUser());
        if (voiceChannel.isEmpty() || voiceChannel.get().getGuild().getIdLong() != guild.getIdLong())
            return event.reply("You must be in a voice channel to perform this command. :L");
        List<Member> member = voiceChannel.get().getMembers();
        if (member.isEmpty())
            return event.reply("VoiceChannel is Empty.");
        performVote(event.getMessageChannel(), member, IMentionable::getAsMention, timeInSeconds);
        return event.reply("Ok");
    }
}