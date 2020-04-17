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
import java.util.Arrays;

public class DiceBot extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "!";

    private static final DiceNotationParser diceNotationParser = new DiceNotationParser();
    private static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

    private RollStatsManager rollStatsManager;

    public DiceBot() throws LoginException, IOException {

        JDA jda = new JDABuilder(ApiKeyHelper.getApiKey())
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

    private boolean getRollStats(User user, MessageChannel channel) {
        int[] stats = rollStatsManager.getStatsByUser(user);
        int count = 0, sum = 0;
        StringBuilder rollsAsString = new StringBuilder();
        for (int i = 0; i < 20; ++i) {
            count += stats[i];
            sum += stats[i]  * (i + 1);
            rollsAsString.append(i + 1).append(": ").append(stats[i]).append("\n");
        }
        float mean = (sum * 1.0f) / (count * 1.0f);
        channel.sendMessage("Stats of User: " + user.getAsMention() + "\n" + rollsAsString + " Mean: " + mean).queue();
        return true;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        String input = event.getMessage().getContentRaw();
        if (!input.startsWith(COMMAND_PREFIX))
            return;

        input = input.substring(1).trim().replaceAll(" +", " ");

        String[] strings = input.split(" ");

        if (strings.length == 0)
            return;

        String command = strings[0];
        String[] args = Arrays.copyOfRange(strings, 1, strings.length);

        executeCommand(command, args, event.getAuthor(), event.getTextChannel(), event.getMessage());
    }

    private void executeCommand(String command, String[] args, User author, TextChannel channel, Message message) {
        boolean result = false;
        if (command.equals("p")) {
            result = probeTdc(author, channel);
        }

        if (command.equals("info") || command.equals("help")) {
            result = info(channel);
        }

        if (command.equals("ru")) {
            result = getRandomUser(channel, author);
        }

        if (command.equals("pstats")) {
            result = getRollStats(author, channel);
        }

        if (command.equals("r")) {
            result = roll((args.length > 0) ? args[0] : "", author, channel);
        }

        if (result) {
            message.delete().queue();
        }
    }

    private boolean info(TextChannel channel) {
        channel.sendMessage("I am open source!\nView: https://github.com/tobiasmiosczka/DiceBot").queue();
        return true;
    }

    private boolean getRandomUser(MessageChannel messageChannel, User author) {
        if(messageChannel.getType() != ChannelType.TEXT) {
            messageChannel.sendMessage("This command can only be performed on a text channel. :L").queue();
            return false;
        }

        VoiceChannel voiceChannel = JdaHelper.getVoiceChannelWithMember(author);
        if (voiceChannel == null || voiceChannel.getGuild().getIdLong() != ((TextChannel)messageChannel).getGuild().getIdLong()) {
            messageChannel.sendMessage("You must be in a voice channel to perform this command. :L").queue();
            return false;
        }

        Member randomMember = JdaHelper.getRandomMember(voiceChannel);
        if (randomMember == null) {
            messageChannel.sendMessage("VoiceChannel is Empty.").queue();
            return false;
        }
        messageChannel.sendMessage("Random User: " + randomMember.getAsMention()).queue();
        return true;
    }


    private boolean probeDsa5(User author, MessageChannel messageChannel) {
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
                + ": " + Roll.toString(rolls)
                + (crit ? " Critical hit!:partying_face: " : "")
                + (miss ? " Critical miss!:see_no_evil: " : "")).queue();
        return true;
    }

    private boolean probeTdc(User author, MessageChannel messageChannel) {
        Roll[] rolls = new Dice(20).roll(2);
        rollStatsManager.addToRollStats(author, rolls);

        boolean crit = (rolls[0].getRoll() == 1 && rolls[1].getRoll() == 1);
        boolean miss = (rolls[0].getRoll() == 20 && rolls[1].getRoll() == 20);

        messageChannel.sendMessage(
                author.getAsMention()
                        + ": " + Roll.toString(rolls) + " = " + Roll.sum(rolls)
                        + (crit ? " Critical hit!:partying_face: " : "")
                        + (miss ? " Critical miss!:see_no_evil: " : "")).queue();
        return true;
    }

    private boolean roll(String arg, User author, MessageChannel messageChannel) {
        if (arg == null || arg.equals("")) {
            messageChannel.sendMessage("Roll what?").queue();
            return false;
        }
        try {
            String rolls = diceNotationParser.parseDiceNotation(arg);
            String formula = diceNotationParser.parseRollNotation(rolls);
            String result = "" + scriptEngine.eval(formula);

            messageChannel.sendMessage("\n" + author.getAsMention() + ": `" + arg + "`\n" + rolls + " = __" + result + "__").queue();
            return true;
        } catch (Exception e) {
            messageChannel.sendMessage("\n" + author.getAsMention() + ": `" + arg + "`\n" + "Sorry, something went wrong.:thinking:").queue();
            e.printStackTrace();
            return false;
        }
    }
}
