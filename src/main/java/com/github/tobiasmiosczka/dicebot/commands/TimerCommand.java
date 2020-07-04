package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
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

@Command(
        command = "t",
        description = "WORK IN PROGRESS"
)
public class TimerCommand implements CommandFunction {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private ConcurrentMap<Message, Long> messages;
    private long counter;

    public TimerCommand() {
        counter = 0;
        messages = new ConcurrentHashMap<>();
        SCHEDULER.scheduleAtFixedRate(() -> {
            ++counter;
            for (Map.Entry<Message, Long> entry : messages.entrySet()) {
                Message message = entry.getKey();
                long secondsLeft = (entry.getValue() - counter);
                message.editMessage(buildMessage(secondsLeft)).queue();
                if (secondsLeft <= 0) {
                    messages.remove(message);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private MessageEmbed buildMessage(long secondsLeft) {
        secondsLeft = Math.max(secondsLeft, 0);
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Timer" + ((secondsLeft == 0) ? "(Over)" : ""))
                .setDescription(secondsLeft + "s.");
        if (secondsLeft > 0)
            builder.setColor(0x00CC00);
        return  builder.build();
    }

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        try {
            int timeInSeconds = Integer.parseInt(arg);
            Message message = messageChannel.sendMessage(buildMessage(timeInSeconds)).complete();
            messages.put(message, counter + timeInSeconds);
            return true;
        } catch (NumberFormatException e) {
            messageChannel
                    .sendMessage("First parameter must be the time to vote in seconds.")
                    .queue();
            return false;
        }
    }
}
