package com.github.tobiasmiosczka.dicebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DiceBot extends ListenerAdapter {

    private static final DiceNotationParser diceNotationParser = new DiceNotationParser();
    private static final ScriptEngineManager mgr = new ScriptEngineManager();
    private static final ScriptEngine engine = mgr.getEngineByName("JavaScript");

    private Map<Long, int[]> rollStats;

    private final JDA jda;

    public DiceBot() throws LoginException, IOException {
        JDABuilder builder = new JDABuilder(ApiKeyHelper.getApiKey());

        jda = builder
                .setBulkDeleteSplittingEnabled(false)
                .setCompression(Compression.NONE)
                .setActivity(Activity.playing("Pen&Paper"))
                .addEventListeners(this)
                .build();

        rollStats = new HashMap<>();

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String text = reader.readLine();
            jda.getTextChannelById(691976630956195906L).sendMessage(text).queue();
        }
    }

    private void addToRollStats(User user, Roll roll) {
        if (!rollStats.containsKey(user.getIdLong()))
            rollStats.put(user.getIdLong(), new int[20]);
        ++rollStats.get(user.getIdLong())[roll.getRoll()-1];
    }

    private void getRollStats(User user, MessageChannel channel) {
        if (!rollStats.containsKey(user.getIdLong()))
            rollStats.put(user.getIdLong(), new int[20]);
        int[] stats = rollStats.get(user.getIdLong());
        int count = 0;
        int sum = 0;
        StringBuilder rollsAsString = new StringBuilder();
        for(int i = 0; i < 20; ++i) {
            count += stats[i];
            sum += stats[i]  * (i + 1);
            rollsAsString.append(i + 1).append(": ").append(stats[i]).append("\n");
        }
        float mean = (sum * 1.0f) / (count * 1.0f);
        channel.sendMessage("Stats of User: " + user.getAsMention() + "\n" + rollsAsString + " Mean: " + mean).queue();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        System.out.println(event.getMessage().getContentRaw());
        String input = event.getMessage().getContentRaw();
        if (input.startsWith("!r "))
            roll(event.getAuthor(), event.getChannel(), event.getMessage());

        if (input.equals("!p"))
            p(event.getAuthor(), event.getChannel());

        if (input.equals("!info"))
            event.getChannel().sendMessage("https://github.com/tobiasmiosczka/DiceBot").queue();

        if (input.equals("!ru"))
           getRandomUser(event.getChannel(), event.getAuthor());

        if (input.equals("!pstats"))
            getRollStats(event.getAuthor(), event.getChannel());

    }

    private void getRandomUser(MessageChannel messageChannel, User author) {
        if(messageChannel.getType() != ChannelType.TEXT) {
            messageChannel.sendMessage("This command can only be performed on a text channel. :L").queue();
            return;
        }
        for (Guild guild : jda.getGuilds()) {
            for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                if (voiceChannel.getMembers().stream().anyMatch(m -> m.getUser().getIdLong() == author.getIdLong())) {
                    Random r = new Random();
                    Member randomMember = voiceChannel.getMembers().get(r.nextInt(voiceChannel.getMembers().size()));
                    if (randomMember.getGuild().getIdLong() == ((TextChannel) messageChannel).getGuild().getIdLong()) {
                        messageChannel.sendMessage("Random User: " + randomMember.getAsMention()).queue();
                        return;
                    }
                }
            }
        }
        messageChannel.sendMessage("You must be in a voice channel to perform this command. :L").queue();
    }


    private void p(User author, MessageChannel messageChannel) {
        Roll[] rolls = new Dice(20).roll(3);
        Arrays.stream(rolls).forEach(roll -> addToRollStats(author, roll));

        boolean crit = ((rolls[0].getRoll() == 1 && rolls[1].getRoll() == 1) ||
                        (rolls[1].getRoll() == 1 && rolls[2].getRoll() == 1) ||
                        (rolls[0].getRoll() == 1 && rolls[2].getRoll() == 1));

        boolean miss = ((rolls[0].getRoll() == 20 && rolls[1].getRoll() == 20) ||
                        (rolls[1].getRoll() == 20 && rolls[2].getRoll() == 20) ||
                        (rolls[0].getRoll() == 20 && rolls[2].getRoll() == 20));

        messageChannel.sendMessage(
                author.getAsMention()
                + ": " + rolls[0] + rolls[1] + rolls[2]
                + (crit ? " Critical hit, you're the best! :)" : "")
                + (miss ? " Critical miss, oops... :(" : "")
        ).queue();
    }


    private void roll(User author, MessageChannel messageChannel, Message message) {
        String input = message.getContentRaw()
                .replace("!r", "")
                .trim();
        if (input.isEmpty()) {
            messageChannel.sendMessage("Roll what?").queue();
            return;
        }
        try {
            String rolls = diceNotationParser.parseDiceNotation(input);
            String formula = diceNotationParser.parseRollNotation(rolls);
            String result = "" + engine.eval(formula);

            messageChannel.sendMessage(
                    "\n"
                            + author.getAsMention() + ": `" + input + "`\n"
                            + rolls + " = __" + result + "__"
            ).queue();

        } catch (Exception e) {
            messageChannel.sendMessage(
                    "\n"
                            + author.getAsMention() + ": `" + input + "`\n"
                            + "Sorry, something went wrong. :("
            ).queue();
            e.printStackTrace();
        }
    }
}
