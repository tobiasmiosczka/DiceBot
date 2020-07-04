package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Command(command = "t")
public class TimerCommand implements CommandFunction {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private ConcurrentMap<Message, Long> messages;
    private long couter;

    public TimerCommand() {
        couter = 0;
        messages = new ConcurrentHashMap<>();
        SCHEDULER.scheduleAtFixedRate(() -> {
            ++couter;
            for (Map.Entry<Message, Long> entry : messages.entrySet()) {
                Message message = entry.getKey();
                long secondsLeft = (entry.getValue() - couter);
                if (secondsLeft <= 0) {
                    messages.remove(message);
                }
                message.editMessage(buildMessage(secondsLeft)).queue();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private MessageEmbed buildMessage(long secondsLeft) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Timer")
                .addField("Time left", secondsLeft + "s.", false);
        return embedBuilder.build();
    }

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        int timeInSeconds;
        try {
            timeInSeconds = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            messageChannel
                    .sendMessage("First parameter must be the time to vote in seconds.")
                    .queue();
            return false;
        }
        Message message = messageChannel.sendMessage(buildMessage(timeInSeconds)).complete();
        messages.put(message, couter + timeInSeconds);
        return true;
    }
}
