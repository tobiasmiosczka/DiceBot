package com.github.tobiasmiosczka.dicebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.Compression;

import javax.annotation.Nonnull;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DiceBot extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "!";

    private static final DiceNotationParser diceNotationParser = new DiceNotationParser();
    private static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

    private RollStatsManager rollStatsManager;

    public DiceBot() throws LoginException, IOException {
        JDABuilder builder = new JDABuilder(ApiKeyHelper.getApiKey());

        JDA jda = builder
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.NONE)
                .setActivity(Activity.playing("Pen&Paper"))
                .addEventListeners(this)
                .build();

        rollStatsManager = new RollStatsManager();

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String text = reader.readLine();
            jda.getTextChannelById(ApiKeyHelper.getDefaultTextChannelId()).sendMessage(text).queue();
        }
    }

    private void getRollStats(User user, MessageChannel channel, Message message) {
        int[] stats = rollStatsManager.getStatsByUser(user);
        int count = 0;
        int sum = 0;
        StringBuilder rollsAsString = new StringBuilder();
        for (int i = 0; i < 20; ++i) {
            count += stats[i];
            sum += stats[i]  * (i + 1);
            rollsAsString.append(i + 1).append(": ").append(stats[i]).append("\n");
        }
        float mean = (sum * 1.0f) / (count * 1.0f);
        channel
                .sendMessage("Stats of User: " + user.getAsMention() + "\n" + rollsAsString + " Mean: " + mean)
                .queue();
        message.delete().queue();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        String input = event.getMessage().getContentRaw();
        if (!input.startsWith(COMMAND_PREFIX))
            return;

        if (input.equals(COMMAND_PREFIX + "p")) {
            probeTdc(event.getAuthor(), event.getChannel(), event.getMessage());
            return;
        }

        if (input.equals(COMMAND_PREFIX + "info") || input.equals(COMMAND_PREFIX + "help")) {
            event.getChannel()
                    .sendMessage("I am open source!\nView: https://github.com/tobiasmiosczka/DiceBot")
                    .queue();
            return;
        }

        if (input.equals(COMMAND_PREFIX + "ru")) {
            getRandomUser(event.getChannel(), event.getAuthor(), event.getMessage());
            return;
        }

        if (input.equals(COMMAND_PREFIX + "pstats")) {
            getRollStats(event.getAuthor(), event.getChannel(), event.getMessage());
            return;
        }

        if (input.startsWith(COMMAND_PREFIX + "r ")) {
            roll(event.getAuthor(), event.getChannel(), event.getMessage());
            return;
        }

        if (input.equals(COMMAND_PREFIX + "r")) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + ": Roll what?:thinking:").queue();
            return;
        }
    }

    private void getRandomUser(MessageChannel messageChannel, User author, Message message) {
        if(messageChannel.getType() != ChannelType.TEXT) {
            messageChannel.sendMessage("This command can only be performed on a text channel. :L").queue();
            return;
        }

        VoiceChannel voiceChannel = JdaHelper.getVoiceChannelWithMember(author);
        if (voiceChannel == null || voiceChannel.getGuild().getIdLong() != ((TextChannel)messageChannel).getGuild().getIdLong()) {
            messageChannel.sendMessage("You must be in a voice channel to perform this command. :L").queue();
            return;
        }

        Member randomMember = JdaHelper.getRandomMember(voiceChannel);
        if (randomMember == null) {
            messageChannel.sendMessage("VoiceChannel is Empty.").queue();
            return;
        }
        messageChannel.sendMessage("Random User: " + randomMember.getAsMention()).queue();
        message.delete().queue();
    }


    private void probeDsa5(User author, MessageChannel messageChannel, Message message) {
        Roll[] rolls = new Dice(20).roll(3);
        rollStatsManager.addToRollStats(author, rolls);

        boolean crit = ((rolls[0].getRoll() == 1 && rolls[1].getRoll() == 1) ||
                        (rolls[1].getRoll() == 1 && rolls[2].getRoll() == 1) ||
                        (rolls[0].getRoll() == 1 && rolls[2].getRoll() == 1));

        boolean miss = ((rolls[0].getRoll() == 20 && rolls[1].getRoll() == 20) ||
                        (rolls[1].getRoll() == 20 && rolls[2].getRoll() == 20) ||
                        (rolls[0].getRoll() == 20 && rolls[2].getRoll() == 20));

        messageChannel.sendMessage(
                author.getAsMention()
                + ": " + rolls[0] + rolls[1] + rolls[2]
                + (crit ? " Critical hit!:partying_face: " : "")
                + (miss ? " Critical miss!:see_no_evil: " : "")
        ).queue();
        message.delete().queue();
    }

    private void probeTdc(User author, MessageChannel messageChannel, Message message) {
        Roll[] rolls = new Dice(20).roll(2);
        rollStatsManager.addToRollStats(author, rolls);

        boolean crit = (rolls[0].getRoll() == 1 && rolls[1].getRoll() == 1);
        boolean miss = (rolls[0].getRoll() == 20 && rolls[1].getRoll() == 20);

        messageChannel.sendMessage(
                author.getAsMention()
                        + ": " + rolls[0] + rolls[1] + " = " + Roll.sum(rolls)
                        + (crit ? " Critical hit!:partying_face: " : "")
                        + (miss ? " Critical miss!:see_no_evil: " : "")
        ).queue();
        message.delete().queue();
    }

    private void roll(User author, MessageChannel messageChannel, Message message) {
        String input = message.getContentRaw()
                .replace(COMMAND_PREFIX + "r", "")
                .trim();
        try {
            String rolls = diceNotationParser.parseDiceNotation(input);
            String formula = diceNotationParser.parseRollNotation(rolls);
            String result = "" + scriptEngine.eval(formula);

            messageChannel.sendMessage(
                    "\n" + author.getAsMention() + ": `" + input + "`\n" + rolls + " = __" + result + "__"
            ).queue();
            message.delete().queue();
        } catch (Exception e) {
            messageChannel.sendMessage(
                    "\n" + author.getAsMention() + ": `" + input + "`\n" + "Sorry, something went wrong.:thinking:"
            ).queue();
            e.printStackTrace();
        }
    }
}
